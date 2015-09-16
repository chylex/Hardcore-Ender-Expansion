package chylex.hee.tileentity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import chylex.hee.mechanics.energy.EnergyValues;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.system.util.CollectionUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class TileEntityAbstractEnergyInventory extends TileEntityAbstractInventory{
	protected static final byte[] chunkOffX = new byte[]{ -1, -1, -1, 0, 0, 0, 1, 1, 1 },
								  chunkOffZ = new byte[]{ -1, 0, 1, -1, 0, 1, -1, 0, 1 };
	
	private byte drainTimer;
	private int unitsLeft = -1;
	private boolean draining;
	
	private boolean hasInsufficientEnergy;
	
	protected abstract byte getDrainTimer();
	protected abstract int getDrainUnits();
	protected abstract boolean isWorking();
	protected abstract void onWork();
	
	@Override
	public void updateEntity(){
		super.updateEntity();
		
		if (worldObj.isRemote)return;
		
		if (isWorking()){
			draining = true;
			
			if (unitsLeft == 0){
				onWork();
				
				if (hasInsufficientEnergy){
					hasInsufficientEnergy = false;
					worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
				}
			}
			else if (!hasInsufficientEnergy && unitsLeft != -1){
				hasInsufficientEnergy = true;
				worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
			}
		}
		else{
			draining = hasInsufficientEnergy = false;
			unitsLeft = -1;
		}
		
		if (draining && (drainTimer == 0 || --drainTimer == 0)){
			Stopwatch.timeAverage("TileEntityAbstractEnergyInventory - drain",1000);
			
			int drain = unitsLeft < 0 ? getDrainUnits() : unitsLeft;
			int chunkX = xCoord>>4, chunkZ = zCoord>>4;
			
			if (drain > EnergyValues.min && worldObj.checkChunksExist(chunkX-1,0,chunkZ-1,chunkX+1,0,chunkZ+1)){
				List<TileEntityEnergyCluster> clusters = new ArrayList<>();
				
				for(int a = 0; a < 9; a++){
					final int cx = chunkX*16+chunkOffX[a]*16, cz = chunkZ*16+chunkOffZ[a]*16;
					
					((Map<ChunkPosition,TileEntity>)worldObj.getChunkFromChunkCoords(chunkX+chunkOffX[a],chunkZ+chunkOffZ[a]).chunkTileEntityMap).entrySet()
					.stream()
					.filter(entry -> entry.getValue().getClass() == TileEntityEnergyCluster.class && Pos.at(cx+entry.getKey().chunkPosX,entry.getKey().chunkPosY,cz+entry.getKey().chunkPosZ).distance(this) <= 16D)
					.map(entry -> (TileEntityEnergyCluster)entry.getValue())
					.forEach(cluster -> clusters.add(cluster));
				}
				
				for(TileEntityEnergyCluster cluster:CollectionUtil.shuffleMe(clusters,worldObj.rand)){
					if ((drain = cluster.tryDrainEnergy(drain,this)) == 0)break;
				}
			}
			
			drainTimer = getDrainTimer();
			unitsLeft = drain;
			
			Stopwatch.finish("TileEntityAbstractEnergyInventory - drain");
		}
	}
	
	@Override
	public Packet getDescriptionPacket(){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("engInsuf",hasInsufficientEnergy);
		return new S35PacketUpdateTileEntity(xCoord,yCoord,zCoord,0,nbt);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet){
		hasInsufficientEnergy = packet.func_148857_g().getBoolean("engInsuf"); // OBFUSCATED get tag data
		worldObj.markBlockRangeForRenderUpdate(xCoord,yCoord,zCoord,xCoord,yCoord,zCoord);
	}
	
	@SideOnly(Side.CLIENT)
	public final boolean hasInsufficientEnergyClient(){
		return hasInsufficientEnergy;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setByte("drainTim",drainTimer);
		nbt.setInteger("unitsLeft",unitsLeft);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		drainTimer = nbt.getByte("drainTim");
		unitsLeft = nbt.getInteger("unitsLeft");
	}
}

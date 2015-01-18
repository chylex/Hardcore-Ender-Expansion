package chylex.hee.tileentity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.mechanics.energy.EnergyChunkData;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C10ParticleEnergyTransfer;
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.EnergySavefile;
import chylex.hee.system.util.MathUtil;

public abstract class TileEntityAbstractEnergyInventory extends TileEntityAbstractInventory{
	protected static final byte[] chunkOffX = new byte[]{ -1, -1, -1, 0, 0, 0, 1, 1, 1 },
								  chunkOffZ = new byte[]{ -1, 0, 1, -1, 0, 1, -1, 0, 1 };
	
	private byte drainTimer;
	private float energyLeft = -1F;
	private boolean draining;
	
	private boolean hasInsufficientEnergy;
	
	protected abstract byte getDrainTimer();
	protected abstract float getDrainAmount();
	protected abstract boolean isWorking();
	protected abstract void onWork();
	
	@Override
	public void updateEntity(){
		super.updateEntity();
		
		if (worldObj.isRemote)return;
		
		if (isWorking()){
			draining = true;
			
			if (MathUtil.floatEquals(energyLeft,0F)){
				onWork();
				
				if (hasInsufficientEnergy){
					hasInsufficientEnergy = false;
					worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
				}
			}
			else if (!hasInsufficientEnergy && !MathUtil.floatEquals(energyLeft,-1F)){
				hasInsufficientEnergy = true;
				worldObj.markBlockForUpdate(xCoord,yCoord,zCoord);
			}
		}
		else{
			draining = hasInsufficientEnergy = false;
			energyLeft = -1F;
		}
		
		if (draining && (drainTimer == 0 || --drainTimer == 0)){
			Stopwatch.timeAverage("TileEntityAbstractEnergyInventory - drain",1000);
			
			float drain = energyLeft <= 0F ? getDrainAmount() : energyLeft;
			
			if (worldObj.provider.getDimensionId() == 1){
				float newDrain = WorldDataHandler.<EnergySavefile>get(EnergySavefile.class).getFromBlockCoords(worldObj,xCoord,zCoord,true).drainEnergy(drain);
				if (!MathUtil.floatEquals(newDrain,drain))PacketPipeline.sendToAllAround(this,64D,new C10ParticleEnergyTransfer(this,xCoord+0.5D,yCoord+96D,zCoord+0.5D,(byte)80,(byte)80,(byte)80));
				drain = newDrain;
			}
			
			if (drain > EnergyChunkData.minSignificantEnergy){
				List<TileEntityEnergyCluster> clusters = new ArrayList<>();
				int chunkX = xCoord>>4, chunkZ = zCoord>>4, cx, cz;
				
				for(int a = 0; a < 9; a++){
					Map<ChunkPosition,TileEntity> tiles = worldObj.getChunkFromChunkCoords(chunkX+chunkOffX[a],chunkZ+chunkOffZ[a]).chunkTileEntityMap;
					cx = chunkX*16+chunkOffX[a]*16;
					cz = chunkZ*16+chunkOffZ[a]*16;
					
					for(Entry<ChunkPosition,TileEntity> entry:tiles.entrySet()){
						ChunkPosition pos = entry.getKey();
						
						if (entry.getValue().getClass() == TileEntityEnergyCluster.class && MathUtil.distance(cx+pos.chunkPosX-xCoord,pos.chunkPosY-yCoord,cz+pos.chunkPosZ-zCoord) <= 16D){
							clusters.add((TileEntityEnergyCluster)entry.getValue());
						}
					}
				}
				
				if (!clusters.isEmpty()){
					Collections.shuffle(clusters,worldObj.rand);
					
					for(Iterator<TileEntityEnergyCluster> iter = clusters.iterator(); iter.hasNext();){
						if ((drain = iter.next().drainEnergy(drain,this)) < EnergyChunkData.minSignificantEnergy)break;
					}
				}
			}
			
			drainTimer = getDrainTimer();
			
			if (drain < EnergyChunkData.minSignificantEnergy)energyLeft = 0F;
			else energyLeft = drain;
			
			Stopwatch.finish("TileEntityAbstractEnergyInventory - drain");
		}
	}
	
	@Override
	public Packet getDescriptionPacket(){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("engInsuf",hasInsufficientEnergy);
		return new S35PacketUpdateTileEntity(pos,0,nbt);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet){
		hasInsufficientEnergy = packet.getNbtCompound().getBoolean("engInsuf");
		worldObj.markBlockRangeForRenderUpdate(pos,pos);
	}
	
	@SideOnly(Side.CLIENT)
	public final boolean hasInsufficientEnergy(){
		return hasInsufficientEnergy;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setByte("drainTim",drainTimer);
		nbt.setFloat("engLeft",energyLeft);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		drainTimer = nbt.getByte("drainTim");
		energyLeft = nbt.getFloat("engLeft");
	}
}

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
import chylex.hee.mechanics.energy.IEnergyBlock;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.collections.CollectionUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class TileEntityAbstractEnergyInventory extends TileEntityAbstractInventory implements IEnergyBlock{
	private int unitsLeft = -1;
	private byte drainTimer;
	private boolean hasInsufficientEnergy;
	
	@Override
	public int getEnergyDrained(){
		return 1;
	}
	
	@Override
	public byte getDrainTimer(){
		return 10;
	}
	
	@Override
	public void updateEntity(){
		super.updateEntity();
		
		if (worldObj.isRemote)return;
		
		if (!isDraining()){
			hasInsufficientEnergy = false;
			unitsLeft = -1;
		}
		else{
			if (unitsLeft == 0){
				onWork();
				
				if (hasInsufficientEnergy){
					hasInsufficientEnergy = false;
					worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				}
			}
			else if (!hasInsufficientEnergy && unitsLeft != -1){
				hasInsufficientEnergy = true;
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
			
			if (drainTimer == 0 || --drainTimer == 0){
				int left = unitsLeft <= 0 ? getEnergyDrained() : unitsLeft;
				int chunkX = xCoord>>4, chunkZ = zCoord>>4;
				
				if (left > 0 && worldObj.checkChunksExist(chunkX-1, 0, chunkZ-1, chunkX+1, 0, chunkZ+1)){
					final Pos me = Pos.at(this);
					final List<TileEntityEnergyCluster> clusters = new ArrayList<>();
					
					Pos.forEachBlock(Pos.at(-1, 0, -1), Pos.at(1, 0, 1), offset -> {
						((Map<ChunkPosition, TileEntity>)worldObj.getChunkFromChunkCoords(chunkX+offset.getX(), chunkZ+offset.getZ()).chunkTileEntityMap).entrySet()
						.stream()
						.filter(entry -> entry.getValue().getClass() == TileEntityEnergyCluster.class && me.distance(entry.getValue()) <= 16D)
						.map(entry -> (TileEntityEnergyCluster)entry.getValue())
						.forEach(clusters::add);
					});
					
					for(TileEntityEnergyCluster cluster:CollectionUtil.shuffled(clusters, worldObj.rand)){
						if ((left = cluster.tryDrainEnergy(left, this)) == 0)break;
					}
				}
				
				drainTimer = getDrainTimer();
				unitsLeft = left;
			}
		}
	}
	
	@Override
	public Packet getDescriptionPacket(){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("hie", hasInsufficientEnergy);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet){
		hasInsufficientEnergy = packet.func_148857_g().getBoolean("hie"); // OBFUSCATED get tag data
		worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord, yCoord, zCoord);
	}
	
	@SideOnly(Side.CLIENT)
	public final boolean hasInsufficientEnergyClient(){
		return hasInsufficientEnergy;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setByte("drainTim", drainTimer);
		nbt.setInteger("unitsLeft", unitsLeft);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		drainTimer = nbt.getByte("drainTim");
		unitsLeft = nbt.getInteger("unitsLeft");
	}
}

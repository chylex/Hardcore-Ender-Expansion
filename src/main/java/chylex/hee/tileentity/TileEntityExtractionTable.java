package chylex.hee.tileentity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.init.BlockList;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.energy.EnergyChunkData;
import chylex.hee.mechanics.energy.EnergyValues;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.EnergySavefile;
import chylex.hee.system.util.BlockPosM;
import chylex.hee.system.util.MathUtil;

public class TileEntityExtractionTable extends TileEntityAbstractTable{
	private static final int[] slotsTop = new int[]{ 0 }, slotsSides = new int[]{ 1, 2 };
	private static final float maxStoredEnergy = EnergyChunkData.energyDrainUnit*15F;

	private byte leakTimer = 100;
	
	@Override
	protected float getDrainAmount(){
		return time < totalTime ? EnergyChunkData.energyDrainUnit*0.25F : 0F;
	}
	
	@Override
	public void updateEntity(){
		super.updateEntity();
		
		if (!worldObj.isRemote && leakTimer < 0 || (leakTimer -= (items[2] == null || items[2].getItem() != ItemList.instability_orb ? 16 : Math.max(0,16-items[2].stackSize))) < 0){
			leakTimer = 100;
			
			if (storedEnergy >= EnergyChunkData.minSignificantEnergy){
				float release = EnergyChunkData.energyDrainUnit*0.08F+(float)Math.sqrt(storedEnergy)*0.005F;
				storedEnergy = Math.max(storedEnergy-release,0F);
				
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
					Collections.shuffle(clusters);
					
					for(Iterator<TileEntityEnergyCluster> iter = clusters.iterator(); iter.hasNext();){
						if ((release = iter.next().addEnergy(release,this)) < EnergyChunkData.minSignificantEnergy)break;
					}
				}
				
				if (release >= EnergyChunkData.minSignificantEnergy && worldObj.provider.dimensionId == 1){
					release = WorldDataHandler.<EnergySavefile>get(EnergySavefile.class).getFromBlockCoords(worldObj,xCoord,zCoord,true).addEnergy(release);
				}
				
				if (release >= EnergyChunkData.minSignificantEnergy){
					BlockPosM tmpPos = BlockPosM.tmp();
					
					for(int attempt = 0, placed = 0; attempt < 8 && placed < 4; attempt++){
						tmpPos.set(xCoord+worldObj.rand.nextInt(7)-3,yCoord+worldObj.rand.nextInt(7)-3,zCoord+worldObj.rand.nextInt(7)-3);
						
						if (tmpPos.isAir(worldObj)){
							tmpPos.setBlock(worldObj,BlockList.corrupted_energy_low,3+MathUtil.floor(release*4.5F));
							++placed;
						}
					}
				}
			}
		}
	}
	
	@Override
	public void invalidateInventory(){
		if (worldObj != null && worldObj.isRemote)return;
		if (requiredStardust == 0 || items[0] == null)resetTable();
		
		if (items[0] != null){
			float energy = EnergyValues.getItemEnergy(items[0]);
			
			if (energy > 0F){
				requiredStardust = (byte)(1+1.5F*Math.sqrt(energy*4F/EnergyChunkData.energyDrainUnit));
				timeStep = (short)Math.max(1,20-(requiredStardust>>1));
				updateComparatorStatus();
			}
		}
	}

	@Override
	protected boolean onWorkFinished(){
		float energy = EnergyValues.getItemEnergy(items[0]);
		if (storedEnergy+energy > maxStoredEnergy)return false;
		
		storedEnergy += energy;
		if ((items[0].stackSize -= 1) <= 0)items[0] = null;
		if ((items[1].stackSize -= requiredStardust) <= 0)items[1] = null;
		
		return true;
	}
	
	@Override
	public float getMaxStoredEnergy(){
		return maxStoredEnergy;
	}
	
	@Override
	public int getHoldingStardust(){
		return items[1] == null ? 0 : items[1].stackSize;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setByte("leakTimer",leakTimer);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		leakTimer = nbt.getByte("leakTimer");
		invalidateInventory();
	}

	@Override
	public int getSizeInventory(){
		return 3;
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack is){
		super.setInventorySlotContents(slot,is);
		if (slot == 0)invalidateInventory();
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is){
		return slot == 1 ? is.getItem() == ItemList.stardust : slot == 2 ? is.getItem() == ItemList.instability_orb : true;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side){
		return side == 0 ? ArrayUtils.EMPTY_INT_ARRAY : side == 1 ? slotsTop : slotsSides;
	}

	@Override
	protected String getContainerDefaultName(){
		return "container.extractionTable";
	}
}

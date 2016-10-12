package chylex.hee.tileentity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.energy.EnergyValues;
import chylex.hee.tileentity.base.TileEntityAbstractTable;

public class TileEntityExtractionTable extends TileEntityAbstractTable{
	public static final int slotStardust = 0, slotOrb = 1, slotSubject = 2;
	
	private static final int[] slotsTop = new int[]{ slotSubject },
	                           slotsSides = new int[]{ slotStardust, slotOrb };
	
	private static final float maxStoredEnergy = EnergyValues.unit*15F;

	private byte leakTimer = 100;
	
	@Override
	public void updateEntity(){
		super.updateEntity();
		
		if (!worldObj.isRemote && leakTimer < 0 || (leakTimer -= (items[slotOrb] == null || items[slotOrb].getItem() != ItemList.instability_orb ? 16 : Math.max(0, 16-items[slotOrb].stackSize))) < 0){
			leakTimer = 100;
			
			/* TODO if (storedEnergy >= EnergyValues.min){
				float release = EnergyValues.unit*0.08F+(float)Math.sqrt(storedEnergy)*0.005F;
				storedEnergy = Math.max(storedEnergy-release, 0F);
				
				List<TileEntityEnergyCluster> clusters = new ArrayList<>();
				int chunkX = xCoord>>4, chunkZ = zCoord>>4, cx, cz;
				
				for(int a = 0; a < 9; a++){
					Map<ChunkPosition, TileEntity> tiles = worldObj.getChunkFromChunkCoords(chunkX+chunkOffX[a], chunkZ+chunkOffZ[a]).chunkTileEntityMap;
					cx = chunkX*16+chunkOffX[a]*16;
					cz = chunkZ*16+chunkOffZ[a]*16;
					
					for(Entry<ChunkPosition, TileEntity> entry:tiles.entrySet()){
						ChunkPosition pos = entry.getKey();
						
						if (entry.getValue().getClass() == TileEntityEnergyCluster.class && MathUtil.distance(cx+pos.chunkPosX-xCoord, pos.chunkPosY-yCoord, cz+pos.chunkPosZ-zCoord) <= 16D){
							clusters.add((TileEntityEnergyCluster)entry.getValue());
						}
					}
				}
				
				if (!clusters.isEmpty()){
					Collections.shuffle(clusters);
					
					for(Iterator<TileEntityEnergyCluster> iter = clusters.iterator(); iter.hasNext();){
						if ((release = iter.next().addEnergy(release, this)) < EnergyValues.min)break;
					}
				}
				
				if (release >= EnergyValues.min){
					BlockPosM tmpPos = BlockPosM.tmp();
					
					for(int attempt = 0, placed = 0; attempt < 8 && placed < 4; attempt++){
						tmpPos.set(xCoord+worldObj.rand.nextInt(7)-3, yCoord+worldObj.rand.nextInt(7)-3, zCoord+worldObj.rand.nextInt(7)-3);
						
						if (tmpPos.isAir(worldObj)){
							tmpPos.setBlock(worldObj, BlockList.corrupted_energy_low, 3+MathUtil.floor(release*4.5F));
							++placed;
						}
					}
				}
			}*/
		}
	}
	
	@Override
	public void invalidateInventory(){
		if (worldObj != null && worldObj.isRemote)return;
		if (requiredStardust == 0 || items[slotSubject] == null)resetTable();
		
		if (items[slotSubject] != null){
			float energy = EnergyValues.getItemEnergy(items[slotSubject]);
			
			if (energy > 0F){
				requiredStardust = (byte)(1+1.5F*Math.sqrt(energy*4F/EnergyValues.unit));
				timeStep = (short)Math.max(1, 20-(requiredStardust>>1));
				updateComparatorStatus();
			}
		}
	}

	@Override
	protected boolean onWorkFinished(){
		float energy = EnergyValues.getItemEnergy(items[slotSubject]);
		if (storedEnergy+energy > maxStoredEnergy)return false;
		
		storedEnergy += energy;
		if ((items[slotSubject].stackSize -= 1) <= 0)items[slotSubject] = null;
		if ((items[slotStardust].stackSize -= requiredStardust) <= 0)items[slotStardust] = null;
		
		return true;
	}
	
	@Override
	public float getMaxStoredEnergy(){
		return maxStoredEnergy;
	}
	
	@Override
	public int getHoldingStardust(){
		return items[slotStardust] == null ? 0 : items[slotStardust].stackSize;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setByte("leakTimer", leakTimer);
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
		super.setInventorySlotContents(slot, is);
		if (slot == slotSubject)invalidateInventory();
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is){
		return slot == slotStardust ? is.getItem() == ItemList.stardust : slot == slotOrb ? is.getItem() == ItemList.instability_orb : true;
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

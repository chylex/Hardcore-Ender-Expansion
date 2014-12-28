package chylex.hee.tileentity;
import gnu.trove.map.hash.TObjectFloatHashMap;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.block.BlockList;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.energy.EnergyChunkData;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C10ParticleEnergyTransfer;
import chylex.hee.system.util.ItemDamagePair;

public class TileEntityExtractionTable extends TileEntityAbstractTable{
	private static final int[] slotsTop = new int[]{ 0 }, slotsSides = new int[]{ 1, 2 };
	private static final float maxStoredEnergy = EnergyChunkData.energyDrainUnit*10F;
	private static final TObjectFloatHashMap<ItemDamagePair> energyValues = new TObjectFloatHashMap<>();
	
	private static void setItemEnergy(Block block, float energyMp){
		energyValues.put(new ItemDamagePair(Item.getItemFromBlock(block),-1),energyMp*EnergyChunkData.energyDrainUnit);
	}
	
	private static void setItemEnergy(Item item, float energyMp){
		energyValues.put(new ItemDamagePair(item,-1),energyMp*EnergyChunkData.energyDrainUnit);
	}
	
	private static float getItemEnergy(ItemStack is){
		for(ItemDamagePair idp:energyValues.keySet()){
			if (idp.check(is))return energyValues.get(idp);
		}
		
		return 0;
	}
	
	static{ // TODO
		setItemEnergy(Blocks.end_stone, 6);
		setItemEnergy(BlockList.end_terrain, 8);
		setItemEnergy(ItemList.stardust, 11);
		setItemEnergy(ItemList.end_powder, 14);
		setItemEnergy(ItemList.silverfish_blood, 16);
		setItemEnergy(ItemList.igneous_rock, 21);
		setItemEnergy(ItemList.knowledge_note, 26);
		setItemEnergy(Items.ender_pearl, 30);
		setItemEnergy(ItemList.enhanced_ender_pearl, 32);
		setItemEnergy(Items.ender_eye, 40);
		setItemEnergy(BlockList.spooky_log, 43);
		setItemEnergy(BlockList.spooky_leaves, 43);
		setItemEnergy(ItemList.instability_orb, 57);
		setItemEnergy(ItemList.temple_caller, 66);
		setItemEnergy(Blocks.ender_chest, 75);
		setItemEnergy(ItemList.transference_gem, 85);
		setItemEnergy(ItemList.ectoplasm, 112);
	}

	private byte leakTimer = 100;
	
	@Override
	public void updateEntity(){
		super.updateEntity();
		
		if (!worldObj.isRemote && leakTimer < 0 || (leakTimer -= (items[2] == null || items[2].getItem() != ItemList.instability_orb ? 0 : 16-items[2].stackSize)) < 0){
			leakTimer = 100;
			
			if (storedEnergy > EnergyChunkData.minSignificantEnergy){
				
			}
		}
		if (worldObj.isRemote || true)return;
		
		if (--leakTimer < 0){
			int orbs = items[2] == null || items[2].getItem() != ItemList.instability_orb ? 0 : items[2].stackSize;
			
			if (containedEnergy > 0 && (orbs == 0 || (orbs > 0 && worldObj.rand.nextInt(3) == 0 && worldObj.rand.nextInt(68) > orbs+worldObj.rand.nextInt(10)))){
				Random rand = worldObj.rand;
				boolean leakIntoWorld = true;
				int releasedEnergy = Math.min(containedEnergy,5+rand.nextInt(15));
				
				for(int attempt = 0, xx, yy, zz, energy = releasedEnergy; attempt < 2500 && energy > 0; attempt++){
					xx = xCoord+rand.nextInt(17)-8;
					yy = yCoord+rand.nextInt(17)-8;
					zz = zCoord+rand.nextInt(17)-8;
					
					if (worldObj.getBlock(xx,yy,zz) == BlockList.energy_cluster){
						TileEntityEnergyCluster cluster = (TileEntityEnergyCluster)worldObj.getTileEntity(xx,yy,zz);
						if (cluster == null)continue;
						
						float diff = Math.min(cluster.data.getMaxEnergyLevel()-cluster.data.getEnergyLevel(),releasedEnergy);
						
						if (diff > 0){
							//cluster.data.addEnergy(diff);
							cluster.synchronize();
							if ((energy -= diff) <= 0)leakIntoWorld = false;
							PacketPipeline.sendToAllAround(this,64D,new C10ParticleEnergyTransfer(this,cluster));
						}
					}
				}
				
				if (leakIntoWorld && rand.nextBoolean() && rand.nextBoolean()){
					if (containedEnergy > 360+rand.nextInt(200) && rand.nextInt(3) == 0){
						for(int attempt = 0, xx, yy, zz, dist; attempt < 40; attempt++){
							dist = 2+(attempt>>3);
							xx = xCoord+rand.nextInt(dist*2+1)-dist;
							yy = yCoord+rand.nextInt(dist*2+1)-dist;
							zz = zCoord+rand.nextInt(dist*2+1)-dist;
							
							if (worldObj.isAirBlock(xx,yy,zz)){
								worldObj.setBlock(xx,yy,zz,BlockList.energy_cluster);
								TileEntityEnergyCluster cluster = (TileEntityEnergyCluster)worldObj.getTileEntity(xx,yy,zz);
								
								if (cluster == null)worldObj.setBlockToAir(xx,yy,zz);
								else{
									NBTTagCompound nbt = new NBTTagCompound();
									cluster.data.writeToNBT(nbt);
									nbt.setInteger("energyAmt",releasedEnergy);
									nbt.setByte("weakness",(byte)0);
									nbt.setByte("regenBoost",(byte)0);
									cluster.data.readFromNBT(nbt);
								}
								
								break;
							}
						}
					}
					else if (rand.nextInt(7) == 0){
						for(int attempt = 0, placed = 0, xx, yy, zz, dist; attempt < 40 && placed < 4; attempt++){
							dist = 3+(attempt>>3);
							xx = xCoord+rand.nextInt(dist*2+1)-dist;
							yy = yCoord+rand.nextInt(dist*2+1)-dist;
							zz = zCoord+rand.nextInt(dist*2+1)-dist;
							
							if (worldObj.isAirBlock(xx,yy,zz)){
								worldObj.setBlock(xx,yy,zz,BlockList.corrupted_energy_low,3+rand.nextInt(3),3);
								++placed;
							}
						}
					}
				}
				
				containedEnergy -= releasedEnergy;
				markDirty();
			}
			
			leakTimer = (byte)(15+(orbs>>1)+(orbs>>2));
		}
	}
	
	@Override
	public void invalidateInventory(){
		if (worldObj != null && worldObj.isRemote)return;
		resetTable();
		
		if (items[0] != null){
			float energy = getItemEnergy(items[0]);
			
			if (energy > 0F){
				requiredStardust = (byte)(1+Math.sqrt(energy/EnergyChunkData.energyDrainUnit));
				timeStep = (short)Math.max(1,20-(requiredStardust>>2));
			}
		}
	}

	@Override
	protected boolean onWorkFinished(){
		float energy = getItemEnergy(items[0]);
		if (storedEnergy+energy <= maxStoredEnergy)return false;
		
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

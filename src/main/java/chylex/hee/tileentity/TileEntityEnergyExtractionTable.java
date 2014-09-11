package chylex.hee.tileentity;
import gnu.trove.map.hash.TObjectByteHashMap;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.block.BlockList;
import chylex.hee.item.ItemList;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C10ParticleEnergyTransfer;
import chylex.hee.system.util.ItemDamagePair;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityEnergyExtractionTable extends TileEntityAbstractInventory implements IInventoryInvalidateable{
	private static final int[] slotsTop = new int[]{ 0 }, slotsSides = new int[]{ 1,2 }, slotsBottom = new int[]{};
	private static final TObjectByteHashMap<ItemDamagePair> energyValues = new TObjectByteHashMap<>();
	
	private static void setItemEnergy(Block block, int energy){
		energyValues.put(new ItemDamagePair(Item.getItemFromBlock(block),-1),(byte)energy);
	}
	
	private static void setItemEnergy(Block block, int metadata, int energy){
		energyValues.put(new ItemDamagePair(Item.getItemFromBlock(block),metadata),(byte)energy);
	}
	
	private static void setItemEnergy(Item item, int energy){
		energyValues.put(new ItemDamagePair(item,-1),(byte)energy);
	}
	
	private static void setItemEnergy(Item item, int damage, int energy){
		energyValues.put(new ItemDamagePair(item,damage),(byte)energy);
	}
	
	private static byte getItemEnergy(ItemStack is){
		for(ItemDamagePair idp:energyValues.keySet()){
			if (idp.check(is))return energyValues.get(idp);
		}
		
		return 0;
	}
	
	static{
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

	private short time, timeStep;
	private byte requiredStardust;
	private int containedEnergy;
	private byte leakTimer = 100;
	
	@Override
	public void updateEntity(){
		if (worldObj.isRemote)return;
		
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
						
						int diff = Math.min(cluster.data.getMaxRegenerativeEnergyAmount()-cluster.data.getEnergyAmount(),releasedEnergy);
						
						if (diff > 0){
							cluster.data.addEnergy(diff);
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
		
		if (requiredStardust > 0){
			if (items[1] == null || items[1].getItem() != ItemList.stardust || items[1].stackSize < requiredStardust)return;
			
			if ((time += timeStep) >= 1000){
				int energy = getItemEnergy(items[0]);
				if (containedEnergy+energy > 500){
					time = 1000;
					return;
				}
				
				containedEnergy += energy;
				if ((items[0].stackSize -= 1) <= 0)items[0] = null;
				if ((items[1].stackSize -= requiredStardust) <= 0)items[1] = null;
				
				markDirty();
				resetExtraction();
				invalidateInventory();
			}
		}
	}
	
	@Override
	public void invalidateInventory(){
		if (worldObj != null && worldObj.isRemote)return;
		resetExtraction();
		
		if (items[0] != null){
			byte energy = getItemEnergy(items[0]);
			if (energy > 0){
				requiredStardust = (byte)(1+(energy>>5)+(energy>>3));
				timeStep = (short)Math.max(1,20-Math.sqrt(2*energy));
			}
		}
	}
	
	private void resetExtraction(){
		if (worldObj != null && worldObj.isRemote)return;
		time = timeStep = requiredStardust = 0;
	}
	
	public int getTime(){
		return time;
	}
	
	public int getHoldingStardust(){
		return items[1] == null ? 0 : items[1].stackSize;
	}
	
	public int getRequiredStardust(){
		return requiredStardust;
	}
	
	public int getContainedEnergy(){
		return containedEnergy;
	}
	
	@SideOnly(Side.CLIENT)
	public void setTime(int time){
		this.time = (short)time;
	}
	
	@SideOnly(Side.CLIENT)
	public void setRequiredStardust(int requiredStardust){
		this.requiredStardust = (byte)requiredStardust;
	}
	
	@SideOnly(Side.CLIENT)
	public void setContainedEnergy(int containedEnergy){
		this.containedEnergy = (short)containedEnergy;
	}

	@SideOnly(Side.CLIENT)
	public int getScaledProgressTime(int scale){
		if (time == 0 && timeStep == 0)return -1;
		return (int)Math.ceil(time*(double)scale/1000D);
	}
	
	@SideOnly(Side.CLIENT)
	public int getScaledContainedEnergy(int scale){
		return (int)Math.ceil(containedEnergy*scale/500F);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("energy",containedEnergy);
		nbt.setByte("leakTimer",leakTimer);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		containedEnergy = nbt.getInteger("energy");
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
		return side == 0 ? slotsBottom : side == 1 ? slotsTop : slotsSides;
	}

	@Override
	protected String getContainerDefaultName(){
		return "container.energyExtractionTable";
	}
}

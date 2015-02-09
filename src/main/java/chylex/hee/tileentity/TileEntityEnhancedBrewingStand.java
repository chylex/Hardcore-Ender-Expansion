package chylex.hee.tileentity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.Constants;
import scala.actors.threadpool.Arrays;
import chylex.hee.api.interfaces.IAcceptFieryEssence;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.brewing.PotionTypes;
import chylex.hee.system.util.ItemUtil;

public class TileEntityEnhancedBrewingStand extends TileEntityBrewingStand implements IAcceptFieryEssence, IUpdatePlayerListBox{
	private static final int[] topSlots = new int[]{ 3 },
							   sideSlots = new int[]{ 0, 1, 2 },
							   bottomSlots = new int[]{ 4 };
	
	private ItemStack[] slotItems = new ItemStack[5];
	private short startBrewTime, brewTime, requiredPowder;
	private boolean[] cachedFilledSlots;
	private Item ingredient;
	
	@Override
	public void update(){
		if (brewTime > 0){
			--brewTime;
			
			if (brewTime == 0){
				doBrewing();
				markDirty();
			}
			else if (!checkBrewingRequirements() || ingredient != slotItems[3].getItem()){
				brewTime = 0;
				markDirty();
			}
		}
		else if (checkBrewingRequirements()){
			startBrewTime = brewTime = (short)(140+15*requiredPowder);
			ingredient = slotItems[3].getItem();
		}
		
		if (!worldObj.isRemote){
			boolean[] currentFilledSlots = new boolean[]{ slotItems[0] != null, slotItems[1] != null, slotItems[2] != null };
			
			if (!Arrays.equals(currentFilledSlots,cachedFilledSlots)){
				cachedFilledSlots = currentFilledSlots;
				
				/*IBlockState state = worldObj.getBlockState(getPos());
				for(int a = 0; a < BlockBrewingStand.HAS_BOTTLE.length; a++)state = state.withProperty(BlockBrewingStand.HAS_BOTTLE[a],Boolean.valueOf(currentFilledSlots[a]));
				worldObj.setBlockState(getPos(),state,2);*/
				// TODO fix
			}
		}
	}
	
	private boolean checkBrewingRequirements(){
		if (slotItems[3] == null || slotItems[3].stackSize == 0){
			requiredPowder = 0;
			return false;
		}

		byte potionCount = 0;
		requiredPowder = 0;
		
		for(int a = 0; a < 3; a++){
			if (slotItems[a] == null)continue;
			++potionCount;
			
			if (!PotionTypes.canBeApplied(slotItems[3],slotItems[a]))return false;
			requiredPowder += PotionTypes.getRequiredPowder(slotItems[3].getItem(),slotItems[a]);
		}
		
		requiredPowder = (short)Math.min(requiredPowder*(potionCount == 2 ? 0.835F : potionCount == 3 ? 0.7F : 1F),69);

		if (potionCount == 0)return false;
		return requiredPowder == 0 || (slotItems[4] != null && slotItems[4].stackSize >= requiredPowder);
	}
		
	private void doBrewing(){
		if (worldObj.isRemote || !checkBrewingRequirements())return;

		for(int a = 0; a < 3; a++){
			if (slotItems[a] == null)continue;
			
			slotItems[a] = PotionTypes.applyIngredientUnsafe(slotItems[3],slotItems[a]);
			ItemUtil.getNBT(slotItems[a],true).setBoolean("hasPotionChanged",true);
		}
		
		if (--slotItems[3].stackSize == 0)slotItems[3] = null;
		
		if (requiredPowder > 0){
			slotItems[4].stackSize -= requiredPowder;
			if (slotItems[4].stackSize == 0)slotItems[4] = null;
			requiredPowder = 0;
		}
	}
	
	@Override
	public String getName(){
		return hasCustomName() ? super.getName() : "container.enhancedBrewing";
	}
	
	@Override
	public int getSizeInventory(){
		return slotItems.length;
	}
	
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is){
		return (is.getItem() == ItemList.end_powder && slot == 4) || super.isItemValidForSlot(slot,is);
	}
	
	@Override
	public ItemStack getStackInSlot(int slot){
		return slot >= 0 && slot < slotItems.length?slotItems[slot]:null;
	}
	
	@Override
	public ItemStack decrStackSize(int slot, int amount){
		return getStackInSlotOnClosing(slot); // Lazy
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int slot){
		if (slot >= 0 && slot < slotItems.length){
			ItemStack is = slotItems[slot];
			slotItems[slot] = null;
			return is;
		}
		else return null;
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack is){
		if (slot >= 0 && slot < slotItems.length)slotItems[slot] = is;
	}
	
	@Override
	public int[] getSlotsForFace(EnumFacing side){
		return side == EnumFacing.UP ? topSlots : side == EnumFacing.DOWN ? bottomSlots : sideSlots;
	}
	
	@Override
	public void setField(int id, int value){
		if (id == 0)brewTime = (short)value;
		else if (id == 1)requiredPowder = (short)value;
		else if (id == 2)startBrewTime = (short)value;
	}
	
	@Override
	public int getField(int id){
		if (id == 0)return brewTime;
		else if (id == 1)return requiredPowder;
		else if (id == 2)return startBrewTime;
		else return 0;
	}
	
	@Override
	public int getFieldCount(){
		return 3;
	}
	
	@Override
	public void clear(){
		for(int a = 0; a < slotItems.length; a++)slotItems[a] = null;
	}
	
	public int getHoldingPowder(){
		return slotItems[4] == null ? 0 : slotItems[4].stackSize;
	}

	@Override
	public int getBoostAmount(int essenceLevel){
		return Math.min(brewTime-1,1+Math.min(7,essenceLevel>>6));
	}

	@Override
	public void boost(){
		--brewTime;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		
		NBTTagList tagItemList = new NBTTagList();
		for(int i = 0; i < slotItems.length; ++i){
			if (slotItems[i] != null){
				NBTTagCompound tagSlot = new NBTTagCompound();
				tagSlot.setByte("Slot",(byte)i);
				slotItems[i].writeToNBT(tagSlot);
				tagItemList.appendTag(tagSlot);
			}
		}
		nbt.setTag("hedItems",tagItemList);
		
		nbt.setShort("hedBrewTime",brewTime);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		
		NBTTagList tagItemList = nbt.getTagList("hedItems",Constants.NBT.TAG_COMPOUND);
		slotItems = new ItemStack[this.getSizeInventory()];

		for(int i = 0; i < tagItemList.tagCount(); ++i){
			NBTTagCompound tagSlot = tagItemList.getCompoundTagAt(i);
			byte slotId = tagSlot.getByte("Slot");
			if (slotId >= 0 && slotId < slotItems.length)slotItems[slotId] = ItemStack.loadItemStackFromNBT(tagSlot);
		}

		brewTime = nbt.getShort("hedBrewTime");
	}
}

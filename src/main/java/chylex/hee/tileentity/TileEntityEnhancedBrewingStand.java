package chylex.hee.tileentity;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraftforge.common.util.Constants;
import chylex.hee.api.interfaces.IAcceptFieryEssence;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.brewing.PotionTypes;
import chylex.hee.mechanics.enhancements.EnhancementEnumHelper;
import chylex.hee.mechanics.enhancements.EnhancementHandler;
import chylex.hee.mechanics.enhancements.IEnhanceableTile;
import chylex.hee.mechanics.enhancements.types.EnhancedBrewingStandEnhancements;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityEnhancedBrewingStand extends TileEntityBrewingStand implements IAcceptFieryEssence, IEnhanceableTile{
	private static final int[] topSlots = new int[]{ 3 },
							   sideSlots = new int[]{ 0, 1, 2 },
							   bottomSlots = new int[]{ 4 };
	
	private ItemStack[] slotItems = new ItemStack[5];
	private byte filledSlotsCache;
	private short startBrewTime, brewTime, requiredPowder;
	private Item ingredient;
	
	private List<Enum> enhancements = new ArrayList<>();
	
	@Override
	public void updateEntity(){
		if (brewTime > 0){
			--brewTime;
			if (brewTime > 1 && brewTime%2 == 0 && enhancements.contains(EnhancedBrewingStandEnhancements.SPEED))--brewTime;
			
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
		
		int filledSlots = getFilledSlots();
		if (filledSlots != filledSlotsCache){
			filledSlotsCache = (byte)filledSlots;
			worldObj.setBlockMetadataWithNotify(xCoord,yCoord,zCoord,filledSlots,2);
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
			
			if (!PotionTypes.canBeApplied(slotItems[3],slotItems[a],enhancements.contains(EnhancedBrewingStandEnhancements.TIER)))return false;
			requiredPowder += PotionTypes.getRequiredPowder(slotItems[3].getItem(),slotItems[a]);
		}
		
		requiredPowder = (short)Math.min(requiredPowder*(potionCount == 2 ? 0.835F : potionCount == 3 ? 0.7F : 1F)*(enhancements.contains(EnhancedBrewingStandEnhancements.COST) ? 0.65F : 1F),69);

		if (potionCount == 0)return false;
		return requiredPowder == 0 || (slotItems[4] != null && slotItems[4].stackSize >= requiredPowder);
	}
		
	private void doBrewing(){
		if (worldObj.isRemote || !checkBrewingRequirements())return;

		for(int a = 0; a < 3; a++){
			if (slotItems[a] == null)continue;
			
			slotItems[a] = PotionTypes.applyIngredientUnsafe(slotItems[3],slotItems[a]);
			
			if (slotItems[a].stackTagCompound == null)slotItems[a].stackTagCompound = new NBTTagCompound();
			slotItems[a].stackTagCompound.setBoolean("hasPotionChanged",true);
		}
		
		if (--slotItems[3].stackSize == 0)slotItems[3] = null;
		
		if (requiredPowder > 0){
			slotItems[4].stackSize -= requiredPowder;
			if (slotItems[4].stackSize == 0)slotItems[4] = null;
			requiredPowder = 0;
		}
	}
	
	@Override
	public List<Enum> getEnhancements(){
		return enhancements;
	}
	
	@Override
	public ItemStack createEnhancedItemStack(){
		return EnhancementHandler.addEnhancements(new ItemStack(ItemList.enhanced_brewing_stand),enhancements);
	}
	
	@Override
	public String getInventoryName(){
		return hasCustomInventoryName() ? super.getInventoryName() : "container.enhancedBrewing";
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
		return slot >= 0 && slot < slotItems.length ? slotItems[slot] : null;
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
	public int getFilledSlots(){
		int i = 0;
		for(int j = 0; j < 3; ++j){
			if (slotItems[j] != null)i |= 1<<j;
		}
		return i;
	}
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side){
		return side == 1 ? topSlots : side == 0 ? bottomSlots : sideSlots;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void func_145938_d(int brewTime){ // OBFUSCATED set brew time
		this.brewTime = (short)brewTime;
	}
	
	@SideOnly(Side.CLIENT)
	public void setRequiredPowder(int requiredPowder){
		this.requiredPowder = (short)requiredPowder;
	}
	
	@SideOnly(Side.CLIENT)
	public void setStartBrewTime(int startBrewTime){
		this.startBrewTime = (short)startBrewTime;
	}
	
	@Override
	public int getBrewTime(){
		return brewTime;
	}
	
	public int getStartBrewTime(){
		return startBrewTime;
	}

	public int getRequiredPowder(){
		return requiredPowder;
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
		nbt.setString("enhancements",EnhancementEnumHelper.serialize(enhancements));
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
		enhancements = EnhancementEnumHelper.deserialize(nbt.getString("enhancements"),EnhancedBrewingStandEnhancements.class);
	}
}

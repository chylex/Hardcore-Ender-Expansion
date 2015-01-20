package chylex.hee.tileentity;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.util.Constants;

public abstract class TileEntityAbstractInventory extends TileEntity implements IUpdatePlayerListBox, ISidedInventory{
	protected ItemStack[] items;
	private String customName;
	private TIntHashSet slotIndexesToUpdateBecauseTheMotherfuckingHoppersDoNotCallAnythingToNotifyTheTileEntity = new TIntHashSet();
	
	public TileEntityAbstractInventory(){
		items = new ItemStack[getSizeInventory()];
	}
	
	@Override
	public void update(){
		if (!slotIndexesToUpdateBecauseTheMotherfuckingHoppersDoNotCallAnythingToNotifyTheTileEntity.isEmpty()){
			for(int slot:slotIndexesToUpdateBecauseTheMotherfuckingHoppersDoNotCallAnythingToNotifyTheTileEntity.toArray()){
				setInventorySlotContents(slot,getStackInSlot(slot));
			}
			
			slotIndexesToUpdateBecauseTheMotherfuckingHoppersDoNotCallAnythingToNotifyTheTileEntity.clear();
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);

		NBTTagList itemList = new NBTTagList();

		for(int slot = 0; slot < items.length; ++slot){
			if (items[slot] != null){
				NBTTagCompound tag = new NBTTagCompound();
				tag.setByte("Slot",(byte)slot);
				itemList.appendTag(items[slot].writeToNBT(tag));
			}
		}

		nbt.setTag("Items",itemList);
		if (hasCustomName())nbt.setString("CustomName",customName);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);

		NBTTagList itemList = nbt.getTagList("Items",Constants.NBT.TAG_COMPOUND);
		items = new ItemStack[getSizeInventory()];

		for(int a = 0; a < itemList.tagCount(); ++a){
			NBTTagCompound tag = itemList.getCompoundTagAt(a);
			byte slot = tag.getByte("Slot");
			if (slot >= 0 && slot < items.length)items[slot] = ItemStack.loadItemStackFromNBT(tag);
		}
		
		if (nbt.hasKey("CustomName",Constants.NBT.TAG_STRING))customName = nbt.getString("CustomName");
	}

	@Override
	public ItemStack getStackInSlot(int slot){
		return slot >= 0 && slot < items.length ? items[slot] : null;
	}

	@Override
	public final ItemStack decrStackSize(int slot, int amount){
		if (slot >= 0 && slot < items.length){
			if (items[slot].stackSize <= amount){
				ItemStack toReturn = items[slot];
				items[slot] = null;
				return toReturn;
			}
			else{
				ItemStack is = items[slot].splitStack(amount);
				if (is.stackSize == 0)items[slot] = null;
				return is;
			}
		}
		else return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot){
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack is){
		if (slot >= 0 && slot < items.length)items[slot] = is;
	}

	@Override
	public int getInventoryStackLimit(){
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player){
		return player.getDistanceSq(getPos().getX()+0.5D,getPos().getY()+0.5D,getPos().getZ()+0.5D) <= 64D;
	}

	@Override
	public void openInventory(EntityPlayer player){}

	@Override
	public void closeInventory(EntityPlayer player){}

	@Override
	public boolean canInsertItem(int slot, ItemStack is, EnumFacing side){
		if (isItemValidForSlot(slot,is)){
			slotIndexesToUpdateBecauseTheMotherfuckingHoppersDoNotCallAnythingToNotifyTheTileEntity.add(slot);
			return true;
		}
		else return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, EnumFacing side){
		slotIndexesToUpdateBecauseTheMotherfuckingHoppersDoNotCallAnythingToNotifyTheTileEntity.add(slot);
		return true;
	}

	@Override
	public final String getName(){
		return customName != null ? customName : getContainerDefaultName();
	}

	@Override
	public final boolean hasCustomName(){
		return customName != null && customName.length() > 0;
	}
	
	@Override
	public final IChatComponent getDisplayName(){
		return hasCustomName() ? new ChatComponentText(customName) : new ChatComponentTranslation(getName());
	}
	
	@Override
	public int getFieldCount(){
		return 0;
	}

	@Override
	public void setField(int id, int value){}

	@Override
	public int getField(int id){
		return 0;
	}

	@Override
	public void clear(){
		for(int a = 0; a < items.length; a++)items[a] = null;
	}
	
	protected abstract String getContainerDefaultName();
}

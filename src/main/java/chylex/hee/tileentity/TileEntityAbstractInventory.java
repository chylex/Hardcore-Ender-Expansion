package chylex.hee.tileentity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

public abstract class TileEntityAbstractInventory extends TileEntity implements ISidedInventory{
	protected ItemStack[] items;
	private String customName;
	
	public TileEntityAbstractInventory(){
		items = new ItemStack[getSizeInventory()];
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
		if (hasCustomInventoryName())nbt.setString("CustomName",customName);
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
	public ItemStack decrStackSize(int slot, int amount){
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
		return player.getDistanceSq(xCoord + 0.5D,yCoord + 0.5D,zCoord + 0.5D) <= 64D;
	}

	@Override
	public void openInventory(){}

	@Override
	public void closeInventory(){}

	@Override
	public boolean canInsertItem(int slot, ItemStack is, int side){
		return isItemValidForSlot(slot,is);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side){
		return true;
	}

	@Override
	public String getInventoryName(){
		return customName != null ? customName : getContainerDefaultName();
	}

	@Override
	public boolean hasCustomInventoryName(){
		return customName != null && customName.length() > 0;
	}
	
	protected abstract String getContainerDefaultName();
}

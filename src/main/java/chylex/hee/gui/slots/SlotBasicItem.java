package chylex.hee.gui.slots;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotBasicItem extends Slot{
	private final Item validItem;
	private final byte stackLimit;
	
	public SlotBasicItem(IInventory inv, int id, int x, int z, Item validItem){
		this(inv,id,x,z,validItem,inv.getInventoryStackLimit());
	}
	
	public SlotBasicItem(IInventory inv, int id, int x, int z, Item validItem, int stackLimit){
		super(inv,id,x,z);
		this.validItem = validItem;
		this.stackLimit = (byte)stackLimit;
	}
	
	@Override
	public boolean isItemValid(ItemStack is){
		return is != null ? is.getItem() == validItem : false;
	}
	
	@Override
	public int getSlotStackLimit(){
		return stackLimit;
	}
}

package chylex.hee.gui;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

class SlotReadOnly extends Slot{
	public SlotReadOnly(IInventory inv, int id, int x, int z){
		super(inv,id,x,z);
	}
	
	public SlotReadOnly(Slot originalSlot){
		super(originalSlot.inventory,originalSlot.slotNumber,originalSlot.xDisplayPosition,originalSlot.yDisplayPosition);
	}
	
	@Override
	public boolean isItemValid(ItemStack is){
		return false;
	}
	
	@Override
	public int getSlotStackLimit(){
		return 64;
	}
}

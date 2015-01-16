package chylex.hee.gui.slots;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotReadOnly extends Slot{
	public SlotReadOnly(IInventory inv, int id, int x, int z){
		super(inv,id,x,z);
	}
	
	public SlotReadOnly(Slot originalSlot){
		super(originalSlot.inventory,originalSlot.getSlotIndex(),originalSlot.xDisplayPosition,originalSlot.yDisplayPosition);
		this.slotNumber = originalSlot.slotNumber;
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

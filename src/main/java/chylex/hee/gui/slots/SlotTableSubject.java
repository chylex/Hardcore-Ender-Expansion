package chylex.hee.gui.slots;
import net.minecraft.inventory.Slot;
import chylex.hee.tileentity.base.IInventoryInvalidateable;

public class SlotTableSubject extends Slot{
	private final IInventoryInvalidateable inv;
	
	public SlotTableSubject(IInventoryInvalidateable inv, int id, int x, int z){
		super(inv, id, x, z);
		this.inv = inv;
	}
	
	@Override
	public void onSlotChanged(){
		super.onSlotChanged();
		inv.invalidateInventory();
	}
}

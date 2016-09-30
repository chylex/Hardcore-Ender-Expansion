package chylex.hee.gui.slots;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import chylex.hee.item.ItemAbstractEnergyAcceptor;

public class SlotEnergyAcceptor extends Slot{
	public SlotEnergyAcceptor(IInventory inv, int id, int x, int z){
		super(inv, id, x, z);
	}

	@Override
	public boolean isItemValid(ItemStack is){
		return is != null && is.getItem() instanceof ItemAbstractEnergyAcceptor;
	}
}

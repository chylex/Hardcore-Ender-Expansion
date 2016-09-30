package chylex.hee.gui.slots;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import chylex.hee.mechanics.enhancements.EnhancementRegistry;

public class SlotEnhancementsSubject extends Slot{
	public SlotEnhancementsSubject(IInventory inv, int id, int x, int z){
		super(inv, id, x, z);
	}

	@Override
	public boolean isItemValid(ItemStack is){
		return is != null ? EnhancementRegistry.canEnhanceItem(is.getItem()) : false;
	}
}

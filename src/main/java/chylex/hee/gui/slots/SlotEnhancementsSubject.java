package chylex.hee.gui.slots;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import chylex.hee.gui.ContainerEndPowderEnhancements;
import chylex.hee.mechanics.enhancements.EnhancementRegistry;

public class SlotEnhancementsSubject extends Slot{
	private final ContainerEndPowderEnhancements container;
	
	public SlotEnhancementsSubject(ContainerEndPowderEnhancements container, IInventory inv, int id, int x, int z){
		super(inv,id,x,z);
		this.container = container;
	}

	@Override
	public boolean isItemValid(ItemStack is){
		return is != null ? EnhancementRegistry.canEnhanceItem(is.getItem()) : false;
	}
	
	@Override
	public void onSlotChanged(){
		super.onSlotChanged();
		container.onSubjectChanged();
	}
}

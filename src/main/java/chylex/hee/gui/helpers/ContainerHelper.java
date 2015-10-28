package chylex.hee.gui.helpers;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public final class ContainerHelper{
	/**
	 * Returns whether items inside the container changed, and thus need to be resynchronized. Should be used in {@code Container.detectAndSendChanges} before calling the super method.
	 */
	public static boolean hasChanged(Container container){
		for(int slot = 0; slot < container.inventorySlots.size(); slot++){
			ItemStack is1 = ((Slot)container.inventorySlots.get(slot)).getStack();
			ItemStack is2 = (ItemStack)container.inventoryItemStacks.get(slot);
			if (!ItemStack.areItemStacksEqual(is1,is2))return true;
		}
		
		return false;
	}
	
	public static void addPlayerInventorySlots(Container container, IInventory inventory, int offsetX, int offsetY){
		for(int row = 0; row < 3; row++){
			for(int col = 0; col < 9; col++)addSlotToContainer(container,new Slot(inventory,col+row*9+9,8+col*18+offsetX,84+row*18+offsetY));
		}

		for(int col = 0; col < 9; col++)addSlotToContainer(container,new Slot(inventory,col,8+col*18+offsetX,142+offsetY));
	}
	
	public static void addSlotToContainer(Container container, Slot slot){
		slot.slotNumber = container.inventorySlots.size();
		container.inventorySlots.add(slot);
		container.inventoryItemStacks.add(null);
	}
	
	private ContainerHelper(){}
}

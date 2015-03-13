package chylex.hee.gui;
import chylex.hee.gui.slots.SlotReadOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerVoidChest extends ContainerChest{
	private int chestSlots;
	
	public ContainerVoidChest(IInventory inventory, IInventory chest){
		super(inventory,chest);
		
		chestSlots = chest.getSizeInventory();
		
		for(int a = 0, numRows = chest.getSizeInventory()/9; a < numRows; a++){
			for(int b = 0; b < 9; b++)inventorySlots.set(b+9*a,new SlotReadOnly((Slot)inventorySlots.get(b+9*a)));
		}
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotId){
		ItemStack isCopy = null;
		Slot slot = (Slot)inventorySlots.get(slotId);

		if (slot != null && slot.getHasStack()){
			ItemStack is = slot.getStack();
			isCopy = is.copy();

			if (slotId < chestSlots){
				if (!mergeItemStack(is,chestSlots,inventorySlots.size(),true)){
					return null;
				}
			}
			else return null;

			if (is.stackSize == 0)slot.putStack(null);
			else slot.onSlotChanged();
		}

		return isCopy;
	}
}

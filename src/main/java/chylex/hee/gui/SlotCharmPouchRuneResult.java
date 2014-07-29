package chylex.hee.gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotCharmPouchRuneResult extends Slot{
	private final IInventory runeInv;
	private final ContainerCharmPouch pouchContainer;
	
	public SlotCharmPouchRuneResult(IInventory inv, IInventory runeInv, ContainerCharmPouch pouchContainer, int slot, int x, int y){
		super(inv,slot,x,y);
		this.runeInv = runeInv;
		this.pouchContainer = pouchContainer;
	}
	
	@Override
	public boolean isItemValid(ItemStack is){
		return false;
	}
	
	@Override
	public void onPickupFromSlot(EntityPlayer player, ItemStack is){
		super.onPickupFromSlot(player,is);
		
		for(int a = 0; a < runeInv.getSizeInventory(); a++){
			ItemStack slotIS = runeInv.getStackInSlot(a);
			if (slotIS != null && --slotIS.stackSize == 0)runeInv.setInventorySlotContents(a,null);
		}
		
		pouchContainer.onCraftMatrixChanged(runeInv);
	}
}

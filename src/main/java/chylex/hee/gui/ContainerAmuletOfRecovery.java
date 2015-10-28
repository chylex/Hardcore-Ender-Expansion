package chylex.hee.gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import chylex.hee.gui.helpers.ContainerHelper;
import chylex.hee.gui.slots.SlotReadOnly;
import chylex.hee.init.ItemList;
import chylex.hee.item.ItemAmuletOfRecovery;

public class ContainerAmuletOfRecovery extends ContainerChest{
	private final EntityPlayer player;
	private final IInventory amuletInv;
	private int amuletSlots;
	
	public ContainerAmuletOfRecovery(EntityPlayer player){
		super(player.inventory,ItemAmuletOfRecovery.getAmuletInventory(player.getHeldItem()));
		
		this.player = player;
		this.amuletInv = getLowerChestInventory();
		this.amuletSlots = amuletInv.getSizeInventory();
		
		for(int a = 0, numRows = amuletInv.getSizeInventory()/9; a < numRows; a++){
			for(int b = 0; b < 9; b++)inventorySlots.set(b+9*a,new SlotReadOnly((Slot)inventorySlots.get(b+9*a)));
		}
	}
	
	private boolean isHoldingAmulet(){
		ItemStack is = player.getHeldItem();
		return is != null && is.getItem() == ItemList.amulet_of_recovery;
	}
	
	@Override
	public void detectAndSendChanges(){
		if (!player.worldObj.isRemote){
			if (!isHoldingAmulet()){
				player.closeScreen();
				return;
			}
			else if (ContainerHelper.hasChanged(this)){
				System.out.println("save"); // TODO
				ItemAmuletOfRecovery.setAmuletInventory(player.getHeldItem(),amuletInv);
			}
		}
		
		super.detectAndSendChanges();
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotId){
		ItemStack isCopy = null;
		Slot slot = (Slot)inventorySlots.get(slotId);

		if (slot != null && slot.getHasStack()){
			ItemStack is = slot.getStack();
			isCopy = is.copy();

			if (slotId < amuletSlots){
				if (!mergeItemStack(is,amuletSlots,inventorySlots.size(),true)){
					return null;
				}
			}
			else return null;

			if (is.stackSize == 0)slot.putStack(null);
			else slot.onSlotChanged();
		}

		return isCopy;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player){
		return true;
	}
}

package chylex.hee.gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import chylex.hee.gui.helpers.ContainerHelper;
import chylex.hee.gui.helpers.IContainerEventHandler;
import chylex.hee.gui.slots.SlotReadOnly;
import chylex.hee.init.ItemList;
import chylex.hee.item.ItemAmuletOfRecovery;

public class ContainerAmuletOfRecovery extends ContainerChest implements IContainerEventHandler{
	private final EntityPlayer player;
	private final IInventory amuletInv;
	
	public ContainerAmuletOfRecovery(EntityPlayer player){
		super(player.inventory, ItemAmuletOfRecovery.getAmuletInventory(player.getHeldItem()));
		
		this.player = player;
		this.amuletInv = getLowerChestInventory();
		
		for(int a = 0, numRows = amuletInv.getSizeInventory()/9; a < numRows; a++){
			for(int b = 0; b < 9; b++)inventorySlots.set(b+9*a, new SlotReadOnly((Slot)inventorySlots.get(b+9*a)));
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
				ItemAmuletOfRecovery.setAmuletInventory(player.getHeldItem(), amuletInv);
			}
		}
		
		super.detectAndSendChanges();
	}
	
	@Override
	public void onEvent(EntityPlayer player, int eventID){
		if (!isHoldingAmulet() || eventID != 0)return;
		
		InventoryPlayer playerInv = this.player.inventory;
		
		for(int slot = 0; slot < 4; slot++){
			tryMoveSlot(3-slot, slot, playerInv.armorInventory);
		}
		
		for(int slot = 9; slot < 36; slot++){
			tryMoveSlot(slot, slot, playerInv.mainInventory);
		}
		
		for(int slot = 0; slot < 9; slot++){
			tryMoveSlot(36+slot, slot, playerInv.mainInventory);
		}
	}
	
	private void tryMoveSlot(int sourceSlot, int targetSlot, ItemStack[] target){
		if (amuletInv.getStackInSlot(sourceSlot) != null && target[targetSlot] == null){
			target[targetSlot] = amuletInv.getStackInSlot(sourceSlot);
			amuletInv.setInventorySlotContents(sourceSlot, null);
		}
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotId){
		return ContainerHelper.transferStack(this, this::mergeItemStack, amuletInv.getSizeInventory(), slotId); // TODO test
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player){
		return true;
	}
}

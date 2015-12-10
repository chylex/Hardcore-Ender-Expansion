package chylex.hee.gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import chylex.hee.game.save.SaveData;
import chylex.hee.game.save.types.player.PortalFile;
import chylex.hee.gui.helpers.ContainerHelper;
import chylex.hee.gui.slots.SlotBasicItem;
import chylex.hee.init.ItemList;

public class ContainerVoidPortalTokens extends ContainerChest{
	private final EntityPlayer player;
	private final IInventory tokenInv;
	
	public ContainerVoidPortalTokens(EntityPlayer player){
		super(player.inventory,SaveData.player(player,PortalFile.class).getTokenInventory());
		
		this.player = player;
		this.tokenInv = getLowerChestInventory();
		
		for(int a = 0, numRows = tokenInv.getSizeInventory()/9; a < numRows; a++){
			for(int b = 0; b < 9; b++){
				Slot slot = (Slot)inventorySlots.get(b+9*a);
				Slot newSlot = new SlotBasicItem(tokenInv,slot.getSlotIndex(),0,0,ItemList.portal_token,1);
				
				ContainerHelper.copySlotInfo(newSlot,slot);
				inventorySlots.set(b+9*a,newSlot);
			}
		}
	}
	
	@Override
	public void detectAndSendChanges(){
		if (!player.worldObj.isRemote && ContainerHelper.hasChanged(this)){
			SaveData.player(player,PortalFile.class).onTokenInventoryUpdated();
		}
		
		super.detectAndSendChanges();
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotId){
		return ContainerHelper.transferStack(this,this::mergeItemStack,tokenInv.getSizeInventory(),slotId);
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player){
		return true;
	}
}

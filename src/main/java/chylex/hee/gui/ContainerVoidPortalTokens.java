package chylex.hee.gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import chylex.hee.block.BlockVoidPortal;
import chylex.hee.game.save.SaveData;
import chylex.hee.game.save.types.player.PortalFile;
import chylex.hee.gui.helpers.ContainerHelper;
import chylex.hee.gui.helpers.IContainerEventHandler;
import chylex.hee.gui.slots.SlotBasicItem;
import chylex.hee.init.ItemList;
import chylex.hee.system.abstractions.Pos;

public class ContainerVoidPortalTokens extends Container implements IContainerEventHandler{
	private final EntityPlayer player;
	private final IInventory tokenInv;
	private final Pos voidPortalPos;
	
	public ContainerVoidPortalTokens(EntityPlayer player, Pos voidPortalPos){
		this.player = player;
		this.tokenInv = !player.worldObj.isRemote ? SaveData.player(player,PortalFile.class).getTokenInventory() : new InventoryBasic("",false,PortalFile.slots);
		this.voidPortalPos = voidPortalPos;
		
		for(int a = 0, numRows = tokenInv.getSizeInventory()/9; a < numRows; a++){
			for(int b = 0; b < 9; b++){
				addSlotToContainer(new SlotBasicItem(tokenInv,b+9*a,8+b*18,18+a*18,ItemList.portal_token,1));
			}
		}
		
		ContainerHelper.addPlayerInventorySlots(this,player.inventory,0,9);
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

	@Override
	public void onEvent(EntityPlayer player, int eventID){
		if (eventID >= 0 && eventID < tokenInv.getSizeInventory() && tokenInv.getStackInSlot(eventID) != null){
			BlockVoidPortal.getData(player.worldObj,voidPortalPos.getX(),voidPortalPos.getY(),voidPortalPos.getZ()).ifPresent(data -> data.activate(tokenInv.getStackInSlot(eventID)));
			player.closeScreen();
		}
	}
}

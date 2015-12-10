package chylex.hee.gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import chylex.hee.gui.helpers.ContainerHelper;
import chylex.hee.gui.slots.SlotReadOnly;

public class ContainerLootChest extends ContainerChest{
	private int chestSlots;
	
	public ContainerLootChest(InventoryPlayer inventory, InventoryLootChest chest){
		super(inventory,chest);
		
		chestSlots = chest.getSizeInventory();
		
		if (!inventory.player.capabilities.isCreativeMode){
			for(int a = 0, numRows = chest.getSizeInventory()/9; a < numRows; a++){
				for(int b = 0; b < 9; b++)inventorySlots.set(b+9*a,new SlotReadOnly((Slot)inventorySlots.get(b+9*a)));
			}
		}
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotId){
		if (player.capabilities.isCreativeMode)return super.transferStackInSlot(player,slotId);
		else return ContainerHelper.transferStack(this,this::mergeItemStack,chestSlots,slotId);
	}
}

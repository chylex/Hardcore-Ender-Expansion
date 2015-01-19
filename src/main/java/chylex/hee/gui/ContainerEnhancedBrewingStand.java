package chylex.hee.gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.gui.slots.SlotBasicItem;
import chylex.hee.gui.slots.SlotBrewingStandIngredient;
import chylex.hee.gui.slots.SlotBrewingStandPotion;
import chylex.hee.item.ItemList;
import chylex.hee.tileentity.TileEntityEnhancedBrewingStand;

public class ContainerEnhancedBrewingStand extends Container{
	private final TileEntityEnhancedBrewingStand tileBrewingStand;
	private final Slot ingredientSlot,powderSlot;
	private int startBrewTime,brewTime,requiredPowder;

	public ContainerEnhancedBrewingStand(InventoryPlayer inv, TileEntityEnhancedBrewingStand tile){
		tileBrewingStand = tile;
		addSlotToContainer(new SlotBrewingStandPotion(tile,0,58,46));
		addSlotToContainer(new SlotBrewingStandPotion(tile,1,81,53));
		addSlotToContainer(new SlotBrewingStandPotion(tile,2,104,46));
		ingredientSlot = addSlotToContainer(new SlotBrewingStandIngredient(tile,3,81,17));
		powderSlot = addSlotToContainer(new SlotBasicItem(tile,4,81,77,ItemList.end_powder));
		
		for(int i = 0; i < 3; ++i){
			for(int j = 0; j < 9; ++j)addSlotToContainer(new Slot(inv,j+i*9+9,9+j*18,108+i*18));
		}

		for(int i = 0; i < 9; ++i)addSlotToContainer(new Slot(inv,i,9+i*18,166));
	}

	@Override
	public void addCraftingToCrafters(ICrafting icrafting){
		super.addCraftingToCrafters(icrafting);
		icrafting.sendProgressBarUpdate(this,0,tileBrewingStand.getBrewTime());
		icrafting.sendProgressBarUpdate(this,2,tileBrewingStand.getStartBrewTime());
	}

	@Override
	public void detectAndSendChanges(){
		super.detectAndSendChanges();

		for(int i = 0; i < crafters.size(); ++i){
			ICrafting icrafting = (ICrafting)crafters.get(i);

			if (brewTime != tileBrewingStand.getBrewTime())icrafting.sendProgressBarUpdate(this,0,tileBrewingStand.getBrewTime());
			if (requiredPowder != tileBrewingStand.getRequiredPowder())icrafting.sendProgressBarUpdate(this,1,tileBrewingStand.getRequiredPowder());
			if (startBrewTime != tileBrewingStand.getStartBrewTime())icrafting.sendProgressBarUpdate(this,2,tileBrewingStand.getStartBrewTime());
		}

		brewTime = tileBrewingStand.getBrewTime();
		requiredPowder = tileBrewingStand.getRequiredPowder();
		startBrewTime = tileBrewingStand.getStartBrewTime();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int value){
		if (id == 0)tileBrewingStand.func_145938_d(value); // OBFUSCATED set brew time
		else if (id == 1)tileBrewingStand.setRequiredPowder(value);
		else if (id == 2)tileBrewingStand.setStartBrewTime(value);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player){
		return tileBrewingStand.isUseableByPlayer(player);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotId){
		ItemStack is = null;
		Slot slot = (Slot)inventorySlots.get(slotId);

		if (slot != null && slot.getHasStack()){
			ItemStack slotIS = slot.getStack();
			is = slotIS.copy();

			if ((slotId < 0 || slotId > 2) && slotId != 3 && slotId != 4){
				if (!ingredientSlot.getHasStack() && ingredientSlot.isItemValid(slotIS)){
					if (!mergeItemStack(slotIS,3,4,false))return null;
				}
				else if (powderSlot.isItemValid(slotIS)){
					if (!mergeItemStack(slotIS,4,5,false))return null;
				}
				else if (SlotBrewingStandPotion.canHoldPotion(is)){
					if (!mergeItemStack(slotIS,0,3,false))return null;
				}
				else if (slotId >= 5 && slotId < 32){
					if (!mergeItemStack(slotIS,32,41,false))return null;
				}
				else if (slotId >= 32 && slotId < 41){
					if (!mergeItemStack(slotIS,5,32,false))return null;
				}
				else if (!mergeItemStack(slotIS,5,41,false))return null;
			}
			else{
				if (!mergeItemStack(slotIS,5,41,true))return null;
				slot.onSlotChange(slotIS,is);
			}

			if (slotIS.stackSize == 0)slot.putStack((ItemStack)null);
			else slot.onSlotChanged();
			
			if (slotIS.stackSize == is.stackSize)return null;
			slot.onPickupFromSlot(player,slotIS);
		}

		return is;
	}
}

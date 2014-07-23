package chylex.hee.gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import chylex.hee.item.ItemList;
import chylex.hee.tileentity.TileEntityEnergyExtractionTable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerEnergyExtractionTable extends Container{
	private final TileEntityEnergyExtractionTable tileEnergyExtractionTable;
	private int prevReqStardust, prevTime, prevContainedEnergy;
	
	public ContainerEnergyExtractionTable(InventoryPlayer inv, TileEntityEnergyExtractionTable tile){
		tileEnergyExtractionTable = tile;
		
		addSlotToContainer(new SlotTableSubject(tile,0,40,17));
		addSlotToContainer(new SlotBasicItem(tile,1,40,53,ItemList.stardust));
		addSlotToContainer(new SlotBasicItem(tile,2,121,64,ItemList.instability_orb));
		
		for(int i = 0; i < 3; ++i){
			for(int j = 0; j < 9; ++j)addSlotToContainer(new Slot(inv,j + i * 9 + 9,8 + j * 18,84 + i * 18));
		}

		for(int i = 0; i < 9; ++i)addSlotToContainer(new Slot(inv,i,8 + i * 18,142));
	}
	
	@Override
	public void addCraftingToCrafters(ICrafting crafter){
		super.addCraftingToCrafters(crafter);
		crafter.sendProgressBarUpdate(this,0,tileEnergyExtractionTable.getRequiredStardust());
		crafter.sendProgressBarUpdate(this,1,tileEnergyExtractionTable.getTime());
		crafter.sendProgressBarUpdate(this,2,tileEnergyExtractionTable.getContainedEnergy());
	}

	@Override
	public void detectAndSendChanges(){
		super.detectAndSendChanges();

		for(int i = 0; i < crafters.size(); ++i){
			ICrafting crafter = (ICrafting)crafters.get(i);
			if (prevReqStardust != tileEnergyExtractionTable.getRequiredStardust())crafter.sendProgressBarUpdate(this,0,tileEnergyExtractionTable.getRequiredStardust());
			if (prevTime != tileEnergyExtractionTable.getTime())crafter.sendProgressBarUpdate(this,1,tileEnergyExtractionTable.getTime());
			if (prevContainedEnergy != tileEnergyExtractionTable.getContainedEnergy())crafter.sendProgressBarUpdate(this,2,tileEnergyExtractionTable.getContainedEnergy());
		}

		prevReqStardust = tileEnergyExtractionTable.getRequiredStardust();
		prevTime = tileEnergyExtractionTable.getTime();
		prevContainedEnergy = tileEnergyExtractionTable.getContainedEnergy();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int value){
		if (id == 0)tileEnergyExtractionTable.setRequiredStardust(value);
		else if (id == 1)tileEnergyExtractionTable.setTime(value);
		else if (id == 2)tileEnergyExtractionTable.setContainedEnergy(value);
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player){
		return tileEnergyExtractionTable.isUseableByPlayer(player);
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotId){
		ItemStack is = null;
		Slot slot = (Slot)inventorySlots.get(slotId);

		if (slot != null && slot.getHasStack()){
			ItemStack is2 = slot.getStack();
			is = is2.copy();

			if (slotId < 3){
				if (!mergeItemStack(is2,11,inventorySlots.size(),true))return null;
			}
			else{
				if (is2.getItem() == ItemList.stardust){
					if (!mergeItemStack(is2,1,2,false) && !mergeItemStack(is2,0,1,false))return null;
				}
				else if (is2.getItem() == ItemList.instability_orb){
					if (!mergeItemStack(is2,2,3,false) && !mergeItemStack(is2,0,1,false))return null;
				}
				else if (!mergeItemStack(is2,0,1,false))return null;
			}

			if (is2.stackSize == 0)slot.putStack(null);
			else slot.onSlotChanged();
		}

		return is;
	}
}

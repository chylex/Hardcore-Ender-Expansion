package chylex.hee.gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import chylex.hee.gui.slots.SlotBasicItem;
import chylex.hee.gui.slots.SlotTableSubject;
import chylex.hee.item.ItemList;
import chylex.hee.tileentity.TileEntityAbstractTable;
import chylex.hee.tileentity.TileEntityExtractionTable;

public class ContainerExtractionTable extends ContainerAbstractTable{
	public ContainerExtractionTable(InventoryPlayer inv, TileEntityExtractionTable tile){
		super(inv,tile);
	}
	
	@Override
	protected void registerSlots(TileEntityAbstractTable table){
		addSlotToContainer(new SlotTableSubject(table,0,40,17));
		addSlotToContainer(new SlotBasicItem(table,1,40,53,ItemList.stardust));
		addSlotToContainer(new SlotBasicItem(table,2,121,53,ItemList.instability_orb,16));
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

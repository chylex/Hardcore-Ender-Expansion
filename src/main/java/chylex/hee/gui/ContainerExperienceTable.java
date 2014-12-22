package chylex.hee.gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import chylex.hee.item.ItemList;
import chylex.hee.tileentity.TileEntityExperienceTable;

public class ContainerExperienceTable extends ContainerAbstractTable{
	public ContainerExperienceTable(InventoryPlayer inv, TileEntityExperienceTable tile){
		super(inv,tile);
		
		addSlotToContainer(new SlotTableSubject(tile,0,52,17));
		addSlotToContainer(new SlotBasicItem(tile,1,52,53,ItemList.stardust));
		addSlotToContainer(new SlotReadOnly(tile,2,108,35));
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotId){
		ItemStack is = null;
		Slot slot = (Slot)inventorySlots.get(slotId);

		if (slot != null && slot.getHasStack()){
			ItemStack is2 = slot.getStack();
			is = is2.copy();

			if (slotId < 11){
				if (!mergeItemStack(is2,3,inventorySlots.size(),true))return null;
			}
			else{
				if (is2.getItem() == ItemList.stardust){
					if (!mergeItemStack(is2,1,2,false))return null;
				}
				else if (!mergeItemStack(is2,0,3,false))return null;
			}

			if (is2.stackSize == 0)slot.putStack(null);
			else slot.onSlotChanged();
		}

		return is;
	}
}

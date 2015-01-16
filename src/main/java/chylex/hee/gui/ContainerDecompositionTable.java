package chylex.hee.gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import chylex.hee.gui.slots.SlotBasicItem;
import chylex.hee.gui.slots.SlotTableSubject;
import chylex.hee.item.ItemList;
import chylex.hee.tileentity.TileEntityAbstractTable;
import chylex.hee.tileentity.TileEntityDecompositionTable;

public class ContainerDecompositionTable extends ContainerAbstractTable{
	public ContainerDecompositionTable(InventoryPlayer inv, TileEntityDecompositionTable tile){
		super(inv,tile);
	}
	
	@Override
	protected void registerSlots(TileEntityAbstractTable table){
		addSlotToContainer(new SlotTableSubject(table,0,34,17));
		addSlotToContainer(new SlotBasicItem(table,1,34,53,ItemList.stardust));
		
		for(int i = 0; i < 3; ++i){
			for(int j = 0; j < 3; ++j)addSlotToContainer(new Slot(table,2+i*3+j,90+j*18,17+i*18));
		}
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotId){
		ItemStack is = null;
		Slot slot = (Slot)inventorySlots.get(slotId);

		if (slot != null && slot.getHasStack()){
			ItemStack is2 = slot.getStack();
			is = is2.copy();

			if (slotId < 11){
				if (!mergeItemStack(is2,11,inventorySlots.size(),true))return null;
			}
			else{
				if (is2.getItem() == ItemList.stardust){
					if (!mergeItemStack(is2,1,2,false))return null;
				}
				else if (!mergeItemStack(is2,0,11,false))return null;
			}

			if (is2.stackSize == 0)slot.putStack(null);
			else slot.onSlotChanged();
		}

		return is;
	}
}

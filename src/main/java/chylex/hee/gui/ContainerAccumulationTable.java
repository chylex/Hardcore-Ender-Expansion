package chylex.hee.gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import chylex.hee.item.ItemAbstractEnergyAcceptor;
import chylex.hee.tileentity.TileEntityAbstractTable;
import chylex.hee.tileentity.TileEntityAccumulationTable;

public class ContainerAccumulationTable extends ContainerAbstractTable{
	public ContainerAccumulationTable(InventoryPlayer inv, TileEntityAccumulationTable tile){
		super(inv,tile);
	}
	
	@Override
	protected void registerSlots(TileEntityAbstractTable table){
		addSlotToContainer(new SlotEnergyAcceptor(table,0,109,35));
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotId){
		ItemStack is = null;
		Slot slot = (Slot)inventorySlots.get(slotId);

		if (slot != null && slot.getHasStack()){
			ItemStack is2 = slot.getStack();
			is = is2.copy();
			
			if (slotId == 0){
				if (!mergeItemStack(is2,1,inventorySlots.size(),true))return null;
			}
			else if (!(is.getItem() instanceof ItemAbstractEnergyAcceptor) || !mergeItemStack(is2,0,1,false))return null;

			if (is2.stackSize == 0)slot.putStack(null);
			else slot.onSlotChanged();
		}

		return is;
	}
}

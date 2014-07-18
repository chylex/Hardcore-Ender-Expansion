package chylex.hee.gui;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import chylex.hee.item.ItemList;
import chylex.hee.tileentity.TileEntityDecompositionTable;

public class ContainerDecompositionTable extends Container{
	private TileEntityDecompositionTable tileDecompositionTable;
	private int prevReqStardust = 0, prevTime = 0;
	
	public ContainerDecompositionTable(InventoryPlayer inv, TileEntityDecompositionTable tile){
		tileDecompositionTable = tile;
		
		addSlotToContainer(new SlotTableSubject(tile,0,34,17));
		addSlotToContainer(new SlotBasicItem(tile,1,34,53,ItemList.stardust));
		
		for(int i = 0; i < 3; ++i){
			for(int j = 0; j < 3; ++j)addSlotToContainer(new Slot(tile,2+i*3+j,90+j*18,17+i*18));
		}
		
		for(int i = 0; i < 3; ++i){
			for(int j = 0; j < 9; ++j)addSlotToContainer(new Slot(inv,j+i*9+9,8+j*18,84+i*18));
		}

		for(int i = 0; i < 9; ++i)addSlotToContainer(new Slot(inv,i,8+i*18,142));
	}
	
	@Override
	public void addCraftingToCrafters(ICrafting crafter){
		super.addCraftingToCrafters(crafter);
		crafter.sendProgressBarUpdate(this,0,tileDecompositionTable.getRequiredStardust());
		crafter.sendProgressBarUpdate(this,1,tileDecompositionTable.getTime());
	}

	@Override
	public void detectAndSendChanges(){
		super.detectAndSendChanges();

		for(int i = 0; i < crafters.size(); ++i){
			ICrafting crafter = (ICrafting)crafters.get(i);
			if (prevReqStardust != tileDecompositionTable.getRequiredStardust())crafter.sendProgressBarUpdate(this,0,tileDecompositionTable.getRequiredStardust());
			if (prevTime != tileDecompositionTable.getTime())crafter.sendProgressBarUpdate(this,1,tileDecompositionTable.getTime());
		}

		prevReqStardust = tileDecompositionTable.getRequiredStardust();
		prevTime = tileDecompositionTable.getTime();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int value){
		if (id == 0)tileDecompositionTable.setRequiredStardust(value);
		else if (id == 1)tileDecompositionTable.setTime(value);
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player){
		return tileDecompositionTable.isUseableByPlayer(player);
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotId){
		ItemStack is = null;
		Slot slot = (Slot)inventorySlots.get(slotId);

		if (slot != null && slot.getHasStack()){
			ItemStack is2 = slot.getStack();
			is = is2.copy();

			if (slotId == 1){
				if (!mergeItemStack(is2,11,inventorySlots.size(),true))return null;
			}
			else if (slotId < 11){
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

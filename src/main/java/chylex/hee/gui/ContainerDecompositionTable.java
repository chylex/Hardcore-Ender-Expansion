package chylex.hee.gui;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import chylex.hee.gui.slots.SlotBasicItem;
import chylex.hee.gui.slots.SlotTableSubject;
import chylex.hee.init.ItemList;
import chylex.hee.tileentity.TileEntityDecompositionTable;

public class ContainerDecompositionTable extends ContainerAbstractTable{
	public ContainerDecompositionTable(InventoryPlayer inv, TileEntityDecompositionTable tile){
		super(inv,tile);
	}
	
	@Override
	protected void registerSlots(){
		addSlotToContainer(new SlotBasicItem(table,TileEntityDecompositionTable.slotStardust,34,53,ItemList.stardust));
		addSlotToContainer(new SlotTableSubject(table,TileEntityDecompositionTable.slotSubject,34,17));
		
		for(int row = 0; row < 3; row++){
			for(int col = 0; col < 3; col++)addSlotToContainer(new Slot(table,2+row*3+col,90+col*18,17+row*18));
		}
	}
}

package chylex.hee.gui;
import net.minecraft.entity.player.InventoryPlayer;
import chylex.hee.gui.slots.SlotBasicItem;
import chylex.hee.gui.slots.SlotTableSubject;
import chylex.hee.init.ItemList;
import chylex.hee.tileentity.TileEntityExtractionTable;

public class ContainerExtractionTable extends ContainerAbstractTable{
	public ContainerExtractionTable(InventoryPlayer inv, TileEntityExtractionTable tile){
		super(inv,tile);
	}
	
	@Override
	protected void registerSlots(){
		addSlotToContainer(new SlotBasicItem(table,TileEntityExtractionTable.slotStardust,40,53,ItemList.stardust));
		addSlotToContainer(new SlotBasicItem(table,TileEntityExtractionTable.slotOrb,121,53,ItemList.instability_orb,16));
		addSlotToContainer(new SlotTableSubject(table,TileEntityExtractionTable.slotSubject,40,17));
	}
}

package chylex.hee.gui;
import net.minecraft.entity.player.InventoryPlayer;
import chylex.hee.gui.slots.SlotBasicItem;
import chylex.hee.gui.slots.SlotReadOnly;
import chylex.hee.gui.slots.SlotTableSubject;
import chylex.hee.init.ItemList;
import chylex.hee.tileentity.TileEntityExperienceTable;

public class ContainerExperienceTable extends ContainerAbstractTable{
	public ContainerExperienceTable(InventoryPlayer inv, TileEntityExperienceTable tile){
		super(inv, tile);
	}
	
	@Override
	protected void registerSlots(){
		addSlotToContainer(new SlotBasicItem(table, TileEntityExperienceTable.slotStardust, 52, 53, ItemList.stardust));
		addSlotToContainer(new SlotTableSubject(table, TileEntityExperienceTable.slotSubject, 52, 17));
		addSlotToContainer(new SlotReadOnly(table, TileEntityExperienceTable.slotOutput, 108, 35));
	}
}

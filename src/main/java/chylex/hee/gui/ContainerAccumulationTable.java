package chylex.hee.gui;
import net.minecraft.entity.player.InventoryPlayer;
import chylex.hee.gui.slots.SlotEnergyAcceptor;
import chylex.hee.tileentity.TileEntityAccumulationTable;

public class ContainerAccumulationTable extends ContainerAbstractTable{
	public ContainerAccumulationTable(InventoryPlayer inv, TileEntityAccumulationTable tile){
		super(inv, tile);
	}
	
	@Override
	protected void registerSlots(){
		addSlotToContainer(new SlotEnergyAcceptor(table, 0, 109, 35));
	}
}

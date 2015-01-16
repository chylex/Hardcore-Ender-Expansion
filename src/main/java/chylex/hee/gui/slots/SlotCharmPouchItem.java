package chylex.hee.gui.slots;
import net.minecraft.inventory.IInventory;
import chylex.hee.gui.ContainerCharmPouch;
import chylex.hee.item.ItemList;

public class SlotCharmPouchItem extends SlotBasicItem{
	private final ContainerCharmPouch pouchContainer;
	
	public SlotCharmPouchItem(IInventory inv, ContainerCharmPouch pouchContainer, int id, int x, int z){
		super(inv,id,x,z,ItemList.charm,1);
		this.pouchContainer = pouchContainer;
	}
	
	@Override
	public void onSlotChanged(){
		super.onSlotChanged();
		pouchContainer.saveCharmPouch();
	}
}

package chylex.hee.gui.slots;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import chylex.hee.gui.ContainerCharmPouch;
import chylex.hee.item.ItemList;

public class SlotCharmPouchRune extends SlotBasicItem{
	private final ContainerCharmPouch pouchContainer;
	
	public SlotCharmPouchRune(IInventory inv, ContainerCharmPouch pouchContainer, int id, int x, int z, Item validItem, int stackLimit){
		super(inv,id,x,z,ItemList.rune,16);
		this.pouchContainer = pouchContainer;
	}
	
	@Override
	public void onSlotChanged(){
		super.onSlotChanged();
		pouchContainer.onCraftMatrixChanged(inventory);
	}
}

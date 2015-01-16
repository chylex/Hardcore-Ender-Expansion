package chylex.hee.gui.slots;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import chylex.hee.mechanics.brewing.PotionTypes;

public class SlotBrewingStandIngredient extends Slot{
	public SlotBrewingStandIngredient(IInventory inv, int id, int x, int z){
		super(inv,id,x,z);
	}

	@Override
	public boolean isItemValid(ItemStack is){
		if (is == null)return false;
		Item item = is.getItem();
		return PotionTypes.getItemIndexes(is).length > 0 || item.isPotionIngredient(is) || PotionTypes.isSpecialIngredient(item);
	}

	@Override
	public int getSlotStackLimit(){
		return 64;
	}
}

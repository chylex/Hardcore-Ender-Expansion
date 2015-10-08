package chylex.hee.mechanics.enhancements;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IEnhanceableTile<T extends Enum<T>>{
	/**
	 * Returns an item to be used for enhancing.
	 */
	default Item getEnhancementItem(){
		return Item.getItemFromBlock(Blocks.air);
	}
	
	/**
	 * Creates a plain ItemStack from the enhancement item.
	 */
	default ItemStack getEnhancementItemStack(){
		return new ItemStack(getEnhancementItem());
	}
	
	/**
	 * Returns a modifiable enhancement list.
	 */
	EnhancementList<T> getEnhancements();
	
	/**
	 * Enhances a new ItemStack returned by the tile entity and returns it.
	 */
	public static <T extends Enum<T>> ItemStack createItemStack(IEnhanceableTile<T> tile){
		ItemStack is = tile.getEnhancementItemStack();
		EnhancementRegistry.<T>getEnhancementList(is).replace(tile.getEnhancements());
		return is;
	}
}

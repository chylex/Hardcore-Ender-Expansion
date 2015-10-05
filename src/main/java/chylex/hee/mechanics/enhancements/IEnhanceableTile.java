package chylex.hee.mechanics.enhancements;
import net.minecraft.item.Item;

public interface IEnhanceableTile<T extends Enum<T>>{
	/**
	 * Returns an item to be used for enhancing.
	 */
	Item getEnhancementItem();
	
	/**
	 * Returns a modifiable enhancement list.
	 */
	EnhancementList<T> getEnhancements();
}

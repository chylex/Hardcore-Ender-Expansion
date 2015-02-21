package chylex.hee.mechanics.enhancements;
import java.util.List;
import net.minecraft.item.ItemStack;

public interface IEnhanceableTile{
	/**
	 * Returns an ItemStack with all enhancements stored in the tile.
	 */
	public ItemStack createEnhancedItemStack();
	
	/**
	 * Returned collection must be modifiable and reflect the internal enhancement values.
	 */
	public List<Enum> getEnhancements();
}

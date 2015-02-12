package chylex.hee.mechanics.enhancements;
import java.util.List;
import net.minecraft.item.ItemStack;

public interface IEnhanceableTile{
	public void loadEnhancementsFromItem(ItemStack is);
	public List<Enum> getEnhancements();
	public ItemStack createEnhancementDisplay();
}

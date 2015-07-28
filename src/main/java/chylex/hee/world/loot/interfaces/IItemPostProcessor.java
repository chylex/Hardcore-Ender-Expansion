package chylex.hee.world.loot.interfaces;
import java.util.Random;
import net.minecraft.item.ItemStack;

@FunctionalInterface
public interface IItemPostProcessor{
	public static final IItemPostProcessor genericPostProcessor = (is, rand) -> is;
	
	public ItemStack processItem(ItemStack is, Random rand);
}

package chylex.hee.world.loot.interfaces;
import java.util.Random;
import net.minecraft.item.ItemStack;

@FunctionalInterface
public interface IItemPostProcessor{
	IItemPostProcessor genericPostProcessor = (is, rand) -> is;
	
	ItemStack processItem(ItemStack is, Random rand);
}

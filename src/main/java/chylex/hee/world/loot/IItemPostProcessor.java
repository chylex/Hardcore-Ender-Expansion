package chylex.hee.world.loot;
import java.util.Random;
import net.minecraft.item.ItemStack;

public interface IItemPostProcessor{
	public static final IItemPostProcessor genericPostProcessor = new IItemPostProcessor(){
		@Override
		public ItemStack processItem(ItemStack is, Random rand){
			return is;
		}
	};
	
	public ItemStack processItem(ItemStack is, Random rand);
}

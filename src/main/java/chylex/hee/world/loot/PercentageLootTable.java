package chylex.hee.world.loot;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.loot.PercentageLootTable.PercentageLootItem;
import chylex.hee.world.loot.interfaces.IItemPostProcessor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class PercentageLootTable<T> extends LootTable<T,PercentageLootItem>{
	private final Set<PercentageLootItem> items = new HashSet<>();
	
	@Override
	PercentageLootItem createLootItem(Item item){
		return new PercentageLootItem(item);
	}
	
	public List<ItemStack> generateLoot(T obj, Random rand){
		List<ItemStack> list = new ArrayList<>();
		
		for(LootItem loot:items){
			ItemStack is = loot.generate(obj,rand);
			
			if (is != null){
				for(IItemPostProcessor processor:postProcessors)is = processor.processItem(is,rand);
				list.add(is);
			}
		}
		
		return list;
	}
	
	public class PercentageLootItem extends LootItem{
		protected PercentageChance<T> chanceGenerator;
		
		PercentageLootItem(Item item){
			super(item);
			items.add(this);
		}
		
		public PercentageLootItem setChances(PercentageChance chanceGenerator){
			this.chanceGenerator = chanceGenerator;
			return this;
		}
		
		@Override
		public ItemStack generate(T obj, Random rand){
			ItemStack is = new ItemStack(item,0,damage == null ? 0 : damage.getDamage(obj,rand));
			
			float[] chances = chanceGenerator.getChances(obj);
			if (chances.length == 0)return null;
			
			float sum = 0F;
			for(float chance:chances)sum += chance;
			
			float randValue = rand.nextFloat();
			
			if (!MathUtil.floatEquals(sum,1F) && (randValue -= 1F-sum) < 0)return null;
			
			for(int index = 0; index < chances.length; index++){
				if ((randValue -= chances[index]) < 0){
					is.stackSize = index;
					return is;
				}
			}
			
			return null;
		}
	}
	
	@FunctionalInterface
	public interface PercentageChance<T>{
		/**
		 * Return an array of chances to generate an item of size index+1.
		 * For example, [ 0.50F, 0.45F ] will assume 50% chance for 1 item, 45% for 2 items, and 5% for no items.
		 */
		float[] getChances(T obj);
	}
}

package chylex.hee.world.loot;
import java.util.Random;
import chylex.hee.system.collections.WeightedList;
import chylex.hee.system.collections.weight.IWeightProvider;
import chylex.hee.world.loot.WeightedLootTable.WeightedLootItem;
import chylex.hee.world.loot.interfaces.IItemPostProcessor;
import chylex.hee.world.loot.interfaces.LootAmountProvider;
import chylex.hee.world.util.IRandomAmount;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class WeightedLootTable extends LootTable<WeightedLootItem>{
	private WeightedList<WeightedLootItem> weightList = new WeightedList<>();
	
	@Override
	WeightedLootItem createLootItem(Item item){
		return new WeightedLootItem(item);
	}
	
	public ItemStack generateWeighted(Object obj, Random rand){
		ItemStack is = weightList.getRandomItem(rand).generate(obj,rand);
		for(IItemPostProcessor processor:postProcessors)is = processor.processItem(is,rand);
		return is;
	}
	
	public class WeightedLootItem extends LootTable.LootItem implements IWeightProvider{
		protected LootAmountProvider amount;
		private short weight;
		
		WeightedLootItem(Item item){
			super(item);
			weightList.add(this);
		}
		
		public WeightedLootItem setAmount(final int amount){
			this.amount = (obj, rand) -> amount;
			return this;
		}
		
		public WeightedLootItem setAmount(final int minAmount, final int maxAmount){
			this.amount = (obj, rand) -> rand.nextInt(maxAmount-minAmount+1)+minAmount;
			return this;
		}
		
		public WeightedLootItem setAmount(final int minAmount, final int maxAmount, final IRandomAmount generator){
			this.amount = (obj, rand) -> generator.generate(rand,minAmount,maxAmount);
			return this;
		}
		
		public WeightedLootItem setAmount(LootAmountProvider amountProvider){
			this.amount = amountProvider;
			return this;
		}
		
		public WeightedLootItem setWeight(int weight){
			this.weight = (short)weight;
			return this;
		}

		@Override
		public int getWeight(){
			return weight;
		}
		
		@Override
		public ItemStack generate(Object obj, Random rand){
			return new ItemStack(item,amount.getAmount(obj,rand),damage == null ? 0 : damage.getDamage(obj,rand));
		}
	}
}

package chylex.hee.world.loot.old;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import chylex.hee.system.collections.WeightedList;
import chylex.hee.world.loot.interfaces.IItemPostProcessor;
import net.minecraft.item.ItemStack;

@Deprecated
public class WeightedLootList extends WeightedList<LootItemStack>{
	private static final long serialVersionUID = -491398574791344180L;
	
	private List<IItemPostProcessor> itemPostProcessors = new ArrayList<>();

	@Deprecated
	public WeightedLootList(LootItemStack...items){
		for(LootItemStack item:items)add(item);
	}
	
	@Override
	@Deprecated
	public WeightedLootList addAll(LootItemStack[] objArray){
		super.addAll(objArray);
		return this;
	}

	@Deprecated
	public WeightedLootList addItemPostProcessor(IItemPostProcessor itemPostProcessor){
		itemPostProcessors.add(itemPostProcessor);
		return this;
	}

	@Deprecated
	public ItemStack generateIS(Random rand){
		LootItemStack is = super.getRandomItem(rand);
		if (is == null)return null;
		
		ItemStack finalIS = is.getIS(rand);
		for(IItemPostProcessor postProcessor:itemPostProcessors)finalIS = postProcessor.processItem(finalIS,rand);
		return finalIS;
	}
	
	/**
	 * Creates a shallow copy of the loot list.
	 */
	@Deprecated
	public WeightedLootList copy(){
		WeightedLootList copy = new WeightedLootList();
		copy.addAll(this);
		copy.itemPostProcessors.addAll(itemPostProcessors);
		return copy;
	}
}
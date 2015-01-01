package chylex.hee.world.loot;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.item.ItemStack;
import chylex.hee.system.collections.WeightedList;

public class WeightedLootList extends WeightedList<LootItemStack>{
	private static final long serialVersionUID = -491398574791344180L;
	
	private List<IItemPostProcessor> itemPostProcessors = new ArrayList<>();
	
	public WeightedLootList(LootItemStack...items){
		for(LootItemStack item:items)add(item);
	}
	
	@Override
	public WeightedLootList addAll(LootItemStack[] objArray){
		super.addAll(objArray);
		return this;
	}
	
	public WeightedLootList addItemPostProcessor(IItemPostProcessor itemPostProcessor){
		itemPostProcessors.add(itemPostProcessor);
		return this;
	}
	
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
	public WeightedLootList copy(){
		WeightedLootList copy = new WeightedLootList();
		copy.addAll(this);
		copy.itemPostProcessors.addAll(itemPostProcessors);
		return copy;
	}
}
package chylex.hee.world.loot;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.item.ItemStack;
import chylex.hee.system.weight.WeightedList;

public class WeightedLootList extends WeightedList<LootItemStack>{
	private static final long serialVersionUID = -491398574791344180L;
	
	private List<IItemPostProcessor> itemPostProcessors = new ArrayList<>();
	
	public WeightedLootList(LootItemStack...items){
		for(LootItemStack item:items)add(item);
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
}
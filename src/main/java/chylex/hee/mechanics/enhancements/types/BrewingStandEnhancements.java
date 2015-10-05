package chylex.hee.mechanics.enhancements.types;
import net.minecraft.item.Item;
import chylex.hee.init.BlockList;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.enhancements.EnhancementData;
import chylex.hee.mechanics.enhancements.EnhancementRegistry;

public enum BrewingStandEnhancements{
	TIER, SPEED, COST;
	
	public static void register(){
		EnhancementData<BrewingStandEnhancements> data = EnhancementRegistry.registerEnhancement(new Item[]{
			ItemList.enhanced_brewing_stand, Item.getItemFromBlock(BlockList.enhanced_brewing_stand)
		},BrewingStandEnhancements.class);
		
		//
	}
}

package chylex.hee.mechanics.enhancements.types;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.enhancements.EnhancementData;
import chylex.hee.mechanics.enhancements.EnhancementRegistry;

public enum SpatialDashGemEnhancements{
	CAPACITY, RANGE, SPEED;
	
	public static void register(){
		EnhancementData<SpatialDashGemEnhancements> data = EnhancementRegistry.registerEnhancement(new Item[]{
			ItemList.spatial_dash_gem
		}, SpatialDashGemEnhancements.class);
		
		data.register(CAPACITY)
		.setMaxLevel(3)
		.addPowder(2, amount -> amount*1.5F)
		.addIngredient(ItemList.ancient_dust, 5, amount -> amount*1.5F);
		
		data.register(RANGE)
		.setMaxLevel(3)
		.addPowder(2, amount -> amount*1.5F)
		.addIngredient(Items.ender_pearl, 5, amount -> amount*1.5F)
		.addIngredient(Items.ender_pearl, 5, amount -> amount*3F)
		.addIngredient(ItemList.stardust, 12, amount -> amount*amount*5, 2);
		
		data.register(SPEED)
		.setMaxLevel(3)
		.addPowder(2, amount -> amount*1.5F);
	}
}

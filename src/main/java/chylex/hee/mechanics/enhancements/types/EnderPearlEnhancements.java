package chylex.hee.mechanics.enhancements.types;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.enhancements.EnhancementData;
import chylex.hee.mechanics.enhancements.EnhancementRegistry;

public enum EnderPearlEnhancements{
	NO_FALL_DAMAGE, NO_GRAVITY, INCREASED_RANGE, DOUBLE_SPEED, EXPLOSIVE, FREEZE, RIDING;
	
	public static void register(){
		EnhancementData<EnderPearlEnhancements> data = EnhancementRegistry.registerEnhancement(new Item[]{
			Items.ender_pearl, ItemList.enhanced_ender_pearl
		},EnderPearlEnhancements.class);
		
		data.setTransformationItem(ItemList.enhanced_ender_pearl);
		
		//
	}
}

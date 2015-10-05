package chylex.hee.mechanics.enhancements.types;
import net.minecraft.item.Item;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.enhancements.EnhancementData;
import chylex.hee.mechanics.enhancements.EnhancementRegistry;

public enum TransferenceGemEnhancements{
	CAPACITY, TOUCH, BEAST;
	
	public static void register(){
		EnhancementData<TransferenceGemEnhancements> data = EnhancementRegistry.registerEnhancement(new Item[]{
			ItemList.transference_gem
		},TransferenceGemEnhancements.class);
		
		//
	}
}

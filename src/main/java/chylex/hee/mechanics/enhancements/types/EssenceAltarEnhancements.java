package chylex.hee.mechanics.enhancements.types;
import net.minecraft.block.Block;
import chylex.hee.init.BlockList;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.enhancements.EnhancementData;
import chylex.hee.mechanics.enhancements.EnhancementRegistry;

public enum EssenceAltarEnhancements{
	RANGE, SPEED, EFFICIENCY;
	
	public static void register(){
		EnhancementData<EssenceAltarEnhancements> data = EnhancementRegistry.registerEnhancement(new Block[]{
			BlockList.essence_altar
		},EssenceAltarEnhancements.class);
		
		data.register(RANGE)
		.setMaxLevel(3)
		.addPowder(2,amount -> amount*1.5F)
		.addIngredient(ItemList.ancient_dust,5,amount -> amount*1.5F);
		
		data.register(SPEED)
		.setMaxLevel(3)
		.addPowder(2,amount -> amount*1.5F)
		.addIngredient(ItemList.ancient_dust,5,amount -> amount*1.5F);
		
		data.register(EFFICIENCY)
		.setMaxLevel(3)
		.addPowder(2,amount -> amount*1.5F)
		.addIngredient(ItemList.ancient_dust,5,amount -> amount*1.5F);
	}
}

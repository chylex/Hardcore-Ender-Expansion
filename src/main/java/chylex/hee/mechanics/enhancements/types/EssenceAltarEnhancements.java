package chylex.hee.mechanics.enhancements.types;
import net.minecraft.block.Block;
import chylex.hee.init.BlockList;
import chylex.hee.mechanics.enhancements.EnhancementData;
import chylex.hee.mechanics.enhancements.EnhancementRegistry;

public enum EssenceAltarEnhancements{
	RANGE, SPEED, EFFICIENCY;
	
	public static void register(){
		EnhancementData<EssenceAltarEnhancements> data = EnhancementRegistry.registerEnhancement(new Block[]{
			BlockList.essence_altar
		},EssenceAltarEnhancements.class);
		
		//
	}
}

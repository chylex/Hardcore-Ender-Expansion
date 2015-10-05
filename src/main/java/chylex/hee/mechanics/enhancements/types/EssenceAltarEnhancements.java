package chylex.hee.mechanics.enhancements.types;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import chylex.hee.init.BlockList;
import chylex.hee.mechanics.enhancements.EnhancementData;
import chylex.hee.mechanics.enhancements.EnhancementRegistry;

public enum EssenceAltarEnhancements{
	RANGE, SPEED, EFFICIENCY;
	
	public static void register(){
		EnhancementData<EssenceAltarEnhancements> data = EnhancementRegistry.registerEnhancement(new Block[]{
			Blocks.tnt, BlockList.enhanced_tnt
		},EssenceAltarEnhancements.class);
		
		data.setTransformationItem(Item.getItemFromBlock(BlockList.enhanced_tnt));
		
		//
	}
}

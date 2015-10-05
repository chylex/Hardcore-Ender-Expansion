package chylex.hee.mechanics.enhancements.types;
import net.minecraft.block.Block;
import chylex.hee.init.BlockList;
import chylex.hee.mechanics.enhancements.EnhancementData;
import chylex.hee.mechanics.enhancements.EnhancementRegistry;

public enum TNTEnhancements{
	NO_BLOCK_DAMAGE, NO_ENTITY_DAMAGE, EXTRA_POWER, TRAP, NOCLIP, FIRE, NO_FUSE;
	
	public static void register(){
		EnhancementData<TNTEnhancements> data = EnhancementRegistry.registerEnhancement(new Block[]{
			BlockList.enhanced_tnt
		},TNTEnhancements.class);
		
		//
	}
}

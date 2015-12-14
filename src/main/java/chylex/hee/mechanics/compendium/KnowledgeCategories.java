package chylex.hee.mechanics.compendium;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import chylex.hee.init.BlockList;
import chylex.hee.mechanics.compendium.content.KnowledgeCategory;

public class KnowledgeCategories{
	public static final KnowledgeCategory
		OVERWORLD = new KnowledgeCategory("ec.title.overworld",new ItemStack(Blocks.stonebrick),KnowledgeRegistrations.ENDERMAN),
		THE_HUB = new KnowledgeCategory("ec.title.theHub",new ItemStack(BlockList.void_portal_frame),KnowledgeRegistrations.THE_HUB);
	
	public static final KnowledgeCategory[] list = new KnowledgeCategory[]{
		OVERWORLD, THE_HUB
	};
}

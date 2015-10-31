package chylex.hee.mechanics.compendium;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import chylex.hee.init.BlockList;
import chylex.hee.mechanics.compendium.content.KnowledgeCategory;

public class KnowledgeCategories{
	public static final KnowledgeCategory
		TEST1 = new KnowledgeCategory("ec.title.stronghold",new ItemStack(Blocks.stonebrick),KnowledgeRegistrations.TEST2),
		TEST2 = new KnowledgeCategory("ec.title.stronghold",new ItemStack(BlockList.end_portal_frame),KnowledgeRegistrations.TEST6),
		TEST3 = new KnowledgeCategory("ec.title.stronghold",new ItemStack(Blocks.end_stone),KnowledgeRegistrations.TEST5);
	
	public static final KnowledgeCategory[] list = new KnowledgeCategory[]{
		TEST1, TEST2, TEST3
	};
}

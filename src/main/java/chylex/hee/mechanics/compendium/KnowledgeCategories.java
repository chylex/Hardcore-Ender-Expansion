package chylex.hee.mechanics.compendium;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import chylex.hee.mechanics.compendium.content.KnowledgeCategory;

public class KnowledgeCategories{
	public static final KnowledgeCategory
		TEST1 = new KnowledgeCategory("ec.title.overworld",new ItemStack(Blocks.stonebrick),KnowledgeRegistrations.ENDERMAN);
	
	public static final KnowledgeCategory[] list = new KnowledgeCategory[]{
		TEST1
	};
}

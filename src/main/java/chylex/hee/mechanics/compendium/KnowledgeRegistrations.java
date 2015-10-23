package chylex.hee.mechanics.compendium;
import chylex.hee.init.BlockList;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.content.objects.IObjectHolder;
import chylex.hee.mechanics.compendium.content.objects.ObjectBlock;

public final class KnowledgeRegistrations{
	public static final KnowledgeObject<? extends IObjectHolder<?>>
	
		TEST1 = $(new ObjectBlock(BlockList.obsidian_falling)),
		TEST2 = $(new ObjectBlock(BlockList.decomposition_table));
	
	public static void initialize(){
		TEST2.setParent(TEST1,0,4);
	}
	
	private static <T extends IObjectHolder<?>> KnowledgeObject<T> $(T holder){
		return new KnowledgeObject<>(holder);
	}
}

package chylex.hee.mechanics.compendium;
import net.minecraft.item.ItemStack;
import chylex.hee.init.BlockList;
import chylex.hee.init.ItemList;
import chylex.hee.item.ItemSpecialEffects;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.content.fragments.FragmentText;
import chylex.hee.mechanics.compendium.content.fragments.KnowledgeFragmentType;
import chylex.hee.mechanics.compendium.content.objects.IObjectHolder;
import chylex.hee.mechanics.compendium.content.objects.ObjectBlock;
import chylex.hee.mechanics.compendium.content.objects.ObjectDummy;

public final class KnowledgeRegistrations{
	public static final KnowledgeObject<? extends IObjectHolder<?>>
		HELP = $(new ObjectDummy("HALP",new ItemStack(ItemList.special_effects,1,ItemSpecialEffects.questionMark))),
		
		TEST1 = $(new ObjectBlock(BlockList.obsidian_falling)),
		TEST2 = $(new ObjectBlock(BlockList.decomposition_table));
	
	public static void initialize(){
		HELP.addFragments(new FragmentText(0).setType(KnowledgeFragmentType.VISIBLE));
		HELP.addFragments(new FragmentText(1).setType(KnowledgeFragmentType.VISIBLE));
		HELP.addFragments(new FragmentText(2).setType(KnowledgeFragmentType.VISIBLE));
		HELP.addFragments(new FragmentText(3).setType(KnowledgeFragmentType.VISIBLE));
		
		TEST2.setParent(TEST1,0,4);
	}
	
	private static <T extends IObjectHolder<?>> KnowledgeObject<T> $(T holder){
		return new KnowledgeObject<>(holder);
	}
}

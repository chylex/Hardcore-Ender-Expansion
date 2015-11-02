package chylex.hee.mechanics.compendium;
import net.minecraft.item.ItemStack;
import chylex.hee.init.BlockList;
import chylex.hee.init.ItemList;
import chylex.hee.item.ItemSpecialEffects;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.content.fragments.FragmentText;
import chylex.hee.mechanics.compendium.content.fragments.KnowledgeFragmentType;
import chylex.hee.mechanics.compendium.content.objects.IObjectHolder;
import chylex.hee.mechanics.compendium.content.objects.ObjectBlock;
import chylex.hee.mechanics.compendium.content.objects.ObjectDummy;

public final class KnowledgeRegistrations{
	public static final KnowledgeObject<? extends IObjectHolder<?>>
		HELP = $(new ObjectDummy("HALP",new ItemStack(ItemList.special_effects,1,ItemSpecialEffects.questionMark)),"ec.title.help"),
		
		TEST1 = $(new ObjectBlock(BlockList.obsidian_falling)),
		TEST2 = $(new ObjectBlock(BlockList.decomposition_table)),
		TEST3 = $(new ObjectBlock(BlockList.end_powder_ore)),
		TEST4 = $(new ObjectBlock(BlockList.endium_block)),
		TEST5 = $(new ObjectBlock(BlockList.ethereal_lantern)),
		TEST6 = $(new ObjectBlock(BlockList.laboratory_floor));
	
	public static void initialize(){
		HELP.setHidden().addFragments(new KnowledgeFragment[]{
			new FragmentText(0).setType(KnowledgeFragmentType.VISIBLE),
			new FragmentText(1).setType(KnowledgeFragmentType.VISIBLE),
			new FragmentText(2).setType(KnowledgeFragmentType.VISIBLE),
			new FragmentText(3).setType(KnowledgeFragmentType.VISIBLE),
			new FragmentText(4).setType(KnowledgeFragmentType.VISIBLE),
			new FragmentText(5).setType(KnowledgeFragmentType.VISIBLE),
			new FragmentText(6).setType(KnowledgeFragmentType.VISIBLE),
			new FragmentText(7).setType(KnowledgeFragmentType.VISIBLE),
			new FragmentText(8).setType(KnowledgeFragmentType.VISIBLE),
			new FragmentText(9).setType(KnowledgeFragmentType.VISIBLE)
		});
		
		TEST2.setParent(TEST1,0,4).addFragments(new KnowledgeFragment[]{
			new FragmentText(20).setType(KnowledgeFragmentType.ESSENTIAL),
			new FragmentText(21).setType(KnowledgeFragmentType.DISCOVERY)
		});
		
		TEST3.setParent(TEST2,-5,3).addParentLine(0,-2).addParentLine(2,-2).addParentLine(2,-3);
		
		TEST4.setParent(TEST2,3,3).setImportant().addChildLine(0,2).addFragments(new KnowledgeFragment[]{
			new FragmentText(40).setType(KnowledgeFragmentType.ESSENTIAL),
			new FragmentText(41).setType(KnowledgeFragmentType.DISCOVERY)
		});
		
		TEST5.setParent(TEST4,0,200);
		
		TEST6.setParent(TEST4,3,8).addParentLine(-3,0);
	}
	
	private static <T extends IObjectHolder<?>> KnowledgeObject<T> $(T holder){
		return new KnowledgeObject<>(holder);
	}
	
	private static <T extends IObjectHolder<?>> KnowledgeObject<T> $(T holder, String tooltip){
		return new KnowledgeObject<>(holder,tooltip);
	}
}

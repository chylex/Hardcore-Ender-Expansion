package chylex.hee.mechanics.compendium;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import chylex.hee.entity.mob.EntityMobEnderman;
import chylex.hee.entity.mob.EntityMobSilverfish;
import chylex.hee.init.BlockList;
import chylex.hee.init.ItemList;
import chylex.hee.item.ItemSpecialEffects;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.content.objects.IObjectHolder;
import chylex.hee.mechanics.compendium.content.objects.ObjectBlock;
import chylex.hee.mechanics.compendium.content.objects.ObjectDummy;
import chylex.hee.mechanics.compendium.content.objects.ObjectItem;
import chylex.hee.mechanics.compendium.content.objects.ObjectMob;
import chylex.hee.system.abstractions.Meta;

public final class KnowledgeRegistrations{
	public static final KnowledgeObject<? extends IObjectHolder<?>>
		HELP = $(new ObjectDummy("HALP",new ItemStack(ItemList.special_effects,1,ItemSpecialEffects.questionMark)),"ec.title.help"),
		
		ENDERMAN = $(new ObjectMob(EntityMobEnderman.class)),
		ENDER_PEARL = $(new ObjectItem(Items.ender_pearl)),
		ETHEREUM = $(new ObjectItem(ItemList.ethereum)),
		ENDERMAN_HEAD = $(new ObjectBlock(BlockList.enderman_head)),
		EYE_OF_ENDER = $(new ObjectItem(Items.ender_eye)),
		
		STRONGHOLD = $(new ObjectDummy("STRONGHOLD",new ItemStack(Blocks.stonebrick))),
		DRY_VINE = $(new ObjectBlock(BlockList.dry_vine)),
		ANCIENT_COBWEB = $(new ObjectBlock(BlockList.ancient_web)),
		STONE_BRICK = $(new ObjectBlock(Blocks.stonebrick)),
		STONE_BRICK_SLAB = $(new ObjectBlock(Blocks.stone_slab,Meta.slabStoneBrickBottom)),
		STONE_BRICK_STAIRS = $(new ObjectBlock(Blocks.stone_brick_stairs)),
		STONE_BRICK_WALL = $(new ObjectBlock(BlockList.stone_brick_wall)),
		SILVERFISH = $(new ObjectMob(EntityMobSilverfish.class)),
		ANCIENT_DUST = $(new ObjectItem(ItemList.ancient_dust)),
		END_PORTAL = $(new ObjectBlock(BlockList.end_portal_frame))
		;
	
	public static void initialize(){
		HELP.setHidden().addFragments(new KnowledgeFragment[]{
			
		});
		
		ENDERMAN.setCategoryObject().setImportant();
		
		ENDER_PEARL.setParent(ENDERMAN,-3,5).addParentLine(0,-2);
		
		ETHEREUM.setParent(ENDERMAN,3,5).addParentLine(0,-2);
		
		ENDERMAN_HEAD.setParent(ENDERMAN,0,3);
		
		EYE_OF_ENDER.setParent(ENDER_PEARL,3,3).addParent(ETHEREUM).addParentLine(0,-3);
		
		STRONGHOLD.setParent(EYE_OF_ENDER,0,4).setImportant();
		
		DRY_VINE.setParent(STRONGHOLD,0,7);
		
		ANCIENT_COBWEB.setParent(STRONGHOLD,-2,7).addParentLine(0,-2).addParentLine(1,-3).addParentLine(2,-3).addChildLine(0,2);
		
		STONE_BRICK.setParent(STRONGHOLD,-6,2).addParentLine(2,-2);
		
		STONE_BRICK_SLAB.setParent(STONE_BRICK,-5,2).addParentLine(2,0).addParentLine(2,-1).addParentLine(3,-2);
		
		STONE_BRICK_STAIRS.setParent(STONE_BRICK,-5,4).addParentLine(2,0).addParentLine(2,-3).addParentLine(3,-4);
		
		STONE_BRICK_WALL.setParent(STONE_BRICK,-5,6).addParentLine(2,0).addParentLine(2,-5).addParentLine(3,-6);
		
		SILVERFISH.setParent(STONE_BRICK,0,5).addChildLine(0,2);
		
		ANCIENT_DUST.setParent(SILVERFISH,2,4).addParent(ANCIENT_COBWEB);
		
		END_PORTAL.setParent(STRONGHOLD,4,10).addParentLine(0,-9).addParentLine(-1,-10);
		
		/*TEST6.setParent(TEST4,3,8).addParentLine(-3,0).addFragments(new KnowledgeFragment[]{
			new FragmentText(60).setType(KnowledgeFragmentType.HINT)
		});*/
	}
	
	private static <T extends IObjectHolder<?>> KnowledgeObject<T> $(T holder){
		return new KnowledgeObject<>(holder);
	}
	
	private static <T extends IObjectHolder<?>> KnowledgeObject<T> $(T holder, String tooltip){
		return new KnowledgeObject<>(holder,tooltip);
	}
}

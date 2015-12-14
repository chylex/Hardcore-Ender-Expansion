package chylex.hee.mechanics.compendium;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import chylex.hee.entity.mob.EntityMobEnderman;
import chylex.hee.entity.mob.EntityMobSilverfish;
import chylex.hee.init.BlockList;
import chylex.hee.init.ItemList;
import chylex.hee.item.ItemPortalToken;
import chylex.hee.item.ItemSpecialEffects;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.content.objects.IObjectHolder;
import chylex.hee.mechanics.compendium.content.objects.ObjectBlock;
import chylex.hee.mechanics.compendium.content.objects.ObjectDummy;
import chylex.hee.mechanics.compendium.content.objects.ObjectItem;
import chylex.hee.mechanics.compendium.content.objects.ObjectMob;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.world.end.EndTerritory;

public final class KnowledgeRegistrations{
	public static final KnowledgeObject<? extends IObjectHolder<?>>
		HELP = $(new ObjectDummy("HALP",new ItemStack(ItemList.special_effects,1,ItemSpecialEffects.questionMark)),"ec.title.help"),
		
		ENDERMAN = $(new ObjectMob(EntityMobEnderman.class)),
		ENDER_PEARL = $(new ObjectItem(Items.ender_pearl)),
		ETHEREUM = $(new ObjectItem(ItemList.ethereum)),
		ENDERMAN_HEAD = $(new ObjectBlock(BlockList.enderman_head)),
		EYE_OF_ENDER = $(new ObjectItem(Items.ender_eye)),
		
		STRONGHOLD = $(new ObjectDummy("STRONGHOLD",new ItemStack(Blocks.stonebrick)),"ec.title.stronghold"),
		DRY_VINE = $(new ObjectBlock(BlockList.dry_vine)),
		ANCIENT_COBWEB = $(new ObjectBlock(BlockList.ancient_web)),
		STONE_BRICK = $(new ObjectBlock(Blocks.stonebrick)),
		STONE_BRICK_SLAB = $(new ObjectBlock(Blocks.stone_slab,Meta.slabStoneBrickBottom)),
		STONE_BRICK_STAIRS = $(new ObjectBlock(Blocks.stone_brick_stairs)),
		STONE_BRICK_WALL = $(new ObjectBlock(BlockList.stone_brick_wall)),
		SILVERFISH = $(new ObjectMob(EntityMobSilverfish.class)),
		ANCIENT_DUST = $(new ObjectItem(ItemList.ancient_dust)),
		ALTERATION_NEXUS = $(new ObjectItem(ItemList.alteration_nexus)),
		ETHEREAL_LANTERN = $(new ObjectBlock(BlockList.ethereal_lantern)),
		END_PORTAL = $(new ObjectBlock(BlockList.end_portal_frame),"tile.endPortal.name"),
		
		ENERGY_RECEPTACLE = $(new ObjectItem(ItemList.energy_receptacle)),
		ENERGY_ORACLE = $(new ObjectItem(ItemList.energy_oracle)),
		SPATIAL_DASH_GEM = $(new ObjectItem(ItemList.spatial_dash_gem)),
		AMULET_OF_RECOVERY = $(new ObjectItem(ItemList.amulet_of_recovery)),
		ENDER_CHEST = $(new ObjectBlock(Blocks.ender_chest)),
		
		ENERGY_SHRINE = $(new ObjectDummy("ENERGYSHRINE",new ItemStack(BlockList.gloomrock)),"ec.title.energyShrine"),
		ENERGY_CLUSTER = $(new ObjectBlock(BlockList.energy_cluster)),
		GLOOMROCK = $(new ObjectBlock(BlockList.gloomrock)),
		GLOOMROCK_SMOOTH_SLAB = $(new ObjectBlock(BlockList.gloomrock_smooth_slab)),
		GLOOMROCK_SMOOTH_STAIRS = $(new ObjectBlock(BlockList.gloomrock_smooth_stairs)),
		GLOOMROCK_BRICK_SLAB = $(new ObjectBlock(BlockList.gloomrock_brick_slab)),
		GLOOMROCK_BRICK_STAIRS = $(new ObjectBlock(BlockList.gloomrock_brick_stairs)),
		GLOOMTORCH = $(new ObjectBlock(BlockList.gloomtorch)),
		
		THE_HUB = $(new ObjectDummy("THEHUB",new ItemStack(BlockList.void_portal_frame,1,Meta.voidPortalFrameStorage)),"ec.title.theHub"),
		END_STONE = $(new ObjectBlock(Blocks.end_stone)),
		END_POWDER_ORE = $(new ObjectBlock(BlockList.end_powder_ore)),
		END_POWDER = $(new ObjectItem(ItemList.end_powder)),
		VOID_PORTAL = $(new ObjectBlock(BlockList.void_portal_frame),"tile.voidPortal.name"),
		PORTAL_TOKEN = $(new ObjectItem(ItemList.portal_token,ItemPortalToken.forTerritory(EndTerritory.DEBUG_TEST,false)))
		;
	
	public static void initialize(){
		HELP.setHidden().addFragments(new KnowledgeFragment[]{
			
		});
		
		//
		
		ENDERMAN.setCategoryObject().setImportant();
		
		ENDER_PEARL.setParent(ENDERMAN,-3,5).addParentLine(0,-2);
		
		ETHEREUM.setParent(ENDERMAN,3,5).addParentLine(0,-2);
		
		ENDERMAN_HEAD.setParent(ENDERMAN,0,3);
		
		EYE_OF_ENDER.setParent(ENDER_PEARL,3,3).addParent(ETHEREUM).addParentLine(0,-3);
		
		STRONGHOLD.setParent(EYE_OF_ENDER,0,5).setSpecial();
		
		DRY_VINE.setParent(STRONGHOLD,-1,7).addParentLine(0,-3).addParentLine(1,-4);
		
		ANCIENT_COBWEB.setParent(STRONGHOLD,-3,7).addParentLine(0,-2).addParentLine(1,-3).addParentLine(2,-3).addParentLine(3,-4).addChildLine(0,2);
		
		STONE_BRICK.setParent(STRONGHOLD,-7,2).addParentLine(2,-2);
		
		STONE_BRICK_SLAB.setParent(STONE_BRICK,-5,2).addParentLine(2,0).addParentLine(2,-1).addParentLine(3,-2);
		
		STONE_BRICK_STAIRS.setParent(STONE_BRICK,-5,4).addParentLine(2,0).addParentLine(2,-3).addParentLine(3,-4);
		
		STONE_BRICK_WALL.setParent(STONE_BRICK,-5,6).addParentLine(2,0).addParentLine(2,-5).addParentLine(3,-6);
		
		SILVERFISH.setParent(STONE_BRICK,0,5).addChildLine(0,2);
		
		ANCIENT_DUST.setParent(SILVERFISH,2,4).addParent(ANCIENT_COBWEB);
		
		ALTERATION_NEXUS.setParent(ANCIENT_DUST,0,4).setImportant();
		
		ETHEREAL_LANTERN.setParent(STRONGHOLD,-3,2).addParentLine(0,-2);
		
		END_PORTAL.setParent(STRONGHOLD,3,11).setImportant().addParentLine(0,-10).addParentLine(-1,-11);
		
		//
		
		ENERGY_ORACLE.setParent(END_PORTAL,-3,4).addParentLine(2,0).addParentLine(3,-1);
		
		ENERGY_RECEPTACLE.setParent(END_PORTAL,3,4).addParentLine(-2,0).addParentLine(-3,-1).addChildLine(0,7);
		
		SPATIAL_DASH_GEM.setParent(STRONGHOLD,9,6).setImportant().addParentLine(-2,0).addParentLine(-2,-2).addParentLine(-3,-3).addParentLine(-6,-3).addParentLine(-6,-5).addParentLine(-7,-6);
		
		AMULET_OF_RECOVERY.setParent(STRONGHOLD,9,8).setImportant().addParentLine(-2,0).addParentLine(-2,-4).addParentLine(-3,-5).addParentLine(-6,-5).addParentLine(-6,-7).addParentLine(-7,-8);
		
		ENDER_CHEST.setParent(ALTERATION_NEXUS,-4,2).addParentLine(2,-2);
		
		//
		
		ENERGY_SHRINE.setParent(ENERGY_ORACLE,0,7).setSpecial();
		
		ENERGY_CLUSTER.setParent(ENERGY_SHRINE,3,6).addParent(ENERGY_RECEPTACLE).addParentLine(0,-3).setImportant();
		
		GLOOMROCK.setParent(ENERGY_SHRINE,-4,2).addParentLine(2,-2);
		
		GLOOMTORCH.setParent(GLOOMROCK,0,4);
		
		GLOOMROCK_BRICK_SLAB.setParent(GLOOMROCK,-5,2).addParentLine(2,0).addParentLine(2,-1).addParentLine(3,-2);
		
		GLOOMROCK_BRICK_STAIRS.setParent(GLOOMROCK,-5,4).addParentLine(2,0).addParentLine(2,-3).addParentLine(3,-4);
		
		GLOOMROCK_SMOOTH_SLAB.setParent(GLOOMROCK,-5,6).addParentLine(2,0).addParentLine(2,-5).addParentLine(3,-6);
		
		GLOOMROCK_SMOOTH_STAIRS.setParent(GLOOMROCK,-5,8).addParentLine(2,0).addParentLine(2,-7).addParentLine(3,-8);
		
		//
		
		THE_HUB.setCategoryObject().setParent(END_PORTAL,-3,27).addParentLine(0,-5).addParentLine(1,-6).addParentLine(8,-6).addParentLine(9,-7).addParentLine(9,-25).addParentLine(7,-27).addChildLine(0,3).setSpecial();
		
		END_STONE.setParent(THE_HUB,-3,6).addParentLine(0,-2).addParentLine(1,-3);
		
		END_POWDER_ORE.setParent(END_STONE,-3,3).addParentLine(2,0).addParentLine(3,-1);
		
		END_POWDER.setParent(END_POWDER_ORE,0,3).setImportant();
		
		VOID_PORTAL.setParent(THE_HUB,3,6).addParentLine(0,-2).addParentLine(-1,-3).setImportant();
		
		PORTAL_TOKEN.setParent(THE_HUB,6,6).addParentLine(0,-2).addParentLine(-1,-2).addParentLine(-3,-2).addParentLine(-4,-3);
	}
	
	private static <T extends IObjectHolder<?>> KnowledgeObject<T> $(T holder){
		return new KnowledgeObject<>(holder);
	}
	
	private static <T extends IObjectHolder<?>> KnowledgeObject<T> $(T holder, String tooltip){
		return new KnowledgeObject<>(holder,tooltip);
	}
}

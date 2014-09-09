package chylex.hee.mechanics.compendium;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import chylex.hee.block.BlockCrossedDecoration;
import chylex.hee.block.BlockList;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.EntityMiniBossEnderEye;
import chylex.hee.entity.mob.EntityMobAngryEnderman;
import chylex.hee.entity.mob.EntityMobBabyEnderman;
import chylex.hee.entity.mob.EntityMobEnderGuardian;
import chylex.hee.entity.mob.EntityMobFireGolem;
import chylex.hee.entity.mob.EntityMobHauntedMiner;
import chylex.hee.entity.mob.EntityMobInfestedBat;
import chylex.hee.entity.mob.EntityMobLouse;
import chylex.hee.entity.mob.EntityMobScorchingLens;
import chylex.hee.entity.mob.EntityMobVampiricBat;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.mechanics.compendium.content.KnowledgeFragmentCrafting;
import chylex.hee.mechanics.compendium.content.KnowledgeFragmentText;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.content.KnowledgeObject.LinkedKnowledgeObject;
import chylex.hee.mechanics.compendium.objects.IKnowledgeObjectInstance;
import chylex.hee.mechanics.compendium.objects.ObjectBlock;
import chylex.hee.mechanics.compendium.objects.ObjectDummy;
import chylex.hee.mechanics.compendium.objects.ObjectItem;
import chylex.hee.mechanics.compendium.objects.ObjectMob;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.system.logging.Log;
import chylex.hee.system.logging.Stopwatch;

public final class KnowledgeRegistrations{
	public static final KnowledgeObject<? extends IKnowledgeObjectInstance<?>>
		HELP = new KnowledgeObject<>(new ObjectDummy("HalpPlz")),
		
		// ===
		
		STRONGHOLD = dummy("Stronghold",new ItemStack(Blocks.stonebrick),"Stronghold"),
		ADVENTURERS_DIARY = create(ItemList.adventurers_diary),
		ENDERMAN_HEAD = create(ItemList.enderman_head),
		MUSIC_DISKS = create(ItemList.music_disk),
		ALTAR_NEXUS = create(ItemList.altar_nexus),
		BASIC_ESSENCE_ALTAR = create(BlockList.essence_altar,EssenceType.INVALID.id),
		ENDERMAN = create(EntityEnderman.class),
		SILVERFISH = create(EntitySilverfish.class),
		
		ESSENCE = create(ItemList.essence),
		
		// ===
		
		DRAGON_LAIR = dummy("DragonLair",new ItemStack(Blocks.dragon_egg),"Dragon Lair"),
		END_STONE = create(Blocks.end_stone),
		FALLING_OBSIDIAN = create(BlockList.obsidian_falling),
		DRAGON_ESSENCE_ALTAR = create(BlockList.essence_altar,EssenceType.DRAGON.id),
		END_POWDER_ORE = create(BlockList.end_powder_ore),
		ENHANCED_BREWING_STAND = create(ItemList.enhanced_brewing_stand),
		ENHANCED_TNT = create(BlockList.enhanced_tnt),
		DRAGON_EGG = create(Blocks.dragon_egg),
		DRAGON_ESSENCE = link(ESSENCE,new ItemStack(ItemList.essence,EssenceType.DRAGON.getItemDamage()),"Dragon Essence"), // TODO localize
		END_POWDER = create(ItemList.end_powder),
		ENHANCED_ENDER_PEARL = create(ItemList.enhanced_ender_pearl),
		TEMPLE_CALLER = create(ItemList.temple_caller),
		ENDER_DRAGON = create(EntityBossDragon.class),
		ANGRY_ENDERMAN = create(EntityMobAngryEnderman.class),
		VAMPIRE_BAT = create(EntityMobVampiricBat.class),
		
		// ===
		
		ENDSTONE_BLOB = dummy("EndstoneBlob",new ItemStack(Blocks.end_stone),"Endstone Blob"),
		IGNEOUS_ROCK_ORE = create(BlockList.igneous_rock_ore),
		DEATH_FLOWER = create(BlockList.death_flower),
		ENDER_GOO = create(ItemList.bucket_ender_goo),
		IGNEOUS_ROCK = create(ItemList.igneous_rock),
		
		// ===
		
		DUNGEON_TOWER = dummy("DungeonTower",new ItemStack(BlockList.obsidian_special,1),"Dungeon Tower"),
		OBSIDIAN_STAIRS = create(BlockList.obsidian_stairs),
		OBSIDIAN_SMOOTH = create(BlockList.obsidian_special,0),
		OBSIDIAN_CHISELED = create(BlockList.obsidian_special,1),
		OBSIDIAN_PILLAR = create(BlockList.obsidian_special,2),
		OBSIDIAN_SMOOTH_GLOWING = create(BlockList.obsidian_special_glow,0),
		OBSIDIAN_CHISELED_GLOWING = create(BlockList.obsidian_special_glow,1),
		OBSIDIAN_PILLAR_GLOWING = create(BlockList.obsidian_special_glow,2),
		SPATIAL_DASH_GEM = create(ItemList.spatial_dash_gem),
		BIOME_COMPASS = create(ItemList.biome_compass),
		ENDER_EYE = create(EntityMiniBossEnderEye.class),
		ANGRY_ENDERMAN_LINKED = new LinkedKnowledgeObject<>(ANGRY_ENDERMAN),
		
		// ===
		
		METEOROID = dummy("Meteoroid",new ItemStack(BlockList.sphalerite),"Meteoroid"),
		SPHALERITE = create(BlockList.sphalerite,0),
		SPHALERITE_WITH_STARDUST = create(BlockList.sphalerite,1),
		STARDUST = create(ItemList.stardust),
		
		// ===
		
		INSTABILITY_ORB_ORE = create(BlockList.instability_orb_ore),
		STARDUST_ORE = create(BlockList.stardust_ore),
		INSTABILITY_ORB = create(ItemList.instability_orb),
		STARDUST_LINKED = link(STARDUST),
		
		// =>
		
		INFESTED_FOREST_BIOME = dummy("InfestedForest",new ItemStack(BlockList.end_terrain,1,0),"Infested Forest Biome"),
		INFESTED_END_STONE = create(BlockList.end_terrain,0),
		INFESTED_GRASS = create(BlockList.crossed_decoration,BlockCrossedDecoration.dataInfestedGrass),
		INFESTED_TALL_GRASS = create(BlockList.crossed_decoration,BlockCrossedDecoration.dataInfestedTallgrass),
		INFESTED_FERN = create(BlockList.crossed_decoration,BlockCrossedDecoration.dataInfestedFern),
		THORNY_BUSH = create(BlockList.crossed_decoration,BlockCrossedDecoration.dataThornBush),
		
		SPOOKY_TREES = create(BlockList.spooky_log),
		SPOOKY_LEAVES = create(BlockList.spooky_leaves),
		DRY_SPLINTER = create(ItemList.dry_splinter),
		GHOST_AMULET = create(ItemList.ghost_amulet),
		ECTOPLASM = create(ItemList.ectoplasm),
		INFESTED_BAT = create(EntityMobInfestedBat.class),
		SILVERFISH_LINKED = link(SILVERFISH),
		
		RAVAGED_BRICKS = create(BlockList.ravaged_brick),
		RAVAGED_BRICK_GLOWING = create(BlockList.ravaged_brick_glow),
		RAVAGED_BRICK_STAIRS = create(BlockList.ravaged_brick_stairs),
		RAVAGED_BRICK_SLAB = create(BlockList.ravaged_brick_slab),
		RAVAGED_BRICK_FENCE = create(BlockList.ravaged_brick_fence),
		CHARM_POUCH = create(ItemList.charm_pouch),
		RUNES = create(ItemList.rune),
		CHARMS = create(ItemList.charm),
		LOUSE = create(EntityMobLouse.class),
		
		// ===
		
		BURNING_MOUNTAINS_BIOME = dummy("BurningMountains",new ItemStack(BlockList.end_terrain,1,1),"Burning Mountains Biome"),
		BURNING_END_STONE = create(BlockList.end_terrain,1),
		LILYFIRE = create(BlockList.crossed_decoration,BlockCrossedDecoration.dataLilyFire),
		
		IGNEOUS_ROCK_ORE_LINKED = link(IGNEOUS_ROCK_ORE),
		IGNEOUS_ROCK_LINKED = link(IGNEOUS_ROCK),
		CINDER = create(BlockList.cinder),
		FIERY_ESSENCE_ALTAR = create(BlockList.essence_altar,EssenceType.FIERY.id),
		FIERY_ESSENCE = link(ESSENCE,new ItemStack(ItemList.essence,EssenceType.FIERY.getItemDamage()),"Fiery Essence"), // TODO localize
		FIRE_GOLEM = create(EntityMobFireGolem.class),
		SCORCHING_LENS = create(EntityMobScorchingLens.class),
		
		FIRE_SHARD = create(ItemList.fire_shard),
		SCORCHING_PICKAXE = create(ItemList.scorching_pickaxe),
		HAUNTED_MINER = create(EntityMobHauntedMiner.class),
		
		// ===
		
		ENCHANTED_ISLAND_BIOME = dummy("EnchantedIsland",new ItemStack(BlockList.end_terrain,1,2),"Enchanted Island Biome"),
		ENCHANTED_END_STONE = create(BlockList.end_terrain,2),
		
		FALLING_OBSIDIAN_LINKED = link(FALLING_OBSIDIAN),
		ENDERMAN_LINKED = link(ENDERMAN),
		BABY_ENDERMAN = create(EntityMobBabyEnderman.class),
		ENDER_GUARDIAN = create(EntityMobEnderGuardian.class);
	
	public static void initialize(){
		Stopwatch.time("KnowledgeRegistrations");
		
		/*
		 * General information
		 * ===================
		 * Numbers below are not set in stone, they will vary a bit around that value but keep the meaning.
		 * 
		 * Object price
		 * ============
		 *    5 | easily found objects without any feature mechanics
		 *   10 | basic objects with mechanics
		 *   20 | important objects with mechanics
		 *   40 | phase objects (early)
		 *   75 | phase objects (mid)
		 *  100 | phase objects (late)
		 * 
		 * Fragment price
		 * ==============
		 *   1 | bulk data
		 *   2 | unimportant or generic information
		 *   5 | basic information about important objects
		 *   8 | key information
		 *  10 | very important information
		 *
		 * Object discovery reward
		 * =======================
		 * Most objects should give enough points to unlock some/all fragments.
		 * Uncommon and rare objects should give an additional bonus.
		 * Purely visual objects should also give a bonus for discovery.
		 * Objects which are made in a way they should be unlocked with points would only give small reward.
		 */
		
		HELP.setFragments(new KnowledgeFragment[]{
			new KnowledgeFragmentText(0).setContents("Welcome to the Ender Compendium, the source of all knowledge about the End!"),
			new KnowledgeFragmentText(1).setContents("The Compendium is divided into phases, clicking them reveals blocks, items and mobs you can find in that phase."),
			new KnowledgeFragmentText(2).setContents("In order to reveal information about these objects, first you have to either discover them, or spend a specified amount of Knowledge Points."),
			new KnowledgeFragmentText(3).setContents("Then you can spend your points on individual Knowledge Fragments."),
			new KnowledgeFragmentText(4).setContents("Note that discovering objects also unlocks some of their fragments and gives you points, whereas buying the object does neither."),
			new KnowledgeFragmentText(5).setContents("Knowledge Fragments also exist as items found in dungeons and traded by villagers. Using them gives you points or unlocks random fragments."),
			new KnowledgeFragmentText(6).setContents("You can use right mouse button instead of the Back button for easier use of the Compendium.")
		});
		
		KnowledgeCategories.OVERWORLD.addKnowledgeObjects(new KnowledgeObject[]{
			STRONGHOLD.setPos(0,1).setUnlockPrice(5).setFragments(new KnowledgeFragment[]{
				
			}),
			
			ADVENTURERS_DIARY.setPos(0,0).setUnlockPrice(5).setDiscoveryReward(12).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(10).setContents("Short story of an adventurer, split across 16 pages.").setPrice(2).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(11).setContents("Opening a new diary page unlocks next page of the story, and locks the item to only open that page for other players.").setPrice(2).setUnlockOnDiscovery()
			}),
			
			ENDERMAN_HEAD.setPos(1,0).setUnlockPrice(5).setDiscoveryReward(10).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(20).setContents("Rare drop from Endermen.").setPrice(2).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(21).setContents("Drop chance is 1 in 40 (2.5%), Looting enchantment increases the chance.").setPrice(2).setUnlockRequirements(40)
			}),
			
			MUSIC_DISKS.setPos(2,0).setUnlockPrice(5).setDiscoveryReward(15).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(30).setContents("Jukebox discs with various pieces of qwertygiy's music.").setPrice(2)
			}),
			
			ALTAR_NEXUS.setPos(3,0).setUnlockPrice(10).setDiscoveryReward(5).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(40).setContents("Core component of the Basic Essence Altar.").setPrice(8),
				new KnowledgeFragmentCrafting(41).setRecipeFromRegistry(new ItemStack(ItemList.altar_nexus)).setUnlockRequirements(80),
				new KnowledgeFragmentCrafting(42).setRecipeFromRegistry(new ItemStack(BlockList.essence_altar)).setUnlockCascade(100)
			}),
			
			BASIC_ESSENCE_ALTAR.setPos(4,0).setNonBuyable().setDiscoveryReward(20).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentCrafting(50).setRecipeFromRegistry(new ItemStack(BlockList.essence_altar)).setUnlockCascade(82),
				new KnowledgeFragmentText(51).setContents("Basic altar is converted into a specific type of altar by giving it one Essence, and 8 blocks and items it requests.").setUnlockOnDiscovery().setPrice(5).setUnlockRequirements(100),
				new KnowledgeFragmentText(52).setContents("Transformed altars can be given 32 of Essence per right-click, or 1 while sneaking.").setPrice(2).setUnlockRequirements(101),
				new KnowledgeFragmentText(53).setContents("Altars have 4 sockets for precious blocks in the corners. Some of the blocks give an effect and some boost used effects, one of each is required minimum.").setPrice(6).setUnlockRequirements(101),
				new KnowledgeFragmentText(54).setContents("Redstone Block increases altar speed, Lapis Block improves range and Nether Quartz Block makes Essence usage lower.").setPrice(2).setUnlockRequirements(103),
				new KnowledgeFragmentText(55).setContents("Iron Block has effect boost 1, Gold Block 3, Diamond Block 7 and Emerald Block 10.").setPrice(2).setUnlockRequirements(103)
			}),
			
			ENDERMAN.setPos(5,0).setDiscoveryReward(15).setFragments(new KnowledgeFragment[]{
				
			}),
			
			SILVERFISH.setPos(6,0).setDiscoveryReward(25).setFragments(new KnowledgeFragment[]{
				
			}),
			
			ESSENCE.setPos(7,0).setDiscoveryReward(12).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(80).setContents("Essence is used to unleash power of altars.").setPrice(2).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(81).setContents("Dragon Essence is gained by killing the Ender Dragon.").setPrice(2), // TODO cascade to Ender Dragon
				new KnowledgeFragmentText(82).setContents("Fiery Essence is dropped by Fire Golem and Scorching Lens, which can be found in a variation of Burning Mountains.").setPrice(2) // TODO cascade to the mobs
			}),
			
			// ===
			
			DRAGON_LAIR.setPos(0,0).setUnlockPrice(35).setFragments(new KnowledgeFragment[]{
				
			})
		});
		
		Stopwatch.finish("KnowledgeRegistrations");
		
		if (Log.isDebugEnabled()){
			Stopwatch.time("KnowledgeRegistrations - Stats");
			
			int amtObjects = 0, amtFragments = 0, totalObjPrice = 0, totalFragPrice = 0, totalReward = 0;
			
			for(KnowledgeObject<?> obj:KnowledgeObject.getAllObjects()){
				++amtObjects;
				for(KnowledgeFragment fragment:obj.getFragments())totalFragPrice += fragment.getPrice();
				amtFragments += obj.getFragments().size();
				totalObjPrice += obj.getUnlockPrice();
				totalReward += obj.getDiscoveryReward();
			}
			
			Log.debug("Knowledge Object amount: $0",amtObjects);
			Log.debug("Knowledge Fragment amount: $0",amtFragments);
			Log.debug("Total Object price: $0",totalObjPrice);
			Log.debug("Total Fragment price: $0",totalFragPrice);
			Log.debug("Total price: $0",totalObjPrice+totalFragPrice);
			Log.debug("Total discovery reward: $0",totalReward);
			
			Stopwatch.finish("KnowledgeRegistrations - Stats");
		}
	}
	
	public static KnowledgeObject<ObjectBlock> create(Block block){
		return new KnowledgeObject<ObjectBlock>(new ObjectBlock(block));
	}
	
	public static KnowledgeObject<ObjectBlock> create(Block block, int metadata){
		return new KnowledgeObject<ObjectBlock>(new ObjectBlock(block,metadata));
	}
	
	public static KnowledgeObject<ObjectItem> create(Item item){
		return new KnowledgeObject<ObjectItem>(new ObjectItem(item));
	}
	
	public static KnowledgeObject<ObjectMob> create(Class<? extends EntityLiving> mobClass){
		return new KnowledgeObject<ObjectMob>(new ObjectMob(mobClass));
	}
	
	public static KnowledgeObject<ObjectDummy> dummy(String identifier, ItemStack itemToRender, String tooltip){
		return new KnowledgeObject<ObjectDummy>(new ObjectDummy(identifier),itemToRender,tooltip);
	}
	
	public static KnowledgeObject<? extends IKnowledgeObjectInstance<?>> link(KnowledgeObject<? extends IKnowledgeObjectInstance<?>> object){
		return new LinkedKnowledgeObject<>(object);
	}
	
	public static KnowledgeObject<? extends IKnowledgeObjectInstance<?>> link(KnowledgeObject<? extends IKnowledgeObjectInstance<?>> object, ItemStack itemToRender, String tooltip){
		return new LinkedKnowledgeObject<>(object,itemToRender,tooltip);
	}
	
	private KnowledgeRegistrations(){}
}
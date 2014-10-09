package chylex.hee.mechanics.compendium;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import chylex.hee.block.BlockCrossedDecoration;
import chylex.hee.block.BlockEndstoneTerrain;
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
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.content.KnowledgeObject.LinkedKnowledgeObject;
import chylex.hee.mechanics.compendium.content.fragments.KnowledgeFragmentCrafting;
import chylex.hee.mechanics.compendium.content.fragments.KnowledgeFragmentEnhancement;
import chylex.hee.mechanics.compendium.content.fragments.KnowledgeFragmentItemConversion;
import chylex.hee.mechanics.compendium.content.fragments.KnowledgeFragmentText;
import chylex.hee.mechanics.compendium.objects.IKnowledgeObjectInstance;
import chylex.hee.mechanics.compendium.objects.ObjectBlock;
import chylex.hee.mechanics.compendium.objects.ObjectDummy;
import chylex.hee.mechanics.compendium.objects.ObjectItem;
import chylex.hee.mechanics.compendium.objects.ObjectMob;
import chylex.hee.mechanics.enhancements.types.EnderPearlEnhancements;
import chylex.hee.mechanics.enhancements.types.TNTEnhancements;
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
		DRAGON_ESSENCE = link(ESSENCE,new ItemStack(ItemList.essence,EssenceType.DRAGON.getItemDamage()),"Essence"),
		END_POWDER = create(ItemList.end_powder),
		ENHANCED_ENDER_PEARL = create(ItemList.enhanced_ender_pearl),
		TEMPLE_CALLER = create(ItemList.temple_caller),
		ENDER_DRAGON = new KnowledgeObject<ObjectMob>(new ObjectMob(EntityBossDragon.class),new ItemStack(Blocks.dragon_egg),"Ender Dragon"),
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
		ENDIUM_ORE = create(BlockList.endium_ore),
		ENDIUM_BLOCK = create(BlockList.endium_block),
		SPATIAL_DASH_GEM = create(ItemList.spatial_dash_gem),
		ENDIUM_INGOT = create(ItemList.endium_ingot),
		BIOME_COMPASS = create(ItemList.biome_compass),
		ENDER_EYE = create(EntityMiniBossEnderEye.class),
		ANGRY_ENDERMAN_LINKED = new LinkedKnowledgeObject<>(ANGRY_ENDERMAN),
		
		// ===
		
		METEOROID = dummy("Meteoroid",new ItemStack(BlockList.sphalerite),"Meteoroid"),
		SPHALERITE = create(BlockList.sphalerite,0),
		SPHALERITE_WITH_STARDUST = create(BlockList.sphalerite,1),
		STARDUST = create(ItemList.stardust),
		
		// ===
		
		BIOME_ISLANDS = dummy("BiomeIslands",new ItemStack(Blocks.end_stone),"Biome Islands"),
		INSTABILITY_ORB_ORE = create(BlockList.instability_orb_ore),
		STARDUST_ORE = create(BlockList.stardust_ore),
		INSTABILITY_ORB = create(ItemList.instability_orb),
		POTION_OF_INSTABILITY = create(ItemList.potion_of_instability),
		STARDUST_LINKED = link(STARDUST),
		
		// =>
		
		INFESTED_FOREST_BIOME = dummy("InfestedForest",new ItemStack(BlockList.end_terrain,1,BlockEndstoneTerrain.metaInfested),"Infested Forest Biome"),
		INFESTED_END_STONE = create(BlockList.end_terrain,0),
		INFESTED_GRASS = create(BlockList.crossed_decoration,BlockCrossedDecoration.dataInfestedGrass),
		INFESTED_TALL_GRASS = create(BlockList.crossed_decoration,BlockCrossedDecoration.dataInfestedTallgrass),
		INFESTED_FERN = create(BlockList.crossed_decoration,BlockCrossedDecoration.dataInfestedFern),
		THORNY_BUSH = create(BlockList.crossed_decoration,BlockCrossedDecoration.dataThornBush),
		INFESTED_BAT = create(EntityMobInfestedBat.class),
		SILVERFISH_LINKED = link(SILVERFISH),
		
		SPOOKY_LOG = create(BlockList.spooky_log),
		SPOOKY_LEAVES = create(BlockList.spooky_leaves),
		DRY_SPLINTER = create(ItemList.dry_splinter),
		GHOST_AMULET = create(ItemList.ghost_amulet),
		ECTOPLASM = create(ItemList.ectoplasm),
		INFESTATION_REMEDY = create(ItemList.infestation_remedy),
		
		RAVAGED_DUNGEON = dummy("RavagedDungeon",new ItemStack(BlockList.ravaged_brick),"Ravaged Dungeon"),
		RAVAGED_BRICK = create(BlockList.ravaged_brick),
		RAVAGED_BRICK_GLOWING = create(BlockList.ravaged_brick_glow),
		RAVAGED_BRICK_STAIRS = create(BlockList.ravaged_brick_stairs),
		RAVAGED_BRICK_SLAB = create(BlockList.ravaged_brick_slab),
		RAVAGED_BRICK_FENCE = create(BlockList.ravaged_brick_fence),
		CHARM_POUCH = create(ItemList.charm_pouch),
		RUNES = create(ItemList.rune),
		CHARMS = create(ItemList.charm),
		LOUSE = create(EntityMobLouse.class),
		
		// ===
		
		BURNING_MOUNTAINS_BIOME = dummy("BurningMountains",new ItemStack(BlockList.end_terrain,1,BlockEndstoneTerrain.metaBurned),"Burning Mountains Biome"),
		BURNED_END_STONE = create(BlockList.end_terrain,1),
		LILYFIRE = create(BlockList.crossed_decoration,BlockCrossedDecoration.dataLilyFire),
		IGNEOUS_ROCK_ORE_LINKED = link(IGNEOUS_ROCK_ORE),
		IGNEOUS_ROCK_LINKED = link(IGNEOUS_ROCK),
		
		CINDER = create(BlockList.cinder),
		FIERY_ESSENCE_ALTAR = create(BlockList.essence_altar,EssenceType.FIERY.id),
		FIERY_ESSENCE = link(ESSENCE,new ItemStack(ItemList.essence,1,EssenceType.FIERY.getItemDamage()),"Essence"),
		FIRE_GOLEM = create(EntityMobFireGolem.class),
		SCORCHING_LENS = create(EntityMobScorchingLens.class),
		
		FIRE_SHARD = create(ItemList.fire_shard),
		SCORCHING_PICKAXE = create(ItemList.scorching_pickaxe),
		HAUNTED_MINER = create(EntityMobHauntedMiner.class),
		
		// ===
		
		ENCHANTED_ISLAND_BIOME = dummy("EnchantedIsland",new ItemStack(BlockList.end_terrain,1,BlockEndstoneTerrain.metaEnchanted),"Enchanted Island Biome"),
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
		 *    8 | easily found objects with some mechanics
		 *   10 | basic objects with mechanics
		 *   15 | regular objects with mechanics
		 *   20 | important objects with mechanics
		 *   30 | major objects
		 *   40 | phase objects (early)
		 *   75 | phase objects (mid)
		 *  100 | phase objects (late)
		 * 
		 * Fragment price
		 * ==============
		 *   1 | bulk data
		 *   2 | unimportant or generic information
		 *   3 | semi-important or additional information
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
			new KnowledgeFragmentText(5).setContents("Knowledge Notes are items found in dungeons and traded by villagers. Using them gives you Knowledge Points, but destroys the item in the process."),
			new KnowledgeFragmentText(6).setContents("You can use right mouse button instead of the Back button for easier use of the Compendium."),
			new KnowledgeFragmentText(7).setContents("Opening the Compendium while sneaking and looking at a block, item or a mob opens the appropriate object.")
		});
		
		ESSENCE.setNonBuyable().setDiscoveryReward(12).setFragments(new KnowledgeFragment[]{
			new KnowledgeFragmentText(80).setContents("Essence is used to unleash power of altars.").setPrice(2).setUnlockOnDiscovery(),
			new KnowledgeFragmentText(81).setContents("Dragon Essence is gained by killing the Ender Dragon.").setPrice(2).setUnlockCascade(704),
			new KnowledgeFragmentText(82).setContents("Fiery Essence is dropped by Fire Golem and Scorching Lens, which can be found in a variation of Burning Mountains.").setPrice(2).setUnlockCascade(614,623)
		});
		
		// ===
		
		KnowledgeCategories.OVERWORLD.addKnowledgeObjects(new KnowledgeObject[]{
			STRONGHOLD.setPosToCenter().setUnlockPrice(5).setDiscoveryReward(10).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(710).setContents("Large, complex undeground structures found in the Overworld.").setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(711).setContents("It is possible to locate Strongholds by releasing Eyes of Ender into the air.").setPrice(2).setUnlockRequirements(710),
				new KnowledgeFragmentText(712).setContents("They contain a variety of rooms, one of which is the End Portal room. The portal leads to the Dragon Lair, and needs to be activated using Eyes of Ender.").setPrice(2).setUnlockRequirements(710),
				new KnowledgeFragmentText(713).setContents("Chests in the Stronghold may contain Adventurer's Diary, Temple Caller and Knowledge Fragments.").setPrice(3).setUnlockRequirements(712)
			}),
			
			ADVENTURERS_DIARY.setPos(-2,0).setUnlockPrice(5).setDiscoveryReward(12).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(10).setContents("Short story of an adventurer, split across 16 pages.").setPrice(2).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(11).setContents("Opening a new diary page unlocks next page of the story, and locks the item to only open that page for other players.").setPrice(2).setUnlockOnDiscovery()
			}),
			
			ENDERMAN_HEAD.setPos(0,0).setUnlockPrice(5).setDiscoveryReward(10).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(20).setContents("Rare drop from Endermen.").setPrice(2).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(21).setContents("Drop chance is 1 in 40 (2.5%), Looting enchantment increases the chance.").setPrice(2).setUnlockRequirements(20)
			}),
			
			MUSIC_DISKS.setPos(2,0).setUnlockPrice(5).setDiscoveryReward(15).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(30).setContents("Jukebox discs with various pieces of qwertygiy's music.").setPrice(2)
			}),
			
			ALTAR_NEXUS.setPos(-1,5).setUnlockPrice(10).setDiscoveryReward(5).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(40).setContents("Core component of the Basic Essence Altar.").setPrice(8),
				new KnowledgeFragmentCrafting(41).setRecipeFromRegistry(new ItemStack(ItemList.altar_nexus)).setPrice(5).setUnlockRequirements(40),
				new KnowledgeFragmentCrafting(42).setRecipeFromRegistry(new ItemStack(BlockList.essence_altar)).setPrice(8).setUnlockCascade(50)
			}),
			
			BASIC_ESSENCE_ALTAR.setPos(1,5).setNonBuyable().setDiscoveryReward(20).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentCrafting(50).setRecipeFromRegistry(new ItemStack(BlockList.essence_altar)).setPrice(8).setUnlockCascade(42),
				new KnowledgeFragmentText(51).setContents("Basic altar is converted into a specific type of altar by giving it one Essence, and 8 blocks and items it requests.").setUnlockOnDiscovery().setPrice(5).setUnlockRequirements(50),
				new KnowledgeFragmentText(52).setContents("Transformed altars can be given 32 of Essence per right-click, or 1 while sneaking.").setPrice(2).setUnlockRequirements(51),
				new KnowledgeFragmentText(53).setContents("Altars have 4 sockets for precious blocks in the corners. Some of the blocks give an effect and some boost used effects, one of each is required minimum.").setPrice(6).setUnlockRequirements(51),
				new KnowledgeFragmentText(54).setContents("Redstone Block increases altar speed, Lapis Block improves range and Nether Quartz Block makes Essence usage lower.").setPrice(2).setUnlockRequirements(53),
				new KnowledgeFragmentText(55).setContents("Iron Block has effect boost 1, Gold Block 3, Diamond Block 7 and Emerald Block 10.").setPrice(2).setUnlockRequirements(53)
			}),
			
			ENDERMAN.setPos(-1,2).setUnlockPrice(5).setDiscoveryReward(15).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(60).setContents("Tall black mobs that populate the End, and uncommonly spawn in the Overworld.").setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(61).setContents("Looking into their eyes or attacking them makes them aggroed.").setPrice(2).setUnlockRequirements(60),
				new KnowledgeFragmentText(62).setContents("Endermen can teleport away, either randomly or sometimes when attacked.").setPrice(2).setUnlockRequirements(60),
				new KnowledgeFragmentText(63).setContents("Water damages them.").setPrice(3).setUnlockRequirements(60)
			}),
			
			SILVERFISH.setPos(1,2).setUnlockPrice(5).setDiscoveryReward(25).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(70).setContents("A tiny hostile arthropod found in Overworld Strongholds, and Infested Forest biomes in the End.").setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(71).setContents("They are often hidden inside Stone Brick blocks. Those blocks are easier to mine.").setPrice(2).setUnlockRequirements(70),
				new KnowledgeFragmentText(72).setContents("Attacking a Silverfish will wake up nearby Silverfish hidden in blocks.").setPrice(3).setUnlockRequirements(71)
			})
		});
		
		// ===
		
		KnowledgeCategories.DRAGON_LAIR.addKnowledgeObjects(new KnowledgeObject[]{
			DRAGON_LAIR.setPosToCenter().setUnlockPrice(45).setDiscoveryReward(15).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(750).setContents("Large island made of End Stone, protected by the Ender Dragon.").setPrice(5),
				new KnowledgeFragmentText(751).setContents("Several Obsidian spikes with Ender Crystals are scattered across the island. The crystals heal the dragon and explode when destroyed.").setPrice(2).setUnlockRequirements(750).setUnlockCascade(191),
				new KnowledgeFragmentText(752).setContents("There are 3 types of spikes:").setPrice(2).setUnlockRequirements(751),
				new KnowledgeFragmentText(753).setContents("One type of crystal spawn 8 primed TNT blocks that explodes near the ground, and makes Endermen under it attack the player.").setPrice(2).setUnlockRequirements(752),
				new KnowledgeFragmentText(754).setContents("Another type has Iron Bars on top around the crystal. This type is usually present on tall pillars.").setPrice(2).setUnlockRequirements(752),
				new KnowledgeFragmentText(755).setContents("The third type causes a massive explosion when destroyed, and scatters the entire pillar around. This type is only selected for small pillars.").setPrice(2).setUnlockRequirements(752)
			}),
			
			END_STONE.setPos(-2,0).setUnlockPrice(5).setDiscoveryReward(8).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(90).setContents("End Stone is the primary building material of the End. It is highly resistant to explosions.").setPrice(2).setUnlockOnDiscovery()
			}),
			
			FALLING_OBSIDIAN.setPos(0,0).setUnlockPrice(8).setDiscoveryReward(12).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(100).setContents("Special variation of Obsidian affected by gravity.").setPrice(2).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(101).setContents("It is primarily found in Dragon Lair and Enchanted Island Biome.").setPrice(2),
				new KnowledgeFragmentText(102).setContents("Falling on a weak block, such as torches or flowers, crushes the block.").setPrice(2).setUnlockRequirements(100),
				new KnowledgeFragmentText(103).setContents("Players and mobs hit by it are severely damaged, up to 30 hearts.").setPrice(2).setUnlockRequirements(100),
				new KnowledgeFragmentText(104).setContents("When broken, it loses its ability to fall.").setPrice(2).setUnlockRequirements(100)
			}),
			
			DRAGON_ESSENCE_ALTAR.setPos(-2,10).setUnlockPrice(25).setDiscoveryReward(25).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(110).setContents("Dragon Essence Altar infuses blocks and items with Dragon Essence.").setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(111).setContents("Infusion requires pedestals, which are up to 8 blocks of the same type placed close around the altar").setPrice(8).setUnlockRequirements(110),
				new KnowledgeFragmentText(112).setContents("Using altar sockets to increase range allows up to 12 pedestals to be used.").setPrice(3).setUnlockRequirements(111),
				new KnowledgeFragmentText(113).setContents("Tools, weapons and armor will quickly repair, and slowly improve enchantments and gain new enchantments.").setPrice(5).setUnlockRequirements(110),
				new KnowledgeFragmentText(114).setContents("Some items will turn into different items when infused:").setPrice(4).setUnlockRequirements(110),
				new KnowledgeFragmentItemConversion(115).setItems(new ItemStack(Items.brewing_stand),new ItemStack(ItemList.enhanced_brewing_stand)).setPrice(2).setUnlockRequirements(114),
				new KnowledgeFragmentItemConversion(116).setItems(new ItemStack(Items.ender_eye),new ItemStack(ItemList.temple_caller)).setPrice(2).setUnlockRequirements(114).setUnlockCascade(180)
			}),
			
			END_POWDER_ORE.setPos(4,0).setUnlockPrice(20).setDiscoveryReward(18).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(120).setContents("Commonly found ore in the End.").setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(121).setContents("It spawns clusters spread out across a larger area than other ores.").setPrice(2).setUnlockOnDiscovery().setUnlockRequirements(120),
				new KnowledgeFragmentText(122).setContents("Mining with any pickaxe yields 1-3 End Powder and 2-4 experience.").setPrice(5).setUnlockRequirements(120),
				new KnowledgeFragmentText(123).setContents("Fortune enchantment has an effect on the amount of End Powder dropped.").setPrice(3).setUnlockRequirements(122)
			}),
			
			ENHANCED_BREWING_STAND.setPos(-1,12).setUnlockPrice(18).setDiscoveryReward(20).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(130).setContents("A Brewing Stand infused with Dragon Essence.").setPrice(5),
				new KnowledgeFragmentText(131).setContents("The brewing speed depends on potion complexity, simple potions brew much faster than with regular Brewing Stand.").setPrice(2).setUnlockRequirements(130),
				new KnowledgeFragmentText(132).setContents("Using Glowstone, Redstone and Gunpowder requires End Powder, but the potions can go over the limits of basic Brewing Stand.").setPrice(5).setUnlockRequirements(130).setUnlockCascade(166),
				new KnowledgeFragmentText(133).setContents("It is required to brew special potions using Awkward Potion and an ingredient:").setPrice(4).setUnlockRequirements(130),
				new KnowledgeFragmentItemConversion(134).setItems(new ItemStack(ItemList.instability_orb),new ItemStack(ItemList.potion_of_instability)).setPrice(2).setUnlockRequirements(133).setUnlockCascade(383,741),
				new KnowledgeFragmentItemConversion(135).setItems(new ItemStack(ItemList.silverfish_blood),new ItemStack(ItemList.infestation_remedy)).setPrice(2).setUnlockRequirements(133).setUnlockCascade(731)
			}),
			
			ENHANCED_TNT.setPos(5,4).setUnlockPrice(12).setDiscoveryReward(10).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentEnhancement(140).setEnhancement(TNTEnhancements.NO_BLOCK_DAMAGE).setPrice(3),
				new KnowledgeFragmentEnhancement(141).setEnhancement(TNTEnhancements.NO_ENTITY_DAMAGE).setPrice(3),
				new KnowledgeFragmentEnhancement(142).setEnhancement(TNTEnhancements.EXTRA_POWER).setPrice(3),
				new KnowledgeFragmentEnhancement(143).setEnhancement(TNTEnhancements.TRAP).setPrice(3),
				new KnowledgeFragmentEnhancement(144).setEnhancement(TNTEnhancements.NOCLIP).setPrice(3),
				new KnowledgeFragmentEnhancement(145).setEnhancement(TNTEnhancements.FIRE).setPrice(3),
				new KnowledgeFragmentEnhancement(146).setEnhancement(TNTEnhancements.NO_FUSE).setPrice(3)
			}),
			
			DRAGON_EGG.setPos(-4,8).setUnlockPrice(10).setDiscoveryReward(15).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(150).setContents("Dragon Egg is created on top of the End Portal after killing the Ender Dragon.").setPrice(5).setUnlockOnDiscovery().setUnlockCascade(705),
				new KnowledgeFragmentText(151).setContents("It teleports into random directions when interacted with.").setPrice(2).setUnlockRequirements(150),
				new KnowledgeFragmentText(152).setContents("The egg can only be picked up by sneaking and hitting it with any sword.").setPrice(5).setUnlockRequirements(150),
				new KnowledgeFragmentText(153).setContents("When destroyed, it teleports back to the End.").setPrice(3).setUnlockRequirements(150)
			}),
			
			DRAGON_ESSENCE.setPos(-2,8),
			
			END_POWDER.setPos(4,2).setUnlockPrice(20).setDiscoveryReward(22).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(160).setContents("Magical powder used to enhance items with special effects.").setPrice(2).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(161).setContents("Sneaking and using the End Powder opens enhancement screen.").setPrice(5).setUnlockOnDiscovery().setUnlockRequirements(160),
				new KnowledgeFragmentText(162).setContents("Placing a block or item into the top slot opens all possible enhancements.").setPrice(2).setUnlockRequirements(161),
				new KnowledgeFragmentText(163).setContents("Enhancing requires a specific amount of End Powder and ingredients of one type.").setPrice(2).setUnlockRequirements(162),
				new KnowledgeFragmentText(164).setContents("The ingredient is unlocked by trying. Using an incorrect ingredient may destroy random items in the interface, but may also reveal the correct ingredient.").setPrice(3).setUnlockRequirements(163),
				new KnowledgeFragmentText(165).setContents("Different enhancements can be stacked together.").setPrice(3).setUnlockRequirements(162),
				new KnowledgeFragmentText(166).setContents("End Powder applies enhancing effects to potions in Enhanced Brewing Stand.").setPrice(5).setUnlockRequirements(160).setUnlockCascade(132),
				new KnowledgeFragmentText(167).setContents("Applying End Powder to Death Flower partially reverts the effect of decaying.").setPrice(3).setUnlockCascade(244)
			}),
			
			ENHANCED_ENDER_PEARL.setPos(3,4).setUnlockPrice(12).setDiscoveryReward(10).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentEnhancement(170).setEnhancement(EnderPearlEnhancements.NO_FALL_DAMAGE).setPrice(3),
				new KnowledgeFragmentEnhancement(171).setEnhancement(EnderPearlEnhancements.NO_GRAVITY).setPrice(3),
				new KnowledgeFragmentEnhancement(172).setEnhancement(EnderPearlEnhancements.INCREASED_RANGE).setPrice(3),
				new KnowledgeFragmentEnhancement(173).setEnhancement(EnderPearlEnhancements.DOUBLE_SPEED).setPrice(3),
				new KnowledgeFragmentEnhancement(174).setEnhancement(EnderPearlEnhancements.EXPLOSIVE).setPrice(3),
				new KnowledgeFragmentEnhancement(175).setEnhancement(EnderPearlEnhancements.FREEZE).setPrice(3),
				new KnowledgeFragmentEnhancement(176).setEnhancement(EnderPearlEnhancements.RIDING).setPrice(3)
			}),
			
			TEMPLE_CALLER.setPos(-3,12).setUnlockPrice(15).setDiscoveryReward(20).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(180).setContents("Temple Caller is an Eye of Ender infused with Dragon Essence.").setPrice(5).setUnlockCascade(116),
				new KnowledgeFragmentText(181).setContents("It can rarely be found in Overworld dungeons, Stronghold and Dungeon Tower.").setPrice(2),
				new KnowledgeFragmentText(182).setContents("Before using the Temple Caller, it has to be filled with Energy.").setPrice(7),
				new KnowledgeFragmentText(183).setContents("Using it in the End with Dragon Egg in the inventory teleports the player to a temple.").setPrice(4).setUnlockRequirements(182),
				new KnowledgeFragmentText(184).setContents("In the temple, there is a dark pedestal for the Dragon Egg. When the Egg is placed, it will use its power to destroy the End.").setPrice(4).setUnlockRequirements(183)
			}),
			
			ENDER_DRAGON.setPos(-3,3).setUnlockPrice(20).setDiscoveryReward(70).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(190).setContents("Ender Dragon is a giant boss with 125 hearts, that protects the End dimension.").setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(191).setContents("Nearby Ender Crystals regenerate the dragon's health.").setPrice(2).setUnlockOnDiscovery().setUnlockRequirements(190).setUnlockCascade(751),
				new KnowledgeFragmentText(192).setContents("Its passive attacks include churning out fireballs and freezeballs, biting that can cause bad status effects, massive knockback to creatures and destroying blocks.").setPrice(3).setUnlockRequirements(190),
				new KnowledgeFragmentText(193).setContents("When enough of the Ender Crystals are destroyed, the dragon begins the angry stage.").setPrice(2).setUnlockRequirements(191),
				new KnowledgeFragmentText(194).setContents("During the angry stage, passive attacks are more powerful, and the dragon also does special attacks (Easy difficulty or higher).").setPrice(3).setUnlockRequirements(192,193),
				new KnowledgeFragmentText(195).setContents("The angry stage also gives the dragon ability to scatter Obsidian from the pillars when it flies through it.").setPrice(3).setUnlockRequirements(194),
				new KnowledgeFragmentText(196).setContents("There are 7 special attacks:").setPrice(5).setUnlockRequirements(194),
				new KnowledgeFragmentText(197).setContents("Divebomb makes the dragon fly up, and then swoop down through the island.").setPrice(2).setUnlockRequirements(196),
				new KnowledgeFragmentText(198).setContents("Stay'n'fire begins with the dragon flying high above a player, and rapidly churning out fireballs at it.").setPrice(2).setUnlockRequirements(196),
				new KnowledgeFragmentText(199).setContents("Bitemadness is a melee attack, during which the dragon targets a single player and repeatedly uses biting attack.").setPrice(2).setUnlockRequirements(196),
				new KnowledgeFragmentText(700).setContents("Punch attack quadruples the dragon's flight speed, and then attack a player.").setPrice(2).setUnlockRequirements(196),
				new KnowledgeFragmentText(701).setContents("Freeze is a short attack that starts with the dragon churning out a freezeball, and then quickly doing another special attack.").setPrice(2).setUnlockRequirements(196),
				new KnowledgeFragmentText(702).setContents("Summon makes the dragon fly high up and spawn Angry Endermen accompanied by lightning.").setPrice(2).setUnlockRequirements(196),
				new KnowledgeFragmentText(703).setContents("Bloodlust slows the dragon down, protects it from attack and turns nearby Endermen into Vampire Bats.").setPrice(2).setUnlockRequirements(196).setUnlockCascade(210),
				new KnowledgeFragmentText(704).setContents("When killed, the dragon floats high up while disappearing, dropping large amount of experience and Dragon Essence.").setPrice(2).setUnlockRequirements(190).setUnlockCascade(81),
				new KnowledgeFragmentText(705).setContents("End Portal is formed below the dragon, with a Dragon Egg on top.").setPrice(3).setUnlockRequirements(704).setUnlockCascade(150)
			}),
			
			ANGRY_ENDERMAN.setPos(-4,5).setUnlockPrice(10).setDiscoveryReward(5).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(200).setContents("Startled Enderman that will attack nearby playeres.").setPrice(2).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(201).setContents("They have less health and strength, but will not teleport away when damaged.").setPrice(3).setUnlockRequirements(200)
			}),
			
			VAMPIRE_BAT.setPos(-2,5).setUnlockPrice(10).setDiscoveryReward(8).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(210).setContents("Special bat summoned by one of the Ender Dragon attacks.").setPrice(4).setUnlockOnDiscovery().setUnlockCascade(703),
				new KnowledgeFragmentText(211).setContents("They instantly die when damaged.").setPrice(2).setUnlockRequirements(210),
				new KnowledgeFragmentText(212).setContents("The bats try to attack players. When they do, they damage the player, heal the Ender Dragon and die.").setPrice(3).setUnlockRequirements(210)
			})
		});
		
		// ===
		
		KnowledgeCategories.ENDSTONE_BLOBS.addKnowledgeObjects(new KnowledgeObject[]{
			ENDSTONE_BLOB.setPosToCenter().setUnlockPrice(40).setDiscoveryReward(10).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(220).setContents("Small blobs of varying shapes, sizes and features, made of End Stone.").setPrice(5),
				new KnowledgeFragmentText(221).setContents("They can contain End Powder Ore, Obsidian, small Ender Goo lakes, caves and rarely tiny clusters of Igneous Rock Ore.").setPrice(3).setUnlockRequirements(220)
			}),
			
			IGNEOUS_ROCK_ORE.setPos(-2,0).setUnlockPrice(20).setDiscoveryReward(10).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(230).setContents("An ore rarely spawned in Endstone Blobs and commonly in Burning Moutains Biome.").setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(231).setContents("Iron Pickaxe or better can effectively mine the ore.").setPrice(2).setUnlockRequirements(230),
				new KnowledgeFragmentText(232).setContents("Always drops just one Igneous Rock, and 3-5 experience orbs.").setPrice(4).setUnlockRequirements(230)
			}),
			
			DEATH_FLOWER.setPos(0,0).setUnlockPrice(12).setDiscoveryReward(10).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(240).setContents("Purple flower found on top of Endstone Blobs. It can be planted on End Stone, Dirt, Grass or inside Flower Pots.").setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(241).setContents("Outside the End, the flower will slowly decay, even if in a Flower Pot.").setPrice(2).setUnlockRequirements(240),
				new KnowledgeFragmentText(242).setContents("After some time of decaying, it starts to occasionally attract Angry Endermen.").setPrice(3).setUnlockRequirements(241),
				new KnowledgeFragmentText(243).setContents("When the flower decays completely, it turns dark, propagates massive amount decay to nearby blocks and spawns an Energy Cluster.").setPrice(5).setUnlockRequirements(241),
				new KnowledgeFragmentText(244).setContents("Partial decay can be healed a little using End Powder.").setPrice(3).setUnlockRequirements(241).setUnlockCascade(167),
				new KnowledgeFragmentCrafting(245).setCustomRecipe(new ItemStack(Items.dye,2,13),new ItemStack[]{ new ItemStack(BlockList.death_flower,1,0) }).setPrice(2).setUnlockRequirements(240),
				new KnowledgeFragmentCrafting(246).setCustomRecipe(new ItemStack(Items.dye,2,8),new ItemStack[]{ new ItemStack(BlockList.death_flower,1,15) }).setPrice(2).setUnlockRequirements(243)
			}),
			
			ENDER_GOO.setPos(2,0).setUnlockPrice(10).setDiscoveryReward(12).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(250).setContents("Thick goo found across the End dimension.").setPrice(2).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(251).setContents("The goo heavily limits movement of anything touching it, and it causes Poison, Weakness and Mining Fatigue as well.").setPrice(5).setUnlockRequirements(250),
				new KnowledgeFragmentText(252).setContents("Creatures from the End are unaffected by its effects.").setPrice(2).setUnlockRequirements(251),
				new KnowledgeFragmentText(253).setContents("It aggresively fights water, especially in the End.").setPrice(2).setUnlockRequirements(250)
			}),
			
			IGNEOUS_ROCK.setPos(-2,2).setUnlockPrice(12).setDiscoveryReward(15).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(260).setContents("Extremely hot rock dropped by the Igneous Rock Ore.").setPrice(2).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(261).setContents("When held, it has a chance of setting the holder on fire. The effect is strengthened in the Nether and partially suppressed in the End.").setPrice(2).setUnlockRequirements(260),
				new KnowledgeFragmentText(262).setContents("It is a very efficient fuel, roughly 3 times better than Blaze Rods.").setPrice(3).setUnlockRequirements(260),
				new KnowledgeFragmentText(263).setContents("When thrown on the ground, it spreads fire, smelts blocks, burns creatures and causes TNT to instantly explode. The rock also quickly dissolves.").setPrice(3).setUnlockRequirements(260)
			})
		});
		
		// ===
		
		KnowledgeCategories.DUNGEON_TOWER.addKnowledgeObjects(new KnowledgeObject[]{
			DUNGEON_TOWER.setPosToCenter().setUnlockPrice(55).setDiscoveryReward(25).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(270).setContents("A tall tower consisting of various types of Obsidian.").setPrice(5),
				new KnowledgeFragmentText(271).setContents("They start spawning around 350 blocks away from the Dragon Lair.").setPrice(3).setUnlockRequirements(270),
				new KnowledgeFragmentText(272).setContents("Each tower contains 3 content floors and 1 chest floor with loot, all are randomly picked and have random elements.").setPrice(2).setUnlockRequirements(270),
				new KnowledgeFragmentText(273).setContents("Loot is also often found in Dispensers, Furnaces and Brewing Stands. There is a 1 in 100 (1%) chance of spawning an Enhanced Brewing Stand.").setPrice(3).setUnlockRequirements(272),
				new KnowledgeFragmentText(274).setContents("Most floors have Angry Enderman spawners. Each spawner gives the Endermen potion effects that are more powerful the higher you get.").setPrice(5).setUnlockRequirements(272),
				new KnowledgeFragmentText(275).setContents("On top of the tower lies an Ender Eye.").setPrice(2).setUnlockRequirements(272)
			}),
			
			OBSIDIAN_STAIRS.setPos(0,0).setUnlockPrice(1).setDiscoveryReward(3).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(280).setContents("Very tough stairs made of Obsidian. Uncraftable.").setPrice(2).setUnlockOnDiscovery()
			}),
			
			OBSIDIAN_SMOOTH.setPos(-2,2).setUnlockPrice(1).setDiscoveryReward(3).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(281).setContents("Obsidian with a smooth texture, it is easier to break. Uncraftable.").setPrice(1).setUnlockOnDiscovery()
			}),
			
			OBSIDIAN_CHISELED.setPos(0,2).setUnlockPrice(1).setDiscoveryReward(3).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(282).setContents("Obsidian with a chiseled texture, it is easier to break. Uncraftable.").setPrice(1).setUnlockOnDiscovery()
			}),
			
			OBSIDIAN_PILLAR.setPos(2,2).setUnlockPrice(1).setDiscoveryReward(3).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(283).setContents("Obsidian pillar, can be placed horizontally or vertically and it is easier to break. Uncraftable.").setPrice(1).setUnlockOnDiscovery()
			}),
			
			OBSIDIAN_SMOOTH_GLOWING.setPos(-2,4).setUnlockPrice(1).setDiscoveryReward(3).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(284).setContents("Glowing Obsidian with a smooth texture, it is easier to break. Uncraftable.").setPrice(1).setUnlockOnDiscovery()
			}),
			
			OBSIDIAN_CHISELED_GLOWING.setPos(0,4).setUnlockPrice(1).setDiscoveryReward(3).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(285).setContents("Glowing Obsidian with a chiseled texture, it is easier to break. Uncraftable.").setPrice(1).setUnlockOnDiscovery()
			}),
			
			OBSIDIAN_PILLAR_GLOWING.setPos(2,4).setUnlockPrice(1).setDiscoveryReward(3).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(286).setContents("Glowing Obsidian pillar, can be placed horizontally or vertically and it is easier to break. Uncraftable.").setPrice(1).setUnlockOnDiscovery()
			}),
			
			ENDIUM_ORE.setPos(0,8).setUnlockPrice(30).setDiscoveryReward(20).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(760).setContents("Rare ore that starts to appear near Dungeon Towers, and gets more common the further you travel.").setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(761).setContents("The block drops the ore, it can be then smelted into Endium Ingot.").setPrice(8).setUnlockOnDiscovery().setUnlockRequirements(760)
			}),
			
			ENDIUM_BLOCK.setPos(-2,8).setUnlockPrice(5).setDiscoveryReward(5).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(770).setContents("Block made of Endium Ingots.").setPrice(2).setUnlockOnDiscovery(),
				new KnowledgeFragmentCrafting(771).setRecipeFromRegistry(new ItemStack(BlockList.endium_block)).setPrice(2).setUnlockOnDiscovery().setUnlockRequirements(770).setUnlockCascade(781)
			}),
			
			SPATIAL_DASH_GEM.setPos(3,7).setUnlockPrice(25).setDiscoveryReward(15).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(290).setContents("Teleportation gem dropped by the Ender Eye.").setPrice(5).setUnlockOnDiscovery().setUnlockCascade(319),
				new KnowledgeFragmentText(291).setContents("Using it creates a beam, which teleports the player to a block of mob it hits.").setPrice(2).setUnlockRequirements(290),
				new KnowledgeFragmentText(292).setContents("The beam can travel up to 75 blocks.").setPrice(3).setUnlockRequirements(291),
				new KnowledgeFragmentText(293).setContents("Unlike Ender Pearls, the beam will try to find suitable area to teleport the player to, even if it hits the side or the bottom of block.").setPrice(2).setUnlockRequirements(291),
				new KnowledgeFragmentText(294).setContents("If no suitable area is found, the player may be teleported into nearby blocks and start suffocating.").setPrice(2).setUnlockRequirements(293)
			}),
			
			ENDIUM_INGOT.setPos(2,8).setUnlockPrice(25).setDiscoveryReward(15).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(780).setContents("Shiny blue metal made by smelting Endium Ore.").setPrice(2).setUnlockOnDiscovery(),
				new KnowledgeFragmentCrafting(781).setRecipeFromRegistry(new ItemStack(BlockList.endium_block)).setPrice(2).setUnlockOnDiscovery().setUnlockRequirements(780).setUnlockCascade(771),
				new KnowledgeFragmentCrafting(782).setRecipeFromRegistry(new ItemStack(ItemList.biome_compass)).setPrice(8).setUnlockRequirements(780).setUnlockCascade(303)
			}), // TODO tweak pos
			
			BIOME_COMPASS.setPos(4,8).setUnlockPrice(25).setDiscoveryReward(10).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(300).setContents("Special compass that points at the nearest Biome Island.").setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(301).setContents("Holding it will show markers for all nearby islands in the dimension.").setPrice(5).setUnlockOnDiscovery().setUnlockRequirements(300),
				new KnowledgeFragmentText(302).setContents("Right-clicking switches between biomes.").setPrice(3).setUnlockRequirements(301),
				new KnowledgeFragmentCrafting(303).setRecipeFromRegistry(new ItemStack(ItemList.biome_compass)).setPrice(8).setUnlockRequirements(300).setUnlockCascade(782)
			}),
			
			ENDER_EYE.setPos(0,7).setUnlockPrice(20).setDiscoveryReward(40).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(310).setContents("The Ender Eye is a small but very powerful and tough mini-boss with 125 hearts.").setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(311).setContents("Hitting it will wake it up. If there eye cannot see anybody to attack, it will fall asleep again and start regenerating.").setPrice(2).setUnlockOnDiscovery().setUnlockRequirements(310),
				new KnowledgeFragmentText(312).setContents("Waking it up will also cause it to destroy weak blocks around.").setPrice(3).setUnlockRequirements(311),
				new KnowledgeFragmentText(313).setContents("It is made of Obsidian, so an Iron Sword or better is required to deal damage.").setPrice(2).setUnlockRequirements(310),
				new KnowledgeFragmentText(314).setContents("The eye is resistant to fire, cactus, suffocation, magic and drowning damage.").setPrice(3).setUnlockRequirements(313),
				new KnowledgeFragmentText(315).setContents("Attack routine consists of 3 randomly selected attacks:").setPrice(2).setUnlockRequirements(310),
				new KnowledgeFragmentText(316).setContents("First attack is a blast wave, which destroys blocks and massively knocks all creatures around away.").setPrice(2).setUnlockRequirements(315),
				new KnowledgeFragmentText(317).setContents("Second attack is a confusion attack, which deals short nausea and blindness effects to players.").setPrice(2).setUnlockRequirements(315),
				new KnowledgeFragmentText(318).setContents("Third attack is a beam attack, during which several purple beams are created and then destroyed. The beams deal magic and fire damage, and additional damage when they get destroyed.").setPrice(2).setUnlockRequirements(315),
				new KnowledgeFragmentText(319).setContents("When killed, it drops a Spatial Dash Gem, 1 Eye of Ender, 3-6 Obsidian and 35 experience.").setPrice(3).setUnlockRequirements(310).setUnlockCascade(290)
			}),
			
			ANGRY_ENDERMAN_LINKED.setPos(-3,7)
		});
		
		// ===
		
		KnowledgeCategories.METEOROIDS.addKnowledgeObjects(new KnowledgeObject[]{
			METEOROID.setPosToCenter().setUnlockPrice(40).setDiscoveryReward(8).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(320).setContents("Tiny clump of Sphalerite which begins to spawn around 1300 blocks away from the Dragon Lair.").setPrice(5)
			}),
			
			SPHALERITE.setPos(-3,1).setUnlockPrice(10).setDiscoveryReward(12).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(330).setContents("Rock of medium toughness found in Meteoroids.").setPrice(2).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(331).setContents("Stone Pickaxe or better is required to mine it quickly.").setPrice(3).setUnlockOnDiscovery().setUnlockRequirements(330)
			}),
			
			SPHALERITE_WITH_STARDUST.setPos(-1,3).setUnlockPrice(10).setDiscoveryReward(10).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(340).setContents("Variation of Sphalerite, that has yellow marks on the surface.").setPrice(2).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(341).setContents("It drops 1-3 Stardust, but no experience unlike the ores.").setPrice(3).setUnlockRequirements(340),
				new KnowledgeFragmentText(342).setContents("Fortune enchantment has a small effect on the drops.").setPrice(2).setUnlockRequirements(341)
			}),
			
			STARDUST.setPos(1,5).setUnlockPrice(10).setDiscoveryReward(8).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(350).setContents("Dust dropped by Stardust Ore and Sphalerite with Stardust. It is used for crafting and as a decomposition catalyst in Tables.").setPrice(2).setUnlockOnDiscovery()
			})
		});
		
		// ===
		
		INSTABILITY_ORB_ORE.setPos(3,1).setUnlockPrice(15).setDiscoveryReward(14).setFragments(new KnowledgeFragment[]{
			new KnowledgeFragmentText(360).setContents("Uncommon ore found in tiny clusters in all Biome Islands.").setPrice(5).setUnlockOnDiscovery(),
			new KnowledgeFragmentText(361).setContents("It can be only mined with Diamond Pickaxe or better.").setPrice(2).setUnlockRequirements(360),
			new KnowledgeFragmentText(362).setContents("The ore has a 40% chance of dropping Instability Orb.").setPrice(2).setUnlockRequirements(360),
			new KnowledgeFragmentText(363).setContents("Each level of Fortune enchantment increases the drop chance by 4%.").setPrice(3).setUnlockRequirements(362)
		});
		
		STARDUST_ORE.setPos(-3,1).setUnlockPrice(15).setDiscoveryReward(12).setFragments(new KnowledgeFragment[]{
			new KnowledgeFragmentText(370).setContents("Ore found commonly in Biome Islands.").setPrice(5).setUnlockOnDiscovery(),
			new KnowledgeFragmentText(371).setContents("Diamond Pickaxe or better is required to get any drops.").setPrice(2).setUnlockRequirements(370),
			new KnowledgeFragmentText(372).setContents("One ore drops 0-4 Stardust and 6-9 experience orbs.").setPrice(2).setUnlockRequirements(371),
			new KnowledgeFragmentText(373).setContents("Fortune enchantment does not affect the drops.").setPrice(3).setUnlockRequirements(372)
		});
		
		INSTABILITY_ORB.setPos(5,1).setUnlockPrice(15).setDiscoveryReward(12).setFragments(new KnowledgeFragment[]{
			new KnowledgeFragmentText(380).setContents("Very unstable material dropped by Instability Orb Ore.").setPrice(5).setUnlockOnDiscovery(),
			new KnowledgeFragmentText(381).setContents("It decomposes when thrown on the ground. When the process finishes, it can either explode, or turn into a random mob, block or item.").setPrice(5).setUnlockRequirements(380),
			new KnowledgeFragmentText(382).setContents("When a TNT explodes near one or more decomposing orbs, they will not explode once the decomposition is complete.").setPrice(3).setUnlockRequirements(381),
			new KnowledgeFragmentText(383).setContents("Brewing it in an Enhanced Brewing Stand creates Potion of Instability.").setPrice(3).setUnlockRequirements(380).setUnlockCascade(134,741)
		});
		
		POTION_OF_INSTABILITY.setPos(7,1).setUnlockPrice(10).setDiscoveryReward(8).setFragments(new KnowledgeFragment[]{
			new KnowledgeFragmentText(740).setContents("Special potion that causes a random effect. It has to be brewed in an Enhanced Brewing Stand, using Gunpowder on the brewed potion turns it into splash version.").setPrice(5).setUnlockOnDiscovery(),
			new KnowledgeFragmentItemConversion(741).setItems(new ItemStack(ItemList.instability_orb),new ItemStack(ItemList.potion_of_instability)).setPrice(2).setUnlockRequirements(740).setUnlockCascade(134,383)
		});
		
		BIOME_ISLANDS.setPos(0,0).setUnlockPrice(40).setDiscoveryReward(20).setFragments(new KnowledgeFragment[]{
			new KnowledgeFragmentText(720).setContents("Biome Islands are large islands made of End Stone, that start spawning roughly 1600 blocks away from the Dragon Lair.").setPrice(5),
			new KnowledgeFragmentText(721).setContents("Each island has caves and ores, every biome modifies the amounts and rates as well as the overall shape of the terrain.").setPrice(2).setUnlockRequirements(720),
			new KnowledgeFragmentText(722).setContents("Biomes can have multiple different variations with unique content, and random deviations that modify properties of some features (such as very tall trees).").setPrice(3).setUnlockRequirements(721),
			new KnowledgeFragmentText(723).setContents("Instability Orb Ore and Stardust Ore only spawn in the islands.").setPrice(2).setUnlockRequirements(721)
		});
		
		STARDUST_LINKED.setPos(-5,1);
		
		// ===
		
		KnowledgeCategories.BIOME_ISLAND_FOREST.addKnowledgeObjects(new KnowledgeObject[]{
			INSTABILITY_ORB_ORE, STARDUST_ORE, INSTABILITY_ORB, POTION_OF_INSTABILITY, STARDUST_LINKED, BIOME_ISLANDS,
			
			INFESTED_FOREST_BIOME.setPosToCenter().setUnlockPrice(60).setDiscoveryReward(45).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(390).setContents("Mostly flat biome with occasional hills, long caves and decent amount of ores.").setPrice(5),
				new KnowledgeFragmentText(391).setContents("There are 2 variations of the biome - Deep and Ravaged.").setPrice(2).setUnlockRequirements(390),
				new KnowledgeFragmentText(392).setContents("Both of these variations contain Silverfish, Infested Bats and occasionally Endermen.").setPrice(3).setUnlockRequirements(391),
				new KnowledgeFragmentText(393).setContents("Deep variation is massively populated with Spooky Trees that can have faces on them. The ground has all types of infested plants, including Thorny Bushes.").setPrice(10).setUnlockRequirements(391),
				new KnowledgeFragmentText(394).setContents("Ravaged variation has patches of infested plants and occasional Spooky Trees, but the most important feature is the Ravaged Dungeon which is an open gate to the Charms.").setPrice(10).setUnlockRequirements(391),
				// 395 reseved for third variation
				new KnowledgeFragmentText(396).setContents("Staying in an Infested Forest for too long causes Infestation to build up. The effects of Infestation kick off some time after leaving the island. Those effects include Weakness, Slowness, Mining Fatigue, and if the Infestation builds up too much, it can even cause Poison, Blindness or Nausea.").setPrice(8).setUnlockRequirements(390),
				new KnowledgeFragmentText(397).setContents("Infestation can only be partly cured using Infestation Remedy.").setPrice(2).setUnlockRequirements(396)
			}),
			
			INFESTED_END_STONE.setPos(0,4).setUnlockPrice(5).setDiscoveryReward(8).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(400).setContents("Variation of End Stone found in the Infested Forest Biome.").setPrice(2).setUnlockOnDiscovery()
			}),
			
			INFESTED_GRASS.setPos(3,5).setUnlockPrice(2).setDiscoveryReward(3).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(410).setContents("One of the plants commonly found in the Infested Forest Biome.").setPrice(2).setUnlockOnDiscovery()
			}),
			
			INFESTED_TALL_GRASS.setPos(5,5).setUnlockPrice(2).setDiscoveryReward(3).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(411).setContents("One of the plants commonly found in the Infested Forest Biome.").setPrice(2).setUnlockOnDiscovery()
			}),
			
			INFESTED_FERN.setPos(3,7).setUnlockPrice(2).setDiscoveryReward(3).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(412).setContents("One of the plants commonly found in the Infested Forest Biome.").setPrice(2).setUnlockOnDiscovery()
			}),
			
			THORNY_BUSH.setPos(5,7).setUnlockPrice(5).setDiscoveryReward(5).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(420).setContents("Dark plant found in the Infested Forest (Deep).").setPrice(2).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(421).setContents("On contact, it causes short Poison effect.").setPrice(3).setUnlockRequirements(420)
			}),
			
			INFESTATION_REMEDY.setPos(-5,7).setUnlockPrice(10).setDiscoveryReward(10).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(730).setContents("A potion that eases the effects of Infestation. It has to be brewed in Enhanced Brewing Stand.").setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentItemConversion(731).setItems(new ItemStack(ItemList.silverfish_blood),new ItemStack(ItemList.infestation_remedy)).setPrice(2).setUnlockRequirements(730).setUnlockCascade(135),
			}),
			
			INFESTED_BAT.setPos(-3,5).setUnlockPrice(8).setDiscoveryReward(5).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(480).setContents("Special type of bat that spawns in the Infested Forest Biome. Each bat varies in size.").setPrice(2).setUnlockOnDiscovery()
			}),
			
			SILVERFISH_LINKED.setPos(-5,5),
			
			// =
			
			SPOOKY_LOG.setPos(-7,11).setUnlockPrice(15).setDiscoveryReward(25).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(430).setContents("Logs used for the trunk of Spooky Trees.").setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(431).setContents("Breaking one log also destroys all logs above.").setPrice(3).setUnlockRequirements(430),
				new KnowledgeFragmentText(432).setContents("Each log has a 1 in 8 (12.5%) chance of dropping a Dry Splinter.").setPrice(3).setUnlockRequirements(430).setUnlockCascade(451),
				new KnowledgeFragmentText(433).setContents("In the Deep variation of Infested Forest, some trees have faces.").setPrice(2).setUnlockRequirements(430),
				new KnowledgeFragmentText(434).setContents("When nobody looks at a face, it will move around the tree or move to another nearby tree.").setPrice(3).setUnlockRequirements(433),
				new KnowledgeFragmentText(435).setContents("If a log with a face is broken, it has a 1 in 4 (25%) chance of spawning a Forest Ghost.").setPrice(2).setUnlockRequirements(433),
				new KnowledgeFragmentText(436).setContents("Ghost Amulet will stop the Forest Ghost from spawning, and the face will have a 3 in 5 (60%) chance to drop Ectoplasm.").setPrice(8).setUnlockRequirements(435).setUnlockCascade(461),
				new KnowledgeFragmentCrafting(437).setRecipeFromRegistry(new ItemStack(BlockList.spooky_log)).setPrice(5).setUnlockRequirements(430).setUnlockCascade(452)
			}),
			
			SPOOKY_LEAVES.setPos(-5,11).setUnlockPrice(10).setDiscoveryReward(8).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(440).setContents("Spooky Leaves are foliage of the Spooky Trees.").setPrice(4).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(441).setContents("If not connected to a Spooky Log, they leaves very quickly decay.").setPrice(3).setUnlockRequirements(440),
				new KnowledgeFragmentCrafting(442).setRecipeFromRegistry(new ItemStack(BlockList.spooky_leaves)).setPrice(5).setUnlockRequirements(440).setUnlockCascade(453)
			}),
			
			DRY_SPLINTER.setPos(-6,13).setUnlockPrice(8).setDiscoveryReward(5).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(450).setContents("Crafting material dropped by Spooky Logs.").setPrice(2).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(451).setContents("Each Spooky Log has 1 in 8 (12.5%) chance of dropping a Dry Splinter.").setPrice(3).setUnlockRequirements(450).setUnlockCascade(432),
				new KnowledgeFragmentCrafting(452).setRecipeFromRegistry(new ItemStack(BlockList.spooky_log)).setPrice(5).setUnlockRequirements(450).setUnlockCascade(437),
				new KnowledgeFragmentCrafting(453).setRecipeFromRegistry(new ItemStack(BlockList.spooky_leaves)).setPrice(5).setUnlockRequirements(450).setUnlockCascade(442)
			}),
			
			GHOST_AMULET.setPos(-7,16).setUnlockPrice(18).setDiscoveryReward(10).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(460).setContents("An amulet that banishes the Forest Ghost.").setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(461).setContents("When in the inventory, Forest Ghost will not spawn and the Spooky Log face will have 3 in 5 (60%) chance to drop Ectoplasm.").setPrice(8).setUnlockRequirements(460).setUnlockCascade(436),
				new KnowledgeFragmentText(462).setContents("In order to create it, one piece of End Powder, Emerald and String all have to be thrown into Ender Goo.").setPrice(8).setUnlockRequirements(460)
			}),
			
			ECTOPLASM.setPos(-5,16).setUnlockPrice(25).setDiscoveryReward(15).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(470).setContents("Strange ethereal substance dropped by banished Forest Ghosts.").setPrice(5).setUnlockOnDiscovery()
			}),
			
			// =
			
			RAVAGED_DUNGEON.setPos(6,11).setUnlockPrice(25).setDiscoveryReward(20).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(490).setContents("Huge dungeon built from Ravaged Bricks, found in Infested Forest (Ravaged).").setPrice(5),
				new KnowledgeFragmentText(491).setContents("It is the only way of acquiring items required to create Charms.").setPrice(2).setUnlockRequirements(490),
				new KnowledgeFragmentText(492).setContents("The dungeon consists of 3 floors. Each floor is a collection of randomly generated hallways and rooms spread across the entire island.").setPrice(2).setUnlockRequirements(491),
				new KnowledgeFragmentText(493).setContents("There is always an entrance to the first floor, then the floors have rooms that lead to lower floors and the bottom floor contains a massive final room with many spawners and loot chests.").setPrice(3).setUnlockRequirements(492),
				new KnowledgeFragmentText(494).setContents("Hallways and rooms have random designs, some of which contain loot chests and Silverfish and Louse spawners.").setPrice(2).setUnlockRequirements(492)
			}),
			
			RAVAGED_BRICK.setPos(6,13).setUnlockPrice(2).setDiscoveryReward(8).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(500).setContents("Primary building block of the Ravaged Dungeon.").setPrice(2).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(501).setContents("Some of the bricks are cracked or damaged.").setPrice(2).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(505).setContents("Brick hardness depends on amount of spawners in the dungeon.").setPrice(3).setUnlockRequirements(500),
				new KnowledgeFragmentCrafting(502).setRecipeFromRegistry(new ItemStack(BlockList.ravaged_brick_stairs,4)).setPrice(2).setUnlockRequirements(500).setUnlockCascade(513),
				new KnowledgeFragmentCrafting(503).setRecipeFromRegistry(new ItemStack(BlockList.ravaged_brick_slab,6)).setPrice(2).setUnlockRequirements(500).setUnlockCascade(515),
				new KnowledgeFragmentCrafting(504).setRecipeFromRegistry(new ItemStack(BlockList.ravaged_brick_fence,6)).setPrice(2).setUnlockRequirements(500).setUnlockCascade(517)
			}),
			
			RAVAGED_BRICK_GLOWING.setPos(8,13).setUnlockPrice(2).setDiscoveryReward(5).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(510).setContents("Glowing variation of the Ravaged Brick. It has the same level as Glowstone. Uncraftable.").setPrice(2).setUnlockOnDiscovery()
			}),
			
			RAVAGED_BRICK_STAIRS.setPos(4,13).setUnlockPrice(2).setDiscoveryReward(5).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(512).setContents("Stairs made of Ravaged Brick, slightly weaker than the full block.").setPrice(2).setUnlockOnDiscovery(),
				new KnowledgeFragmentCrafting(513).setRecipeFromRegistry(new ItemStack(BlockList.ravaged_brick_stairs,4)).setPrice(2).setUnlockRequirements(512).setUnlockCascade(502)
			}),
			
			RAVAGED_BRICK_SLAB.setPos(2,13).setUnlockPrice(2).setDiscoveryReward(5).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(514).setContents("Slab made of Ravaged Brick, weaker than the full block.").setPrice(2).setUnlockOnDiscovery(),
				new KnowledgeFragmentCrafting(515).setRecipeFromRegistry(new ItemStack(BlockList.ravaged_brick_slab,6)).setPrice(2).setUnlockRequirements(514).setUnlockCascade(503)
			}),
			
			RAVAGED_BRICK_FENCE.setPos(10,13).setUnlockPrice(2).setDiscoveryReward(5).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(516).setContents("Fence made of Ravaged Brick, weaker than the full block.").setPrice(2).setUnlockOnDiscovery(),
				new KnowledgeFragmentCrafting(517).setRecipeFromRegistry(new ItemStack(BlockList.ravaged_brick_fence,6)).setPrice(2).setUnlockRequirements(516).setUnlockCascade(504)
			}),
			
			CHARM_POUCH.setPos(9,16).setUnlockPrice(30).setDiscoveryReward(20).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(520).setContents("Magical pouch that allows creating, holding and activating Charms.").setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentCrafting(521).setRecipeFromRegistry(new ItemStack(ItemList.charm_pouch)).setPrice(8).setUnlockRequirements(520).setUnlockCascade(533),
				new KnowledgeFragmentText(522).setContents("Sneaking and using the pouch activates or deactives the Charms. Only one pouch can be active at a time.").setPrice(3).setUnlockRequirements(520)
			}),
			
			RUNES.setPos(5,16).setUnlockPrice(20).setDiscoveryReward(12).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(530).setContents("Runes are used to create Charms, and craft Charm Pouch.").setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(531).setContents("There are 6 types of Runes - Power, Agility, Vigor, Defense, Magic and Void. The Void rune is less common than other types.").setPrice(3).setUnlockRequirements(530),
				new KnowledgeFragmentText(532).setContents("It is required to explore the Ravaged Dungeon to get Runes. They are found in chests and rarely dropped by Lice.").setPrice(3).setUnlockRequirements(530),
				new KnowledgeFragmentCrafting(533).setRecipeFromRegistry(new ItemStack(ItemList.charm_pouch)).setPrice(8).setUnlockRequirements(530).setUnlockCascade(521)
			}),
			
			CHARMS.setPos(7,16).setUnlockPrice(35).setDiscoveryReward(28).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(540).setContents("Charms are player enchantments, used and crafted in the Charm Pouch.").setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(541).setContents("Between 3 to 5 Runes are combined together to create a Charm. Many combinations create same Charms with different attributes.").setPrice(5).setUnlockRequirements(540)
				// TODO list charms
			}),
			
			LOUSE.setPos(3,16).setUnlockPrice(15).setDiscoveryReward(15).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(550).setContents("An arthropod spawning in the Ravaged Dungeon.").setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(551).setContents("Lice have runic symbols on top, which modify their attributes or abilities.").setPrice(2).setUnlockRequirements(550),
				new KnowledgeFragmentText(552).setContents("The runic symbols can have one or two different colors, each corresponds to a Rune. Killing them can drop either of the Runes.").setPrice(5).setUnlockRequirements(551),
				new KnowledgeFragmentText(553).setContents("Basic Runes increase attributes, such as strength, speed or health. Void Rune (gray) gives the Louse one of special and extremely dangerous abilities.").setPrice(3).setUnlockRequirements(552)
			})
		});
		
		KnowledgeCategories.BIOME_ISLAND_MOUNTAINS.addKnowledgeObjects(new KnowledgeObject[]{
			INSTABILITY_ORB_ORE, STARDUST_ORE, INSTABILITY_ORB, POTION_OF_INSTABILITY, STARDUST_LINKED, BIOME_ISLANDS,
			
			BURNING_MOUNTAINS_BIOME.setPosToCenter().setUnlockPrice(60).setDiscoveryReward(45).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(560).setContents("Fire-oriented biome with huge mountains and a large amount of caves and ores. Apart from default ores, this biome also has common Igneous Rock Ore.").setPrice(5),
				new KnowledgeFragmentText(561).setContents("There are 2 variations of the biome - Scorching and Mine.").setPrice(2).setUnlockRequirements(560),
				new KnowledgeFragmentText(562).setContents("Scorching variation has large blobs of Cinder, streams and pools of Lava and Lilyfires. This is the only place where Fire Golems and Scorching Lenses spawn.").setPrice(10).setUnlockRequirements(561),
				new KnowledgeFragmentText(563).setContents("Mine variation spawns Haunted Miners, which protect Resource Pits (holes with ores from the End and Lava) and long patches of Overworld ores. Not all Overworld ores are present in a single island, usually one or two random ores are missing.").setPrice(10).setUnlockRequirements(561)
			}),
			
			BURNED_END_STONE.setPos(0,4).setUnlockPrice(5).setDiscoveryReward(8).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(570).setContents("Variation of End Stone found in the Burning Mountains Biome.").setPrice(2).setUnlockOnDiscovery()
			}),
			
			LILYFIRE.setPos(3,5).setUnlockPrice(4).setDiscoveryReward(5).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(580).setContents("Orange tulip found in the Burning Mountains (Scorching).").setPrice(2).setUnlockOnDiscovery(),
				new KnowledgeFragmentCrafting(581).setCustomRecipe(new ItemStack(Items.dye,2,14),new ItemStack[]{ new ItemStack(BlockList.crossed_decoration,1,BlockCrossedDecoration.dataLilyFire) }).setPrice(3).setUnlockRequirements(580)
			}),
			
			IGNEOUS_ROCK_ORE_LINKED.setPos(-3,5),
			
			IGNEOUS_ROCK_LINKED.setPos(-5,5),
			
			// =
			
			CINDER.setPos(-6,9).setUnlockPrice(5).setDiscoveryReward(5).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(590).setContents("Rock found in Burning Mountains (Scorching).").setPrice(2).setUnlockOnDiscovery()
			}),
			
			FIERY_ESSENCE_ALTAR.setPos(-6,15).setUnlockPrice(25).setDiscoveryReward(20).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(600).setContents("Fiery Essence Altar converts Fiery Essence into heat to speed up Furnaces, Brewing Stands and other devices.").setPrice(10).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(601).setContents("More Fiery Essence causes faster distribution of heat, having 512 Essence yields best speed.").setPrice(8).setUnlockRequirements(600)
			}),
			
			FIERY_ESSENCE.setPos(-6,13),
			
			FIRE_GOLEM.setPos(-7,11).setUnlockPrice(20).setDiscoveryReward(15).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(610).setContents("Fiery creature that spawns in Burning Mountains (Scorching).").setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(611).setContents("It has both a ranged attack and a melee attack.").setPrice(2).setUnlockRequirements(610),
				new KnowledgeFragmentText(612).setContents("During the ranged attack, it creates fireballs which cause large fiery craters.").setPrice(3).setUnlockRequirements(611),
				new KnowledgeFragmentText(613).setContents("When the golem is hit by an explosion, it ignores the damage and teleports. This ability has a short cooldown.").setPrice(3).setUnlockRequirements(611),
				new KnowledgeFragmentText(614).setContents("The golem drops 0-1 Fire Charges and 1-3 Fiery Essence.").setPrice(3).setUnlockRequirements(610).setUnlockCascade(82)
			}),
			
			SCORCHING_LENS.setPos(-5,11).setUnlockPrice(20).setDiscoveryReward(12).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(620).setContents("Small biped with a large eye, that spawns in Burning Mountains (Scorching).").setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(621).setContents("It attacks players in proximity by churning out fire from its eye.").setPrice(2).setUnlockRequirements(620),
				new KnowledgeFragmentText(622).setContents("The fire deals damage, has a chance of knocking the attacked creature back and increases the length of fire status effect.").setPrice(3).setUnlockRequirements(621),
				new KnowledgeFragmentText(623).setContents("It drops 0-2 Igneous Rock and 1-2 Fiery Essence.").setPrice(3).setUnlockRequirements(620).setUnlockCascade(82)
			}),
			
			// =
			
			FIRE_SHARD.setPos(6,11).setUnlockPrice(15).setDiscoveryReward(8).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(630).setContents("Fire Shard is a crafting material dropped by Haunted Miners.").setPrice(5).setUnlockOnDiscovery().setUnlockCascade(657),
				new KnowledgeFragmentCrafting(631).setRecipeFromRegistry(new ItemStack(ItemList.scorching_pickaxe)).setPrice(8).setUnlockRequirements(630).setUnlockCascade(643)
			}),
			
			SCORCHING_PICKAXE.setPos(6,13).setUnlockPrice(30).setDiscoveryReward(12).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(640).setContents("Special type of pickaxe that smelts blocks and applies fortune effect to ores.").setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(641).setContents("It only works on smeltable blocks and ores. Blocks that smelt into items are affected by fortune, including for example Iron Ore and Cactus.").setPrice(5).setUnlockRequirements(640),
				new KnowledgeFragmentText(642).setContents("Compatible blocks are also mined much faster.").setPrice(3).setUnlockRequirements(641),
				new KnowledgeFragmentCrafting(643).setRecipeFromRegistry(new ItemStack(ItemList.scorching_pickaxe)).setPrice(8).setUnlockRequirements(640).setUnlockCascade(631)
			}),
			
			HAUNTED_MINER.setPos(6,9).setUnlockPrice(20).setDiscoveryReward(15).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(650).setContents("Haunted Miner is a fiery flying mob that spawns in Burning Mountains (Mine).").setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(651).setContents("They guard the mines, they sense and attack players who have too many mining related items in their inventory, such as pickaxes, ores, ingots or minerals.").setPrice(5).setUnlockRequirements(651),
				new KnowledgeFragmentText(652).setContents("They burn any creature that gets too close to it.").setPrice(3).setUnlockRequirements(650),
				new KnowledgeFragmentText(653).setContents("The miners have 3 powerful spells:").setPrice(2).setUnlockRequirements(650),
				new KnowledgeFragmentText(654).setContents("First one is a projectile attack. The miner shoots two rapid projectiles that deal massive amount of damage and set hit mobs on fire.").setPrice(2).setUnlockRequirements(653),
				new KnowledgeFragmentText(655).setContents("Second attack is a blast wave, which blasts all living creatures away with higher strength the closer they are, causes damage to them and spawns fire all around.").setPrice(2).setUnlockRequirements(653),
				new KnowledgeFragmentText(656).setContents("Third attack is a lava attack, during which the miner continuously creates lava pillars that come from the ground, destroying all blocks that touch the lava.").setPrice(2).setUnlockRequirements(653),
				new KnowledgeFragmentText(657).setContents("When killed, they drop 0-2 Fire Shards and 10 experience.").setPrice(3).setUnlockRequirements(650).setUnlockCascade(630)
			})
		});
		
		KnowledgeCategories.BIOME_ISLAND_ENCHISLAND.addKnowledgeObjects(new KnowledgeObject[]{
			INSTABILITY_ORB_ORE, STARDUST_ORE, INSTABILITY_ORB, POTION_OF_INSTABILITY, STARDUST_LINKED, BIOME_ISLANDS,
			
			ENCHANTED_ISLAND_BIOME.setPosToCenter().setUnlockPrice(60).setDiscoveryReward(45).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(660).setContents("Very flat biome with small amount of caves.").setPrice(5),
				new KnowledgeFragmentText(661).setContents("There is currently only one variation - Homeland.").setPrice(2).setUnlockRequirements(660),
				new KnowledgeFragmentText(662).setContents("Homeland has lakes of Ender Goo, piles of Falling Obsidian and a strange Obsidian road-like structure. Endermen, Baby Endermen and Ender Guardians spawn there.").setPrice(10).setUnlockRequirements(661)
			}),
			
			ENCHANTED_END_STONE.setPos(0,4).setUnlockPrice(5).setDiscoveryReward(8).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(670).setContents("Variation of End Stone found in the Enchanted Island Biome.").setPrice(2).setUnlockOnDiscovery()
			}),
			
			FALLING_OBSIDIAN_LINKED.setPos(3,5),
			
			ENDERMAN_LINKED.setPos(-3,5),
			
			BABY_ENDERMAN.setPos(-1,8).setUnlockPrice(20).setDiscoveryReward(8).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(680).setContents("Baby Enderman spawns in the Enchanted Island (Homeland).").setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(681).setContents("If attacked, the parents will teleport and protect the baby.").setPrice(2).setUnlockRequirements(680),
				new KnowledgeFragmentText(682).setContents("Sometimes, it may approach a player and steal random item from their inventory.").setPrice(2).setUnlockRequirements(680),
				new KnowledgeFragmentText(683).setContents("The item is dropped on death. Each baby also has their own priorities and may exchange stolen item with another one on the ground.").setPrice(3).setUnlockRequirements(682),
				new KnowledgeFragmentText(684).setContents("It will not steal from players who wear Enderman Head.").setPrice(3).setUnlockRequirements(682)
			}),
			
			ENDER_GUARDIAN.setPos(1,8).setUnlockPrice(15).setDiscoveryReward(10).setFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(690).setContents("Large golem-like creature that spawns in the Enchanted Island (Homeland).").setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(691).setContents("It only attacks players when provoked.").setPrice(2).setUnlockRequirements(690),
				new KnowledgeFragmentText(692).setContents("When attacking, it charges the player with melee attacks, and after a short while it teleports away and creates a blast under the player.").setPrice(5).setUnlockRequirements(690),
				new KnowledgeFragmentText(693).setContents("The guardian drops 0-1 Ender Pearls and 1-3 Obsidian blocks.").setPrice(3).setUnlockRequirements(690)
			})
		});
		
		// next: 790
		
		Stopwatch.finish("KnowledgeRegistrations");
		
		if (Log.isDebugEnabled()){
			Stopwatch.time("KnowledgeRegistrations - Stats");
			
			int amtObjects = 0, amtFragments = 0, totalObjPrice = 0, totalFragPrice = 0, totalReward = 0;
			
			for(KnowledgeObject<?> obj:KnowledgeObject.getAllObjects()){
				if (obj == HELP)continue;
				else if (obj.getDiscoveryReward() == 0 || obj.getUnlockPrice() == 0)throw new IllegalStateException("Knowledge Object "+obj.globalID+"/"+obj.getTooltip()+" has illegal reward ("+obj.getDiscoveryReward()+") or unlock price ("+obj.getUnlockPrice()+").");
				
				for(KnowledgeFragment fragment:obj.getFragments()){
					totalFragPrice += fragment.getPrice();
					if (fragment.getPrice() == 0)throw new IllegalStateException("Knowledge Fragment "+fragment.globalID+" has illegal unlock price.");
					
					for(int id:fragment.getUnlockRequirements()){
						if (KnowledgeFragment.getById(id) == null)throw new IllegalStateException("Knowledge Fragment "+fragment.globalID+" has invalid unlock requirement ID "+id+".");
					}

					for(int id:fragment.getUnlockCascade()){
						if (KnowledgeFragment.getById(id) == null)throw new IllegalStateException("Knowledge Fragment "+fragment.globalID+" has invalid unlock cascade ID "+id+".");
					}
				}
				
				++amtObjects;
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
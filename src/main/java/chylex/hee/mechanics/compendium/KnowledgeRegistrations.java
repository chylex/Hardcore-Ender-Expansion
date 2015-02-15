package chylex.hee.mechanics.compendium;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import chylex.hee.block.BlockCrossedDecoration;
import chylex.hee.block.BlockEndstoneTerrain;
import chylex.hee.block.BlockList;
import chylex.hee.block.BlockSpecialEffects;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.EntityMiniBossEnderEye;
import chylex.hee.entity.boss.EntityMiniBossFireFiend;
import chylex.hee.entity.mob.EntityMobAngryEnderman;
import chylex.hee.entity.mob.EntityMobBabyEnderman;
import chylex.hee.entity.mob.EntityMobEnderGuardian;
import chylex.hee.entity.mob.EntityMobEndermage;
import chylex.hee.entity.mob.EntityMobEnderman;
import chylex.hee.entity.mob.EntityMobFireGolem;
import chylex.hee.entity.mob.EntityMobHauntedMiner;
import chylex.hee.entity.mob.EntityMobHomelandEnderman;
import chylex.hee.entity.mob.EntityMobInfestedBat;
import chylex.hee.entity.mob.EntityMobLouse;
import chylex.hee.entity.mob.EntityMobScorchingLens;
import chylex.hee.entity.mob.EntityMobVampiricBat;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.compendium.content.KnowledgeCategory;
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
import chylex.hee.mechanics.compendium.util.KnowledgeUtils;
import chylex.hee.mechanics.enhancements.EnhancementHandler;
import chylex.hee.mechanics.enhancements.IEnhancementEnum;
import chylex.hee.mechanics.enhancements.types.EnderPearlEnhancements;
import chylex.hee.mechanics.enhancements.types.EssenceAltarEnhancements;
import chylex.hee.mechanics.enhancements.types.SpatialDashGemEnhancements;
import chylex.hee.mechanics.enhancements.types.TNTEnhancements;
import chylex.hee.mechanics.enhancements.types.TransferenceGemEnhancements;
import chylex.hee.mechanics.essence.EssenceType;
import chylex.hee.system.logging.Log;
import chylex.hee.system.logging.Stopwatch;
import cpw.mods.fml.common.Loader;

public final class KnowledgeRegistrations{
	public static final KnowledgeObject<? extends IKnowledgeObjectInstance<?>>
		HELP = new KnowledgeObject<>(new ObjectDummy("HalpPlz")),
		
		// ===
		
		STRONGHOLD = dummy("Stronghold",new ItemStack(Blocks.stonebrick),"ec.title.stronghold"),
		ADVENTURERS_DIARY = create(ItemList.adventurers_diary),
		ENDERMAN_HEAD = create(ItemList.enderman_head),
		MUSIC_DISKS = create(ItemList.music_disk),
		END_PORTAL = create(Blocks.end_portal_frame,"ec.title.portal"),
		ALTAR_NEXUS = create(ItemList.altar_nexus),
		BASIC_ESSENCE_ALTAR = create(BlockList.essence_altar,EssenceType.INVALID.id),
		ENDERMAN = create(EntityMobEnderman.class),
		SILVERFISH = create(EntitySilverfish.class),
		
		ESSENCE = create(ItemList.essence),
		
		// ===
		
		DRAGON_LAIR = dummy("DragonLair",new ItemStack(Blocks.dragon_egg),"ec.title.dragonLair"),
		END_STONE = create(Blocks.end_stone),
		FALLING_OBSIDIAN = create(BlockList.obsidian_falling),
		DRAGON_ESSENCE_ALTAR = create(BlockList.essence_altar,EssenceType.DRAGON.id),
		END_POWDER_ORE = create(BlockList.end_powder_ore),
		ENHANCED_BREWING_STAND = create(ItemList.enhanced_brewing_stand),
		DRAGON_EGG = create(Blocks.dragon_egg),
		DRAGON_ESSENCE = link(ESSENCE,new ItemStack(ItemList.essence,EssenceType.DRAGON.getItemDamage()),"ec.title.essence"),
		END_POWDER = create(ItemList.end_powder),
		TEMPLE_CALLER = create(ItemList.temple_caller),
		ENDER_DRAGON = new KnowledgeObject<ObjectMob>(new ObjectMob(EntityBossDragon.class),new ItemStack(Blocks.dragon_egg),"ec.title.enderDragon"),
		ANGRY_ENDERMAN = create(EntityMobAngryEnderman.class),
		VAMPIRE_BAT = create(EntityMobVampiricBat.class),
		
		ENDER_PEARL_ENHANCEMENTS = dummy("Enhancements.EnderPearl",new ItemStack(ItemList.enhanced_ender_pearl),"ec.title.enh.enderPearl"),
		ESSENCE_ALTAR_ENHANCEMENTS = dummy("Enhancements.EssenceAltar",new ItemStack(BlockList.essence_altar),"ec.title.enh.essenceAltar"),
		TNT_ENHANCEMENTS = dummy("Enhancements.TNT",new ItemStack(BlockList.enhanced_tnt),"ec.title.enh.tnt"),
		SPATIAL_DASH_GEM_ENHANCEMENTS = dummy("Enhancements.SpatialDashGem",new ItemStack(ItemList.spatial_dash_gem),"ec.title.enh.spatialDashGem"),
		TRANSFERENCE_GEM_ENHANCEMENTS = dummy("Enhancements.TransferenceGem",new ItemStack(ItemList.transference_gem),"ec.title.enh.transferenceGem"),
		
		// ===
		
		ENDSTONE_BLOB = dummy("EndstoneBlob",new ItemStack(Blocks.end_stone),"ec.title.endstoneBlob"),
		IGNEOUS_ROCK_ORE = create(BlockList.igneous_rock_ore),
		DEATH_FLOWER = create(BlockList.death_flower),
		ENDER_GOO = create(ItemList.bucket_ender_goo),
		IGNEOUS_ROCK = create(ItemList.igneous_rock),
		TRANSPORT_BEACON = create(BlockList.transport_beacon),
		
		// ===
		
		DUNGEON_TOWER = dummy("DungeonTower",new ItemStack(BlockList.obsidian_special,1),"ec.title.dungeonTower"),
		OBSIDIAN_STAIRS = create(BlockList.obsidian_stairs),
		OBSIDIAN_SMOOTH = create(BlockList.obsidian_special,0),
		OBSIDIAN_CHISELED = create(BlockList.obsidian_special,1),
		OBSIDIAN_PILLAR = create(BlockList.obsidian_special,2),
		OBSIDIAN_SMOOTH_GLOWING = create(BlockList.obsidian_special_glow,0),
		OBSIDIAN_CHISELED_GLOWING = create(BlockList.obsidian_special_glow,1),
		OBSIDIAN_PILLAR_GLOWING = create(BlockList.obsidian_special_glow,2),
		ENERGY = create(BlockList.energy_cluster,"Energy"),
		ENDIUM_ORE = create(BlockList.endium_ore),
		ENDIUM_BLOCK = create(BlockList.endium_block),
		VOID_CHEST = create(BlockList.void_chest),
		SPATIAL_DASH_GEM = create(ItemList.spatial_dash_gem),
		ENDIUM_INGOT = create(ItemList.endium_ingot),
		BIOME_COMPASS = create(ItemList.biome_compass),
		ENDER_EYE = create(EntityMiniBossEnderEye.class),
		ANGRY_ENDERMAN_LINKED = new LinkedKnowledgeObject<>(ANGRY_ENDERMAN),
		
		// ===
		
		METEOROID = dummy("Meteoroid",new ItemStack(BlockList.sphalerite),"ec.title.meteoroid"),
		SPHALERITE = create(BlockList.sphalerite,0),
		SPHALERITE_WITH_STARDUST = create(BlockList.sphalerite,1),
		STARDUST = create(ItemList.stardust),
		
		// ===
		
		BIOME_ISLANDS = dummy("BiomeIslands",new ItemStack(BlockList.special_effects,1,BlockSpecialEffects.metaBiomeIslandIcon),"ec.title.biomeIslands"),
		INSTABILITY_ORB_ORE = create(BlockList.instability_orb_ore),
		STARDUST_ORE = create(BlockList.stardust_ore),
		INSTABILITY_ORB = create(ItemList.instability_orb),
		POTION_OF_INSTABILITY = create(ItemList.potion_of_instability),
		STARDUST_LINKED = link(STARDUST),
		DECOMPOSITION_TABLE = create(BlockList.decomposition_table),
		EXPERIENCE_TABLE = create(BlockList.experience_table),
		ACCUMULATION_TABLE = create(BlockList.accumulation_table),
		EXTRACTION_TABLE = create(BlockList.extraction_table),
		
		// ===
		
		INFESTED_FOREST_BIOME = dummy("InfestedForest",new ItemStack(BlockList.end_terrain,1,BlockEndstoneTerrain.metaInfested),"ec.title.biome.infestedForest"),
		INFESTED_END_STONE = create(BlockList.end_terrain,0),
		INFESTED_GRASS = create(BlockList.crossed_decoration,BlockCrossedDecoration.dataInfestedGrass),
		INFESTED_TALL_GRASS = create(BlockList.crossed_decoration,BlockCrossedDecoration.dataInfestedTallgrass),
		INFESTED_FERN = create(BlockList.crossed_decoration,BlockCrossedDecoration.dataInfestedFern),
		INFESTATION_REMEDY = create(ItemList.infestation_remedy),
		INFESTED_BAT = create(EntityMobInfestedBat.class),
		SILVERFISH_LINKED = link(SILVERFISH),
		
		THORNY_BUSH = create(BlockList.crossed_decoration,BlockCrossedDecoration.dataThornBush),
		SPOOKY_LOG = create(BlockList.spooky_log),
		SPOOKY_LEAVES = create(BlockList.spooky_leaves),
		DRY_SPLINTER = create(ItemList.dry_splinter),
		GHOST_AMULET = create(ItemList.ghost_amulet),
		ECTOPLASM = create(ItemList.ectoplasm),
		SPECTRAL_TEAR = create(ItemList.spectral_tear),
		LIVING_MATTER = create(ItemList.living_matter),
		CURSE = create(ItemList.curse,"ec.title.curses"),
		POTION_OF_PURITY = create(ItemList.potion_of_purity),
		CURSE_AMULET = create(ItemList.curse_amulet),
		
		RAVAGED_DUNGEON = dummy("RavagedDungeon",new ItemStack(BlockList.ravaged_brick),"ec.title.ravagedDungeon"),
		RAVAGED_BRICK = create(BlockList.ravaged_brick),
		RAVAGED_BRICK_GLOWING = create(BlockList.ravaged_brick_glow),
		RAVAGED_BRICK_STAIRS = create(BlockList.ravaged_brick_stairs),
		RAVAGED_BRICK_SLAB = create(BlockList.ravaged_brick_slab),
		RAVAGED_BRICK_FENCE = create(BlockList.ravaged_brick_fence),
		CHARM_POUCH = create(ItemList.charm_pouch),
		RUNES = create(ItemList.rune,"Runes"),
		CHARMS = create(ItemList.charm,"Charms"),
		LOUSE = create(EntityMobLouse.class),
		
		// ===
		
		BURNING_MOUNTAINS_BIOME = dummy("BurningMountains",new ItemStack(BlockList.end_terrain,1,BlockEndstoneTerrain.metaBurned),"ec.title.biome.burningMountains"),
		BURNED_END_STONE = create(BlockList.end_terrain,1),
		LILYFIRE = create(BlockList.crossed_decoration,BlockCrossedDecoration.dataLilyFire),
		IGNEOUS_ROCK_ORE_LINKED = link(IGNEOUS_ROCK_ORE),
		IGNEOUS_ROCK_LINKED = link(IGNEOUS_ROCK),
		
		CINDER = create(BlockList.cinder),
		DUNGEON_PUZZLE = create(BlockList.dungeon_puzzle,"ec.title.dungeonPuzzle"),
		FIERY_ESSENCE_ALTAR = create(BlockList.essence_altar,EssenceType.FIERY.id),
		FIERY_ESSENCE = link(ESSENCE,new ItemStack(ItemList.essence,1,EssenceType.FIERY.getItemDamage()),"ec.title.essence"),
		FIRE_GOLEM = create(EntityMobFireGolem.class),
		SCORCHING_LENS = create(EntityMobScorchingLens.class),
		FIRE_FIEND = create(EntityMiniBossFireFiend.class),
		
		INFERNIUM = create(ItemList.infernium),
		SCORCHING_PICKAXE = create(ItemList.scorching_pickaxe),
		HAUNTED_MINER = create(EntityMobHauntedMiner.class),
		
		// ===
		
		ENCHANTED_ISLAND_BIOME = dummy("EnchantedIsland",new ItemStack(BlockList.end_terrain,1,BlockEndstoneTerrain.metaEnchanted),"ec.title.biome.enchantedIsland"),
		ENCHANTED_END_STONE = create(BlockList.end_terrain,2),
		
		FALLING_OBSIDIAN_LINKED = link(FALLING_OBSIDIAN),
		PERSEGRIT = create(BlockList.persegrit),
		ENDERMAN_LINKED = link(ENDERMAN),
		
		ARCANE_SHARD = create(ItemList.arcane_shard),
		BLANK_GEM = create(ItemList.blank_gem),
		SPATIAL_DASH_GEM_LINKED = link(SPATIAL_DASH_GEM),
		TRANSFERENCE_GEM = create(ItemList.transference_gem),
		BABY_ENDERMAN = create(EntityMobBabyEnderman.class),
		HOMELAND_ENDERMAN = create(EntityMobHomelandEnderman.class),
		
		LABORATORY = dummy("Laboratory",new ItemStack(BlockList.laboratory_floor),"ec.title.laboratory"),
		LABORATORY_OBSIDIAN = create(BlockList.laboratory_obsidian),
		LABORATORY_GLASS = create(BlockList.laboratory_glass),
		LABORATORY_FLOOR = create(BlockList.laboratory_floor),
		LABORATORY_STAIRS = create(BlockList.laboratory_stairs),
		OBSIDIAN_FRAGMENT = create(ItemList.obsidian_fragment),
		OBSIDIAN_ROD = create(ItemList.obsidian_rod),
		AURICION = create(ItemList.auricion),
		ENERGY_WAND_CORE = create(ItemList.energy_wand_core),
		ENERGY_WAND = create(ItemList.energy_wand),
		ENDER_GUARDIAN = create(EntityMobEnderGuardian.class),
		ENDERMAGE = create(EntityMobEndermage.class);
	
	public static void initialize(){
		Stopwatch.time("KnowledgeRegistrations");
		
		/*
		 * General information
		 * ===================
		 * Numbers below are not set in stone, they will vary a bit around that value but keep the meaning.
		 * 
		 * Object price
		 * ============
		 * early | mid | late | description
		 * ------+-----+------+------------
		 *     5 |   7 |   10 | easily found objects without any feature mechanics
		 *     8 |  10 |   15 | easily found objects with some mechanics
		 *    12 |  15 |   25 | basic objects with mechanics
		 *    20 |  25 |   35 | important objects with mechanics
		 *    30 |  40 |   50 | major objects
		 *    40 |  75 |  100 | phase objects
		 * 
		 * (might vary)
		 * 
		 * Fragment price
		 * ==============
		 * early | mid | late | description
		 * ------+-----+------+------------
		 *     1 |   2 |    3 | bulk data
		 *     2 |   4 |    6 | unimportant or generic information
		 *     3 |   5 |    8 | semi-important or additional information
		 *     5 |   7 |   10 | basic information about important objects
		 *     8 |  10 |   15 | important and key information
		 * 
		 * Game phases
		 * ===========
		 * early - overworld, dragon lair, endstone blobs, dungeon tower
		 * mid - meteoroids, biome islands
		 * late - 
		 *
		 * Object discovery reward
		 * =======================
		 * Most objects should give enough points to unlock some/all fragments.
		 * Uncommon and rare objects should give an additional bonus.
		 * Purely visual objects should also give a small bonus for discovery.
		 * Objects which are made in a way they should be unlocked with points would only give small reward.
		 */
		
		HELP.addFragments(new KnowledgeFragment[]{
			new KnowledgeFragmentText(0),
			new KnowledgeFragmentText(1),
			new KnowledgeFragmentText(2),
			new KnowledgeFragmentText(3),
			new KnowledgeFragmentText(4),
			new KnowledgeFragmentText(5),
			new KnowledgeFragmentText(6),
			new KnowledgeFragmentText(7),
			new KnowledgeFragmentText(8),
			new KnowledgeFragmentText(9),
			new KnowledgeFragmentText(18)
		});
		
		if (Loader.isModLoaded("NotEnoughItems")){
			HELP.addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(19)
			});
		}
		
		ESSENCE.setNonBuyable().setDiscoveryReward(12).addFragments(new KnowledgeFragment[]{
			new KnowledgeFragmentText(80).setPrice(2).setUnlockOnDiscovery(),
			new KnowledgeFragmentText(81).setPrice(2).setUnlockCascade(704),
			new KnowledgeFragmentText(82).setPrice(2).setUnlockCascade(614,623)
		});
		
		// ===
		
		KnowledgeCategories.OVERWORLD.addKnowledgeObjects(new KnowledgeObject[]{
			STRONGHOLD.setCategoryObject(KnowledgeCategories.OVERWORLD).setUnlockPrice(5).setDiscoveryReward(10).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(710).setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(711).setPrice(2).setUnlockRequirements(710),
				new KnowledgeFragmentText(712).setPrice(2).setUnlockRequirements(710),
				new KnowledgeFragmentText(713).setPrice(3).setUnlockRequirements(712)
			}),
			
			ADVENTURERS_DIARY.setPos(0,0).setUnlockPrice(5).setDiscoveryReward(8).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(10).setPrice(2).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(11).setPrice(2).setUnlockOnDiscovery()
			}),
			
			ENDERMAN_HEAD.setPos(2,0).setUnlockPrice(5).setDiscoveryReward(10).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(20).setPrice(2).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(21).setPrice(2).setUnlockRequirements(20)
			}),
			
			MUSIC_DISKS.setPos(4,0).setUnlockPrice(5).setDiscoveryReward(5).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(30).setPrice(2)
			}),
			
			END_PORTAL.setPos(2,6).setNonBuyable().setDiscoveryReward(28).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(1410).setNonBuyable().setUnlockOnDiscovery(),
				new KnowledgeFragmentText(1411).setNonBuyable().setUnlockOnDiscovery(),
				new KnowledgeFragmentText(1412).setNonBuyable().setUnlockOnDiscovery(),
				new KnowledgeFragmentText(1413).setNonBuyable().setUnlockOnDiscovery()
			}),
			
			ALTAR_NEXUS.setPos(1,9).setUnlockPrice(10).setDiscoveryReward(5).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(40).setPrice(8),
				new KnowledgeFragmentCrafting(41).setRecipeFromRegistry(new ItemStack(ItemList.altar_nexus)).setPrice(5).setUnlockRequirements(40),
				new KnowledgeFragmentCrafting(42).setRecipeFromRegistry(new ItemStack(BlockList.essence_altar)).setPrice(8).setUnlockCascade(50)
			}),
			
			BASIC_ESSENCE_ALTAR.setPos(3,9).setNonBuyable().setDiscoveryReward(20).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentCrafting(50).setRecipeFromRegistry(new ItemStack(BlockList.essence_altar)).setPrice(8).setUnlockCascade(42),
				new KnowledgeFragmentText(51).setUnlockOnDiscovery().setPrice(5).setUnlockRequirements(50),
				new KnowledgeFragmentText(52).setPrice(2).setUnlockRequirements(51),
				new KnowledgeFragmentText(53).setPrice(6).setUnlockRequirements(51), // TODO remove sockets
				new KnowledgeFragmentText(54).setPrice(2).setUnlockRequirements(53),
				new KnowledgeFragmentText(55).setPrice(2).setUnlockRequirements(53)
			}),
			
			ENDERMAN.setPos(1,3).setUnlockPrice(5).setDiscoveryReward(15).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(60).setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(61).setPrice(2).setUnlockRequirements(60),
				new KnowledgeFragmentText(62).setPrice(2).setUnlockRequirements(60),
				new KnowledgeFragmentText(63).setPrice(3).setUnlockRequirements(60)
			}),
			
			SILVERFISH.setPos(3,3).setUnlockPrice(5).setDiscoveryReward(20).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(70).setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(71).setPrice(2).setUnlockRequirements(70),
				new KnowledgeFragmentText(72).setPrice(3).setUnlockRequirements(71)
			})
		});
		
		// ===
		
		KnowledgeCategories.DRAGON_LAIR.addKnowledgeObjects(new KnowledgeObject[]{
			DRAGON_LAIR.setCategoryObject(KnowledgeCategories.DRAGON_LAIR).setUnlockPrice(45).setDiscoveryReward(15).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(750).setPrice(5),
				new KnowledgeFragmentText(751).setPrice(2).setUnlockRequirements(750).setUnlockCascade(191),
				new KnowledgeFragmentText(752).setPrice(1).setUnlockRequirements(751),
				new KnowledgeFragmentText(753).setPrice(2).setUnlockRequirements(752),
				new KnowledgeFragmentText(754).setPrice(2).setUnlockRequirements(752),
				new KnowledgeFragmentText(755).setPrice(2).setUnlockRequirements(752)
			}),
			
			END_STONE.setPos(0,0).setUnlockPrice(5).setDiscoveryReward(8).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(90).setPrice(2).setUnlockOnDiscovery()
			}),
			
			FALLING_OBSIDIAN.setPos(2,0).setUnlockPrice(8).setDiscoveryReward(12).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(100).setPrice(2).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(101).setPrice(2),
				new KnowledgeFragmentText(102).setPrice(2).setUnlockRequirements(100),
				new KnowledgeFragmentText(103).setPrice(2).setUnlockRequirements(100),
				new KnowledgeFragmentText(104).setPrice(2).setUnlockRequirements(100)
			}),
			
			DRAGON_ESSENCE_ALTAR.setPos(3,10).setUnlockPrice(25).setDiscoveryReward(22).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(110).setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(111).setPrice(8).setUnlockRequirements(110),
				new KnowledgeFragmentText(112).setPrice(3).setUnlockRequirements(111),
				new KnowledgeFragmentText(113).setPrice(5).setUnlockRequirements(110),
				new KnowledgeFragmentText(114).setPrice(2).setUnlockRequirements(110),
				new KnowledgeFragmentItemConversion(115).setItems(new ItemStack(Items.brewing_stand),new ItemStack(ItemList.enhanced_brewing_stand)).setPrice(2).setUnlockRequirements(114),
				new KnowledgeFragmentItemConversion(116).setItems(new ItemStack(Items.ender_eye),new ItemStack(ItemList.temple_caller)).setNonBuyable(), // 180
				new KnowledgeFragmentItemConversion(117).setItems(new ItemStack(ItemList.ghost_amulet),new ItemStack(ItemList.ghost_amulet,1,1)).setNonBuyable(), // 463
			}),
			
			END_POWDER_ORE.setPos(6,0).setUnlockPrice(15).setDiscoveryReward(12).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(120).setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(121).setPrice(2).setUnlockOnDiscovery().setUnlockRequirements(120),
				new KnowledgeFragmentText(122).setPrice(5).setUnlockRequirements(120),
				new KnowledgeFragmentText(123).setPrice(3).setUnlockRequirements(122)
			}),
			
			ENHANCED_BREWING_STAND.setPos(4,12).setUnlockPrice(18).setDiscoveryReward(20).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(130).setPrice(5),
				new KnowledgeFragmentText(131).setPrice(2).setUnlockRequirements(130),
				new KnowledgeFragmentText(132).setPrice(5).setUnlockRequirements(130).setUnlockCascade(166),
				new KnowledgeFragmentText(133).setPrice(2).setUnlockRequirements(130),
				new KnowledgeFragmentItemConversion(134).setItems(new ItemStack(ItemList.instability_orb),new ItemStack(ItemList.potion_of_instability)).setNonBuyable(), // 741
				new KnowledgeFragmentItemConversion(135).setItems(new ItemStack(ItemList.silverfish_blood),new ItemStack(ItemList.infestation_remedy)).setNonBuyable(), // 731
				new KnowledgeFragmentItemConversion(136).setItems(new ItemStack(ItemList.ectoplasm),new ItemStack(ItemList.potion_of_purity)).setNonBuyable() // 951
			}),
			
			DRAGON_EGG.setPos(1,8).setUnlockPrice(12).setDiscoveryReward(15).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(150).setPrice(5).setUnlockOnDiscovery().setUnlockCascade(705),
				new KnowledgeFragmentText(151).setPrice(2).setUnlockRequirements(150),
				new KnowledgeFragmentText(152).setPrice(8).setUnlockRequirements(150),
				new KnowledgeFragmentText(153).setPrice(3).setUnlockRequirements(150)
			}),
			
			DRAGON_ESSENCE.setPos(3,8),
			
			END_POWDER.setPos(8,0).setUnlockPrice(20).setDiscoveryReward(20).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(160).setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(161).setPrice(5).setUnlockOnDiscovery().setUnlockRequirements(160),
				new KnowledgeFragmentText(162).setPrice(2).setUnlockRequirements(161),
				new KnowledgeFragmentText(163).setPrice(2).setUnlockRequirements(162),
				new KnowledgeFragmentText(164).setPrice(3).setUnlockRequirements(163),
				new KnowledgeFragmentText(165).setPrice(2).setUnlockRequirements(162),
				new KnowledgeFragmentText(166).setPrice(5).setUnlockRequirements(160).setUnlockCascade(132),
				new KnowledgeFragmentText(167).setPrice(3).setUnlockCascade(244)
			}),
			
			TEMPLE_CALLER.setPos(2,12).setUnlockPrice(18).setDiscoveryReward(20).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(180).setPrice(5).setUnlockCascade(116),
				new KnowledgeFragmentText(181).setPrice(2),
				new KnowledgeFragmentText(182).setPrice(8),
				new KnowledgeFragmentText(183).setPrice(3).setUnlockRequirements(182),
				new KnowledgeFragmentText(184).setPrice(3).setUnlockRequirements(183)
			}),
			
			ENDER_DRAGON.setPos(1,3).setUnlockPrice(20).setDiscoveryReward(55).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(190).setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(191).setPrice(2).setUnlockOnDiscovery().setUnlockRequirements(190).setUnlockCascade(751),
				new KnowledgeFragmentText(192).setPrice(3).setUnlockRequirements(190),
				new KnowledgeFragmentText(193).setPrice(3).setUnlockRequirements(191),
				new KnowledgeFragmentText(194).setPrice(3).setUnlockRequirements(192,193),
				new KnowledgeFragmentText(195).setPrice(3).setUnlockRequirements(194),
				new KnowledgeFragmentText(196).setPrice(5).setUnlockRequirements(194),
				new KnowledgeFragmentText(197).setPrice(2).setUnlockRequirements(196),
				new KnowledgeFragmentText(198).setPrice(2).setUnlockRequirements(196),
				new KnowledgeFragmentText(700).setPrice(2).setUnlockRequirements(196),
				new KnowledgeFragmentText(702).setPrice(2).setUnlockRequirements(196),
				new KnowledgeFragmentText(703).setPrice(2).setUnlockRequirements(196).setUnlockCascade(210),
				new KnowledgeFragmentText(704).setPrice(2).setUnlockRequirements(190).setUnlockCascade(81),
				new KnowledgeFragmentText(705).setPrice(2).setUnlockRequirements(704).setUnlockCascade(150)
			}),
			
			ANGRY_ENDERMAN.setPos(0,5).setUnlockPrice(8).setDiscoveryReward(5).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(200).setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(201).setPrice(3).setUnlockRequirements(200)
			}),
			
			VAMPIRE_BAT.setPos(2,5).setUnlockPrice(8).setDiscoveryReward(8).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(210).setPrice(5).setUnlockOnDiscovery().setUnlockCascade(703),
				new KnowledgeFragmentText(211).setPrice(2).setUnlockRequirements(210),
				new KnowledgeFragmentText(212).setPrice(3).setUnlockRequirements(210)
			}),
			
			ENDER_PEARL_ENHANCEMENTS.setPos(5,0).setUnlockPrice(8).setDiscoveryReward(10).addFragments(KnowledgeUtils.createEnhancementFragments(EnderPearlEnhancements.class,170,3,10)),
			
			ESSENCE_ALTAR_ENHANCEMENTS.setPos(5,2).setUnlockPrice(10).setDiscoveryReward(10).addFragments(KnowledgeUtils.createEnhancementFragments(EssenceAltarEnhancements.class,1470,5,10)),
			
			TNT_ENHANCEMENTS.setPos(5,4).setUnlockPrice(10).setDiscoveryReward(10).addFragments(KnowledgeUtils.createEnhancementFragments(TNTEnhancements.class,140,3,10)),
			
			SPATIAL_DASH_GEM_ENHANCEMENTS.setPos(5,6).setUnlockPrice(12).setDiscoveryReward(10).addFragments(KnowledgeUtils.createEnhancementFragments(SpatialDashGemEnhancements.class,1480,4,10)),
			
			TRANSFERENCE_GEM_ENHANCEMENTS.setPos(5,8).setUnlockPrice(12).setDiscoveryReward(10).addFragments(KnowledgeUtils.createEnhancementFragments(TransferenceGemEnhancements.class,1490,4,10))
		});
		
		// ===
		
		KnowledgeCategories.ENDSTONE_BLOBS.addKnowledgeObjects(new KnowledgeObject[]{
			ENDSTONE_BLOB.setCategoryObject(KnowledgeCategories.ENDSTONE_BLOBS).setUnlockPrice(40).setDiscoveryReward(10).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(220).setPrice(5),
				new KnowledgeFragmentText(221).setPrice(3).setUnlockRequirements(220),
				new KnowledgeFragmentText(222).setPrice(5).setUnlockRequirements(221),
				new KnowledgeFragmentText(223).setPrice(5).setUnlockRequirements(221),
				new KnowledgeFragmentText(224).setPrice(5).setUnlockRequirements(221)
			}),
			
			IGNEOUS_ROCK_ORE.setPos(0,0).setUnlockPrice(10).setDiscoveryReward(10).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(230).setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(231).setPrice(2).setUnlockRequirements(230),
				new KnowledgeFragmentText(232).setPrice(3).setUnlockRequirements(230)
			}),
			
			DEATH_FLOWER.setPos(3,0).setUnlockPrice(15).setDiscoveryReward(10).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(240).setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(241).setPrice(2).setUnlockRequirements(240),
				new KnowledgeFragmentText(242).setPrice(3).setUnlockRequirements(241),
				new KnowledgeFragmentText(243).setPrice(5).setUnlockRequirements(241),
				new KnowledgeFragmentText(244).setPrice(3).setUnlockRequirements(241).setUnlockCascade(167),
				new KnowledgeFragmentCrafting(245).setCustomRecipe(new ItemStack(Items.dye,2,13),new ItemStack[]{ new ItemStack(BlockList.death_flower,1,0) }).setPrice(2).setUnlockRequirements(240),
				new KnowledgeFragmentCrafting(246).setCustomRecipe(new ItemStack(Items.dye,2,8),new ItemStack[]{ new ItemStack(BlockList.death_flower,1,15) }).setPrice(2).setUnlockRequirements(243)
			}),
			
			ENDER_GOO.setPos(6,0).setUnlockPrice(8).setDiscoveryReward(12).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(250).setPrice(2).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(251).setPrice(5).setUnlockRequirements(250),
				new KnowledgeFragmentText(252).setPrice(2).setUnlockRequirements(251),
				new KnowledgeFragmentText(253).setPrice(3).setUnlockRequirements(250)
			}),
			
			IGNEOUS_ROCK.setPos(0,2).setUnlockPrice(18).setDiscoveryReward(15).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(260).setPrice(2).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(261).setPrice(2).setUnlockRequirements(260),
				new KnowledgeFragmentText(262).setPrice(3).setUnlockRequirements(260),
				new KnowledgeFragmentText(263).setPrice(3).setUnlockRequirements(260)
			}),
			
			TRANSPORT_BEACON.setPos(9,0).setUnlockPrice(20).setDiscoveryReward(20).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(960).setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(961).setPrice(5).setUnlockRequirements(960),
				new KnowledgeFragmentText(962).setPrice(5).setUnlockRequirements(960),
				new KnowledgeFragmentText(963).setPrice(3).setUnlockRequirements(960),
			})
		});
		
		// ===
		
		KnowledgeCategories.DUNGEON_TOWER.addKnowledgeObjects(new KnowledgeObject[]{
			DUNGEON_TOWER.setCategoryObject(KnowledgeCategories.DUNGEON_TOWER).setUnlockPrice(55).setDiscoveryReward(25).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(270).setPrice(5),
				new KnowledgeFragmentText(271).setPrice(3).setUnlockRequirements(270),
				new KnowledgeFragmentText(272).setPrice(2).setUnlockRequirements(270),
				new KnowledgeFragmentText(273).setPrice(3).setUnlockRequirements(272),
				new KnowledgeFragmentText(274).setPrice(5).setUnlockRequirements(272),
				new KnowledgeFragmentText(275).setPrice(2).setUnlockRequirements(272)
			}),
			
			OBSIDIAN_STAIRS.setPos(3,4).setUnlockPrice(1).setDiscoveryReward(3).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(280).setPrice(2).setUnlockOnDiscovery()
			}),
			
			OBSIDIAN_SMOOTH.setPos(1,0).setUnlockPrice(1).setDiscoveryReward(3).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(281).setPrice(1).setUnlockOnDiscovery()
			}),
			
			OBSIDIAN_CHISELED.setPos(3,0).setUnlockPrice(1).setDiscoveryReward(3).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(282).setPrice(1).setUnlockOnDiscovery()
			}),
			
			OBSIDIAN_PILLAR.setPos(5,0).setUnlockPrice(1).setDiscoveryReward(3).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(283).setPrice(1).setUnlockOnDiscovery()
			}),
			
			OBSIDIAN_SMOOTH_GLOWING.setPos(1,2).setUnlockPrice(1).setDiscoveryReward(3).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(284).setPrice(1).setUnlockOnDiscovery()
			}),
			
			OBSIDIAN_CHISELED_GLOWING.setPos(3,2).setUnlockPrice(1).setDiscoveryReward(3).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(285).setPrice(1).setUnlockOnDiscovery()
			}),
			
			OBSIDIAN_PILLAR_GLOWING.setPos(5,2).setUnlockPrice(1).setDiscoveryReward(3).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(286).setPrice(1).setUnlockOnDiscovery()
			}),
			
			ENERGY.setPos(3,16).setUnlockPrice(35).setDiscoveryReward(25).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(890).setPrice(8),
				new KnowledgeFragmentText(891).setPrice(5).setUnlockRequirements(890),
				new KnowledgeFragmentText(892).setPrice(5).setUnlockRequirements(891),
				new KnowledgeFragmentText(893).setPrice(4).setUnlockRequirements(892),
				new KnowledgeFragmentText(894).setPrice(8).setUnlockRequirements(893),
				new KnowledgeFragmentText(895).setPrice(4).setUnlockRequirements(891),
				new KnowledgeFragmentText(289).setPrice(5).setUnlockRequirements(892),
				new KnowledgeFragmentText(896).setPrice(5).setUnlockRequirements(892),
				new KnowledgeFragmentText(897).setPrice(4).setUnlockRequirements(896),
				new KnowledgeFragmentText(898).setPrice(5).setUnlockRequirements(891),
				new KnowledgeFragmentText(899).setPrice(5).setUnlockRequirements(891),
			}),
			
			ENDIUM_ORE.setPos(0,12).setUnlockPrice(25).setDiscoveryReward(20).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(760).setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(761).setPrice(8).setUnlockOnDiscovery().setUnlockRequirements(760)
			}),
			
			ENDIUM_BLOCK.setPos(4,11).setUnlockPrice(5).setDiscoveryReward(5).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(770).setPrice(2).setUnlockOnDiscovery(),
				new KnowledgeFragmentCrafting(771).setRecipeFromRegistry(new ItemStack(BlockList.endium_block)).setPrice(2).setUnlockOnDiscovery().setUnlockRequirements(770).setUnlockCascade(781),
				new KnowledgeFragmentCrafting(772).setRecipeFromRegistry(new ItemStack(BlockList.void_chest)).setNonBuyable(), // 801
				new KnowledgeFragmentCrafting(773).setRecipeFromRegistry(new ItemStack(ItemList.blank_gem)).setNonBuyable(), // 1422
			}),
			
			VOID_CHEST.setPos(6,11).setUnlockPrice(20).setDiscoveryReward(8).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(800).setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentCrafting(801).setRecipeFromRegistry(new ItemStack(BlockList.void_chest)).setPrice(8).setUnlockRequirements(800).setUnlockCascade(772),
				new KnowledgeFragmentText(802).setPrice(3).setUnlockRequirements(800)
			}),
			
			SPATIAL_DASH_GEM.setPos(6,7).setUnlockPrice(25).setDiscoveryReward(15).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(290).setPrice(5).setUnlockOnDiscovery().setUnlockCascade(319),
				new KnowledgeFragmentText(291).setPrice(2).setUnlockRequirements(290),
				new KnowledgeFragmentText(292).setPrice(3).setUnlockRequirements(291),
				new KnowledgeFragmentText(293).setPrice(2).setUnlockRequirements(291),
				new KnowledgeFragmentText(294).setPrice(2).setUnlockRequirements(293),
				new KnowledgeFragmentCrafting(295).setRecipeFromRegistry(new ItemStack(ItemList.spatial_dash_gem)).setNonBuyable()
			}),
			
			ENDIUM_INGOT.setPos(2,12).setUnlockPrice(30).setDiscoveryReward(15).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(780).setPrice(2).setUnlockOnDiscovery(),
				new KnowledgeFragmentCrafting(781).setRecipeFromRegistry(new ItemStack(BlockList.endium_block)).setPrice(2).setUnlockOnDiscovery().setUnlockRequirements(780).setUnlockCascade(771),
				new KnowledgeFragmentCrafting(782).setRecipeFromRegistry(new ItemStack(ItemList.biome_compass)).setNonBuyable() // 303
			}),
			
			BIOME_COMPASS.setPos(4,13).setUnlockPrice(25).setDiscoveryReward(10).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(300).setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(301).setPrice(5).setUnlockOnDiscovery().setUnlockRequirements(300),
				new KnowledgeFragmentText(302).setPrice(3).setUnlockRequirements(301),
				new KnowledgeFragmentCrafting(303).setRecipeFromRegistry(new ItemStack(ItemList.biome_compass)).setPrice(8).setUnlockRequirements(300).setUnlockCascade(782)
			}),
			
			ENDER_EYE.setPos(3,7).setUnlockPrice(20).setDiscoveryReward(32).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(310).setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(311).setPrice(2).setUnlockOnDiscovery().setUnlockRequirements(310),
				new KnowledgeFragmentText(312).setPrice(3).setUnlockRequirements(311),
				new KnowledgeFragmentText(313).setPrice(2).setUnlockRequirements(310),
				new KnowledgeFragmentText(314).setPrice(3).setUnlockRequirements(313),
				new KnowledgeFragmentText(315).setPrice(2).setUnlockRequirements(310),
				new KnowledgeFragmentText(316).setPrice(2).setUnlockRequirements(315),
				new KnowledgeFragmentText(317).setPrice(2).setUnlockRequirements(315),
				new KnowledgeFragmentText(318).setPrice(2).setUnlockRequirements(315),
				new KnowledgeFragmentText(319).setPrice(3).setUnlockRequirements(310).setUnlockCascade(290)
			}),
			
			ANGRY_ENDERMAN_LINKED.setPos(0,7)
		});
		
		// ===
		
		KnowledgeCategories.METEOROIDS.addKnowledgeObjects(new KnowledgeObject[]{
			METEOROID.setCategoryObject(KnowledgeCategories.METEOROIDS).setUnlockPrice(40).setDiscoveryReward(8).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(320).setPrice(7)
			}),
			
			SPHALERITE.setPos(0,0).setUnlockPrice(12).setDiscoveryReward(6).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(330).setPrice(4).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(331).setPrice(4).setUnlockOnDiscovery().setUnlockRequirements(330)
			}),
			
			SPHALERITE_WITH_STARDUST.setPos(2,2).setUnlockPrice(12).setDiscoveryReward(8).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(340).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(341).setPrice(5).setUnlockRequirements(340),
				new KnowledgeFragmentText(342).setPrice(4).setUnlockRequirements(341)
			}),
			
			STARDUST.setPos(4,4).setUnlockPrice(20).setDiscoveryReward(8).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(350).setPrice(4).setUnlockOnDiscovery()
			}),
			
			DECOMPOSITION_TABLE.setPos(5,7).setUnlockPrice(12).setDiscoveryReward(6).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(880).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentCrafting(881).setRecipeFromRegistry(new ItemStack(BlockList.decomposition_table)).setPrice(10).setUnlockRequirements(880),
				new KnowledgeFragmentText(882).setPrice(4).setUnlockRequirements(880),
				new KnowledgeFragmentText(883).setPrice(5).setUnlockRequirements(880),
				new KnowledgeFragmentText(884).setPrice(4).setUnlockRequirements(883),
				new KnowledgeFragmentText(885).setPrice(4).setUnlockRequirements(882)
			}),
			
			EXPERIENCE_TABLE.setPos(7,7).setUnlockPrice(12).setDiscoveryReward(6).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(970).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentCrafting(971).setRecipeFromRegistry(new ItemStack(BlockList.experience_table)).setPrice(10).setUnlockRequirements(970),
				new KnowledgeFragmentText(972).setPrice(4).setUnlockRequirements(970),
				new KnowledgeFragmentText(973).setPrice(4).setUnlockRequirements(970)
			}),
			
			ACCUMULATION_TABLE.setPos(5,9).setUnlockPrice(18).setDiscoveryReward(8).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(980).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentCrafting(981).setRecipeFromRegistry(new ItemStack(BlockList.accumulation_table)).setPrice(10).setUnlockRequirements(980),
				new KnowledgeFragmentText(982).setPrice(4).setUnlockRequirements(980),
				new KnowledgeFragmentText(983).setPrice(4).setUnlockRequirements(980)
			}),
			
			EXTRACTION_TABLE.setPos(7,9).setUnlockPrice(18).setDiscoveryReward(8).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(990).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentCrafting(991).setRecipeFromRegistry(new ItemStack(BlockList.extraction_table)).setPrice(10).setUnlockRequirements(990),
				new KnowledgeFragmentText(992).setPrice(5).setUnlockRequirements(990),
				new KnowledgeFragmentText(993).setPrice(5).setUnlockRequirements(990),
				new KnowledgeFragmentText(994).setPrice(4).setUnlockRequirements(993),
				new KnowledgeFragmentText(995).setPrice(4).setUnlockRequirements(990)
			})
		});
		
		// ===
		
		KnowledgeCategories.BIOME_ISLANDS.addKnowledgeObjects(new KnowledgeObject[]{	
			BIOME_ISLANDS.setCategoryObject(KnowledgeCategories.BIOME_ISLANDS).setUnlockPrice(50).setDiscoveryReward(20).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(720).setPrice(7),
				new KnowledgeFragmentText(721).setPrice(4).setUnlockRequirements(720),
				new KnowledgeFragmentText(722).setPrice(4).setUnlockRequirements(721),
				new KnowledgeFragmentText(723).setPrice(4).setUnlockRequirements(721)
			}),
			
			INSTABILITY_ORB_ORE.setPos(0,3).setUnlockPrice(15).setDiscoveryReward(8).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(360).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(361).setPrice(4).setUnlockRequirements(360),
				new KnowledgeFragmentText(362).setPrice(4).setUnlockRequirements(360),
				new KnowledgeFragmentText(363).setPrice(5).setUnlockRequirements(362)
			}),
			
			STARDUST_ORE.setPos(0,0).setUnlockPrice(15).setDiscoveryReward(10).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(370).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(371).setPrice(4).setUnlockRequirements(370),
				new KnowledgeFragmentText(372).setPrice(4).setUnlockRequirements(371),
				new KnowledgeFragmentText(373).setPrice(5).setUnlockRequirements(372)
			}),
			
			INSTABILITY_ORB.setPos(2,3).setUnlockPrice(20).setDiscoveryReward(12).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(380).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(381).setPrice(7).setUnlockRequirements(380),
				new KnowledgeFragmentText(382).setPrice(5).setUnlockRequirements(381),
				new KnowledgeFragmentText(383).setNonBuyable() // 741
			}),
			
			POTION_OF_INSTABILITY.setPos(4,3).setUnlockPrice(15).setDiscoveryReward(8).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(740).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentItemConversion(741).setItems(new ItemStack(ItemList.instability_orb),new ItemStack(ItemList.potion_of_instability)).setPrice(3).setUnlockRequirements(740).setUnlockCascade(134,383)
			}),
			
			STARDUST_LINKED.setPos(2,0)
		});
		
		// ===
		
		KnowledgeCategories.BIOME_ISLAND_FOREST.addKnowledgeObjects(new KnowledgeObject[]{
			INFESTED_FOREST_BIOME.setCategoryObject(KnowledgeCategories.BIOME_ISLAND_FOREST).setUnlockPrice(60).setDiscoveryReward(45).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(390).setPrice(7),
				new KnowledgeFragmentText(391).setPrice(4).setUnlockRequirements(390),
				new KnowledgeFragmentText(392).setPrice(4).setUnlockRequirements(391),
				new KnowledgeFragmentText(393).setPrice(15).setUnlockRequirements(391),
				new KnowledgeFragmentText(394).setPrice(15).setUnlockRequirements(391),
				// 395 reseved for third variation
				new KnowledgeFragmentText(396).setPrice(10).setUnlockRequirements(390),
				new KnowledgeFragmentText(397).setPrice(5).setUnlockRequirements(396)
			}),
			
			INFESTED_END_STONE.setPos(0,0).setUnlockPrice(7).setDiscoveryReward(8).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(400).setPrice(2).setUnlockOnDiscovery()
			}),
			
			INFESTED_GRASS.setPos(2,0).setUnlockPrice(2).setDiscoveryReward(3).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(410).setPrice(2).setUnlockOnDiscovery()
			}),
			
			INFESTED_TALL_GRASS.setPos(4,0).setUnlockPrice(2).setDiscoveryReward(3).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(411).setPrice(2).setUnlockOnDiscovery()
			}),
			
			INFESTED_FERN.setPos(6,0).setUnlockPrice(2).setDiscoveryReward(3).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(412).setPrice(2).setUnlockOnDiscovery()
			}),
			
			INFESTATION_REMEDY.setPos(4,2).setUnlockPrice(12).setDiscoveryReward(10).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(730).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentItemConversion(731).setItems(new ItemStack(ItemList.silverfish_blood),new ItemStack(ItemList.infestation_remedy)).setPrice(2).setUnlockRequirements(730).setUnlockCascade(135),
			}),
			
			INFESTED_BAT.setPos(2,2).setUnlockPrice(8).setDiscoveryReward(5).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(480).setPrice(4).setUnlockOnDiscovery()
			}),
			
			SILVERFISH_LINKED.setPos(0,2),
			
			// =
			
			THORNY_BUSH.setPos(0,7).setUnlockPrice(8).setDiscoveryReward(5).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(420).setPrice(4).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(421).setPrice(4).setUnlockRequirements(420)
			}),
			
			SPOOKY_LOG.setPos(2,8).setUnlockPrice(25).setDiscoveryReward(22).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(430).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(431).setPrice(4).setUnlockRequirements(430),
				new KnowledgeFragmentText(432).setPrice(7).setUnlockRequirements(430).setUnlockCascade(451),
				new KnowledgeFragmentText(433).setPrice(5).setUnlockRequirements(430),
				new KnowledgeFragmentText(434).setPrice(4).setUnlockRequirements(433),
				new KnowledgeFragmentText(435).setPrice(4).setUnlockRequirements(433),
				new KnowledgeFragmentText(436).setPrice(10).setUnlockRequirements(435).setUnlockCascade(461),
				new KnowledgeFragmentCrafting(437).setRecipeFromRegistry(new ItemStack(BlockList.spooky_log)).setPrice(5).setUnlockRequirements(430).setUnlockCascade(452)
			}),
			
			SPOOKY_LEAVES.setPos(2,6).setUnlockPrice(12).setDiscoveryReward(8).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(440).setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(441).setPrice(4).setUnlockRequirements(440),
				new KnowledgeFragmentCrafting(442).setRecipeFromRegistry(new ItemStack(BlockList.spooky_leaves)).setPrice(5).setUnlockRequirements(440).setUnlockCascade(453)
			}),
			
			DRY_SPLINTER.setPos(4,7).setUnlockPrice(15).setDiscoveryReward(5).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(450).setPrice(2).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(451).setPrice(3).setUnlockRequirements(450).setUnlockCascade(432),
				new KnowledgeFragmentCrafting(452).setRecipeFromRegistry(new ItemStack(BlockList.spooky_log)).setPrice(5).setUnlockRequirements(450).setUnlockCascade(437),
				new KnowledgeFragmentCrafting(453).setRecipeFromRegistry(new ItemStack(BlockList.spooky_leaves)).setPrice(5).setUnlockRequirements(450).setUnlockCascade(442)
			}),
			
			GHOST_AMULET.setPos(7,7).setUnlockPrice(24).setDiscoveryReward(8).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(460).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(461).setPrice(10).setUnlockRequirements(460).setUnlockCascade(436),
				new KnowledgeFragmentText(462).setPrice(10).setUnlockRequirements(460),
				new KnowledgeFragmentText(463).setPrice(7).setUnlockRequirements(462).setUnlockCascade(117)
			}),
			
			ECTOPLASM.setPos(9,7).setUnlockPrice(35).setDiscoveryReward(15).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(470).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentCrafting(471).setRecipeFromRegistry(new ItemStack(ItemList.spectral_tear)).setNonBuyable() // 921
			}),
			
			SPECTRAL_TEAR.setPos(9,9).setUnlockPrice(10).setDiscoveryReward(6).addFragments(new KnowledgeFragment[]{
				 new KnowledgeFragmentText(920).setPrice(5).setUnlockOnDiscovery(),
				 new KnowledgeFragmentCrafting(921).setRecipeFromRegistry(new ItemStack(ItemList.spectral_tear)).setPrice(7).setUnlockRequirements(920).setUnlockCascade(471),
				 new KnowledgeFragmentCrafting(922).setRecipeFromRegistry(new ItemStack(ItemList.living_matter)).setNonBuyable() // 931
			}),
			
			LIVING_MATTER.setPos(11,9).setUnlockPrice(20).setDiscoveryReward(8).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(930).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentCrafting(931).setRecipeFromRegistry(new ItemStack(ItemList.living_matter)).setPrice(10).setUnlockRequirements(930).setUnlockCascade(922)
			}),
			
			CURSE.setPos(13,9).setUnlockPrice(40).setDiscoveryReward(15).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(940).setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(941).setPrice(7).setUnlockRequirements(940),
				new KnowledgeFragmentText(942).setPrice(5).setUnlockRequirements(941),
				new KnowledgeFragmentText(943).setPrice(5).setUnlockRequirements(941),
				new KnowledgeFragmentText(944).setPrice(4).setUnlockRequirements(943),
				new KnowledgeFragmentText(945).setPrice(7).setUnlockRequirements(941)
			}).addFragments(KnowledgeUtils.createCurseFragments(1300,941)),
			
			POTION_OF_PURITY.setPos(15,8).setUnlockPrice(15).setDiscoveryReward(10).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(950).setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(951).setPrice(5).setUnlockRequirements(950).setUnlockCascade(136)
			}),
			
			CURSE_AMULET.setPos(15,10).setUnlockPrice(10).setDiscoveryReward(12).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(1400).setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(1401).setPrice(4).setUnlockRequirements(1400)
			}),
			
			// =
			
			RAVAGED_DUNGEON.setPos(4,13).setUnlockPrice(30).setDiscoveryReward(20).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(490).setPrice(7),
				new KnowledgeFragmentText(491).setPrice(5).setUnlockRequirements(490),
				new KnowledgeFragmentText(492).setPrice(4).setUnlockRequirements(491),
				new KnowledgeFragmentText(493).setPrice(5).setUnlockRequirements(492),
				new KnowledgeFragmentText(494).setPrice(4).setUnlockRequirements(492)
			}),
			
			RAVAGED_BRICK.setPos(4,15).setUnlockPrice(10).setDiscoveryReward(8).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(500).setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(501).setPrice(4).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(505).setPrice(7).setUnlockRequirements(500),
				new KnowledgeFragmentCrafting(502).setRecipeFromRegistry(new ItemStack(BlockList.ravaged_brick_stairs,4)).setPrice(4).setUnlockRequirements(500).setUnlockCascade(513),
				new KnowledgeFragmentCrafting(503).setRecipeFromRegistry(new ItemStack(BlockList.ravaged_brick_slab,6)).setPrice(4).setUnlockRequirements(500).setUnlockCascade(515),
				new KnowledgeFragmentCrafting(504).setRecipeFromRegistry(new ItemStack(BlockList.ravaged_brick_fence,6)).setPrice(4).setUnlockRequirements(500).setUnlockCascade(517)
			}),
			
			RAVAGED_BRICK_GLOWING.setPos(6,15).setUnlockPrice(2).setDiscoveryReward(5).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(510).setPrice(5).setUnlockOnDiscovery()
			}),
			
			RAVAGED_BRICK_STAIRS.setPos(2,15).setUnlockPrice(2).setDiscoveryReward(5).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(512).setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentCrafting(513).setRecipeFromRegistry(new ItemStack(BlockList.ravaged_brick_stairs,4)).setPrice(4).setUnlockRequirements(512).setUnlockCascade(502)
			}),
			
			RAVAGED_BRICK_SLAB.setPos(0,15).setUnlockPrice(2).setDiscoveryReward(5).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(514).setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentCrafting(515).setRecipeFromRegistry(new ItemStack(BlockList.ravaged_brick_slab,6)).setPrice(4).setUnlockRequirements(514).setUnlockCascade(503)
			}),
			
			RAVAGED_BRICK_FENCE.setPos(8,15).setUnlockPrice(2).setDiscoveryReward(5).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(516).setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentCrafting(517).setRecipeFromRegistry(new ItemStack(BlockList.ravaged_brick_fence,6)).setPrice(4).setUnlockRequirements(516).setUnlockCascade(504)
			}),
			
			CHARM_POUCH.setPos(7,18).setUnlockPrice(40).setDiscoveryReward(20).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(520).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentCrafting(521).setRecipeFromRegistry(new ItemStack(ItemList.charm_pouch)).setPrice(10).setUnlockRequirements(520).setUnlockCascade(533),
				new KnowledgeFragmentText(522).setPrice(5).setUnlockRequirements(520)
			}),
			
			RUNES.setPos(3,18).setUnlockPrice(25).setDiscoveryReward(18).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(530).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(531).setPrice(5).setUnlockRequirements(530),
				new KnowledgeFragmentText(532).setPrice(5).setUnlockRequirements(530),
				new KnowledgeFragmentCrafting(533).setRecipeFromRegistry(new ItemStack(ItemList.charm_pouch)).setNonBuyable() // 521
			}),
			
			CHARMS.setPos(5,18).setUnlockPrice(35).setDiscoveryReward(25).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(540).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(541).setPrice(7).setUnlockRequirements(540)
			}).addFragments(KnowledgeUtils.createCharmFragments(1000,541)),
			
			LOUSE.setPos(1,18).setUnlockPrice(15).setDiscoveryReward(15).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(550).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(551).setPrice(5).setUnlockRequirements(550),
				new KnowledgeFragmentText(552).setPrice(7).setUnlockRequirements(551),
				new KnowledgeFragmentText(553).setPrice(5).setUnlockRequirements(552)
			})
		});
		
		KnowledgeCategories.BIOME_ISLAND_MOUNTAINS.addKnowledgeObjects(new KnowledgeObject[]{
			BURNING_MOUNTAINS_BIOME.setCategoryObject(KnowledgeCategories.BIOME_ISLAND_MOUNTAINS).setUnlockPrice(60).setDiscoveryReward(45).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(560).setPrice(7),
				new KnowledgeFragmentText(561).setPrice(4).setUnlockRequirements(560),
				new KnowledgeFragmentText(562).setPrice(15).setUnlockRequirements(561),
				new KnowledgeFragmentText(563).setPrice(15).setUnlockRequirements(561)
			}),
			
			BURNED_END_STONE.setPos(0,0).setUnlockPrice(5).setDiscoveryReward(8).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(570).setPrice(2).setUnlockOnDiscovery()
			}),
			
			LILYFIRE.setPos(4,0).setUnlockPrice(4).setDiscoveryReward(5).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(580).setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentCrafting(581).setCustomRecipe(new ItemStack(Items.dye,2,14),new ItemStack[]{ new ItemStack(BlockList.crossed_decoration,1,BlockCrossedDecoration.dataLilyFire) }).setPrice(4).setUnlockRequirements(580)
			}),
			
			IGNEOUS_ROCK_ORE_LINKED.setPos(2,0),
			
			IGNEOUS_ROCK_LINKED.setPos(2,2),
			
			// =
			
			CINDER.setPos(0,7).setUnlockPrice(5).setDiscoveryReward(5).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(590).setPrice(5).setUnlockOnDiscovery()
			}),
			
			DUNGEON_PUZZLE.setPos(10,7).setUnlockPrice(15).setDiscoveryReward(25).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(900).setPrice(10).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(901).setPrice(7).setUnlockRequirements(900),
				new KnowledgeFragmentText(902).setPrice(7).setUnlockRequirements(901),
				new KnowledgeFragmentText(903).setPrice(10).setUnlockRequirements(901)
			}),
			
			FIERY_ESSENCE_ALTAR.setPos(5,9).setUnlockPrice(25).setDiscoveryReward(20).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(600).setPrice(10).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(601).setPrice(8).setUnlockRequirements(600)
			}),
			
			FIERY_ESSENCE.setPos(5,7),
			
			FIRE_GOLEM.setPos(2,6).setUnlockPrice(20).setDiscoveryReward(15).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(610).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(611).setPrice(4).setUnlockRequirements(610),
				new KnowledgeFragmentText(612).setPrice(5).setUnlockRequirements(611),
				new KnowledgeFragmentText(613).setPrice(5).setUnlockRequirements(611),
				new KnowledgeFragmentText(614).setPrice(5).setUnlockRequirements(610).setUnlockCascade(82)
			}),
			
			SCORCHING_LENS.setPos(2,8).setUnlockPrice(20).setDiscoveryReward(12).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(620).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(621).setPrice(4).setUnlockRequirements(620),
				new KnowledgeFragmentText(622).setPrice(5).setUnlockRequirements(621),
				new KnowledgeFragmentText(623).setPrice(5).setUnlockRequirements(620).setUnlockCascade(82)
			}),
			
			FIRE_FIEND.setPos(8,7).setUnlockPrice(30).setDiscoveryReward(18).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(910).setPrice(10).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(911).setPrice(5).setUnlockRequirements(910),
				new KnowledgeFragmentText(912).setPrice(4).setUnlockRequirements(910),
				new KnowledgeFragmentText(913).setPrice(5).setUnlockRequirements(912),
				new KnowledgeFragmentText(914).setPrice(5).setUnlockRequirements(912),
				new KnowledgeFragmentText(915).setPrice(7).setUnlockRequirements(912),
				new KnowledgeFragmentText(916).setPrice(7).setUnlockRequirements(915),
				new KnowledgeFragmentText(917).setPrice(5).setUnlockRequirements(910)
			}),
			
			// =
			
			INFERNIUM.setPos(2,13).setUnlockPrice(15).setDiscoveryReward(12).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(630).setPrice(7).setUnlockOnDiscovery().setUnlockCascade(657),
				new KnowledgeFragmentCrafting(631).setRecipeFromRegistry(new ItemStack(ItemList.scorching_pickaxe)).setNonBuyable() // 643
			}),
			
			SCORCHING_PICKAXE.setPos(4,13).setUnlockPrice(40).setDiscoveryReward(15).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(640).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(641).setPrice(7).setUnlockRequirements(640),
				new KnowledgeFragmentText(642).setPrice(5).setUnlockRequirements(641),
				new KnowledgeFragmentCrafting(643).setRecipeFromRegistry(new ItemStack(ItemList.scorching_pickaxe)).setPrice(10).setUnlockRequirements(640).setUnlockCascade(631)
			}),
			
			HAUNTED_MINER.setPos(0,13).setUnlockPrice(25).setDiscoveryReward(15).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(650).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(651).setPrice(7).setUnlockRequirements(651),
				new KnowledgeFragmentText(652).setPrice(4).setUnlockRequirements(650),
				new KnowledgeFragmentText(653).setPrice(4).setUnlockRequirements(650),
				new KnowledgeFragmentText(654).setPrice(4).setUnlockRequirements(653),
				new KnowledgeFragmentText(655).setPrice(4).setUnlockRequirements(653),
				new KnowledgeFragmentText(656).setPrice(4).setUnlockRequirements(653),
				new KnowledgeFragmentText(657).setPrice(5).setUnlockRequirements(650).setUnlockCascade(630)
			})
		});
		
		KnowledgeCategories.BIOME_ISLAND_ENCHISLAND.addKnowledgeObjects(new KnowledgeObject[]{
			ENCHANTED_ISLAND_BIOME.setCategoryObject(KnowledgeCategories.BIOME_ISLAND_ENCHISLAND).setUnlockPrice(60).setDiscoveryReward(45).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(660).setPrice(5),
				new KnowledgeFragmentText(661).setPrice(4).setUnlockRequirements(660),
				new KnowledgeFragmentText(662).setPrice(15).setUnlockRequirements(661),
				new KnowledgeFragmentText(663).setPrice(15).setUnlockRequirements(661),
				new KnowledgeFragmentText(665).setPrice(7).setUnlockRequirements(661).setUnlockCascade(810)
			}),
			
			ENCHANTED_END_STONE.setPos(0,0).setUnlockPrice(5).setDiscoveryReward(8).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(670).setPrice(2).setUnlockOnDiscovery()
			}),
			
			FALLING_OBSIDIAN_LINKED.setPos(2,0),
			
			PERSEGRIT.setPos(4,0).setUnlockPrice(7).setDiscoveryReward(15).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(810).setPrice(5).setUnlockOnDiscovery().setUnlockCascade(665),
				new KnowledgeFragmentText(811).setPrice(5).setUnlockRequirements(810),
				new KnowledgeFragmentText(812).setPrice(2).setUnlockRequirements(810)
			}),
			
			ENDERMAN_LINKED.setPos(0,2),
			
			// =
			
			ARCANE_SHARD.setPos(5,6).setUnlockPrice(28).setDiscoveryReward(27).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(1420).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(1421).setPrice(2),
				new KnowledgeFragmentCrafting(1422).setRecipeFromRegistry(new ItemStack(ItemList.blank_gem)).setPrice(5).setUnlockCascade(773),
				new KnowledgeFragmentCrafting(1423).setRecipeFromRegistry(new ItemStack(ItemList.spatial_dash_gem)).setPrice(7).setUnlockCascade(295),
				new KnowledgeFragmentCrafting(1424).setRecipeFromRegistry(new ItemStack(ItemList.transference_gem)).setPrice(7)
			}),
			
			BLANK_GEM.setPos(7,6).setUnlockPrice(15).setDiscoveryReward(5).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(1430).setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentCrafting(1431).setRecipeFromRegistry(new ItemStack(ItemList.blank_gem)).setNonBuyable(),
			}),
			
			SPATIAL_DASH_GEM_LINKED.setPos(9,6),
			
			TRANSFERENCE_GEM.setPos(11,6).setUnlockPrice(20).setDiscoveryReward(10).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(1440).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentCrafting(1441).setRecipeFromRegistry(new ItemStack(ItemList.transference_gem)).setNonBuyable(),
				new KnowledgeFragmentText(1442).setPrice(5).setUnlockRequirements(1440),
				new KnowledgeFragmentText(1443).setPrice(5).setUnlockRequirements(1442),
				new KnowledgeFragmentText(1444).setPrice(4).setUnlockRequirements(1443),
				new KnowledgeFragmentText(1445).setPrice(4).setUnlockRequirements(1444)
			}),
			
			BABY_ENDERMAN.setPos(0,6).setUnlockPrice(25).setDiscoveryReward(8).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(680).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(681).setPrice(4).setUnlockRequirements(680),
				new KnowledgeFragmentText(682).setPrice(4).setUnlockRequirements(680),
				new KnowledgeFragmentText(683).setPrice(5).setUnlockRequirements(682),
				new KnowledgeFragmentText(684).setPrice(5).setUnlockRequirements(682)
			}),
			
			HOMELAND_ENDERMAN.setPos(2,6).setUnlockPrice(25).setDiscoveryReward(15).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(790).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(791).setPrice(5).setUnlockRequirements(790),
				new KnowledgeFragmentText(792).setPrice(5).setUnlockRequirements(791),
				new KnowledgeFragmentText(793).setPrice(5).setUnlockRequirements(791)
			}),
			
			// =
			
			LABORATORY.setPos(3,10).setUnlockPrice(30).setDiscoveryReward(20).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(1460).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(1461).setPrice(5).setUnlockRequirements(1460),
				new KnowledgeFragmentText(1462).setPrice(5).setUnlockRequirements(1461),
				new KnowledgeFragmentText(1463).setPrice(5).setUnlockRequirements(1462)
			}),
			
			LABORATORY_OBSIDIAN.setPos(0,12).setUnlockPrice(5).setDiscoveryReward(10).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(1450).setPrice(4).setUnlockOnDiscovery()
			}),
			
			LABORATORY_GLASS.setPos(2,12).setUnlockPrice(5).setDiscoveryReward(10).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(1451).setPrice(4).setUnlockOnDiscovery()
			}),
			
			LABORATORY_FLOOR.setPos(4,12).setUnlockPrice(5).setDiscoveryReward(10).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(1452).setPrice(4).setUnlockOnDiscovery()
			}),
			
			LABORATORY_STAIRS.setPos(6,12).setUnlockPrice(5).setDiscoveryReward(5).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(1453).setPrice(4).setUnlockOnDiscovery()
			}),
			
			OBSIDIAN_FRAGMENT.setPos(2,15).setUnlockPrice(15).setDiscoveryReward(10).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(820).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentCrafting(821).setRecipeFromRegistry(new ItemStack(ItemList.obsidian_rod)).setNonBuyable(), // 831
				new KnowledgeFragmentCrafting(822).setRecipeFromRegistry(new ItemStack(Blocks.obsidian)).setPrice(5).setUnlockRequirements(820),
				new KnowledgeFragmentCrafting(823).setRecipeFromRegistry(new ItemStack(BlockList.obsidian_special,1,0)).setPrice(4).setUnlockRequirements(822),
				new KnowledgeFragmentCrafting(824).setRecipeFromRegistry(new ItemStack(BlockList.obsidian_special,1,1)).setPrice(4).setUnlockRequirements(822),
				new KnowledgeFragmentCrafting(825).setRecipeFromRegistry(new ItemStack(BlockList.obsidian_special,1,2)).setPrice(4).setUnlockRequirements(822),
			}),
			
			OBSIDIAN_ROD.setPos(4,15).setUnlockPrice(7).setDiscoveryReward(5).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(830).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentCrafting(831).setRecipeFromRegistry(new ItemStack(ItemList.obsidian_rod)).setPrice(5).setUnlockRequirements(830).setUnlockCascade(821),
				new KnowledgeFragmentCrafting(832).setRecipeFromRegistry(new ItemStack(ItemList.energy_wand)).setNonBuyable() // 851
			}),
			
			AURICION.setPos(12,15).setUnlockPrice(7).setDiscoveryReward(12).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(870).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(871).setPrice(5).setUnlockRequirements(870),
				new KnowledgeFragmentCrafting(872).setRecipeFromRegistry(new ItemStack(ItemList.energy_wand_core)).setPrice(7).setUnlockRequirements(870).setUnlockCascade(841)
			}),
			
			ENERGY_WAND_CORE.setPos(10,15).setUnlockPrice(7).setDiscoveryReward(5).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(840).setPrice(5).setUnlockOnDiscovery(),
				new KnowledgeFragmentCrafting(841).setRecipeFromRegistry(new ItemStack(ItemList.energy_wand_core)).setPrice(7).setUnlockRequirements(840).setUnlockCascade(872),
				new KnowledgeFragmentCrafting(842).setRecipeFromRegistry(new ItemStack(ItemList.energy_wand)).setNonBuyable() // 851
			}),
			
			ENERGY_WAND.setPos(7,15).setUnlockPrice(35).setDiscoveryReward(8).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(850).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentCrafting(851).setRecipeFromRegistry(new ItemStack(ItemList.energy_wand)).setPrice(10).setUnlockRequirements(850).setUnlockCascade(832,842),
				new KnowledgeFragmentText(852).setPrice(7).setUnlockRequirements(850),
				new KnowledgeFragmentText(853).setPrice(5).setUnlockRequirements(852)
			}),
			
			ENDER_GUARDIAN.setPos(0,15).setUnlockPrice(20).setDiscoveryReward(15).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(690).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(691).setPrice(5).setUnlockRequirements(690),
				new KnowledgeFragmentText(692).setPrice(7).setUnlockRequirements(690),
				new KnowledgeFragmentText(694).setPrice(5).setUnlockRequirements(690),
				new KnowledgeFragmentText(693).setPrice(5).setUnlockRequirements(690)
			}),
			
			ENDERMAGE.setPos(14,15).setUnlockPrice(20).setDiscoveryReward(15).addFragments(new KnowledgeFragment[]{
				new KnowledgeFragmentText(860).setPrice(7).setUnlockOnDiscovery(),
				new KnowledgeFragmentText(861).setPrice(5).setUnlockRequirements(860),
				new KnowledgeFragmentText(862).setPrice(7).setUnlockRequirements(860),
				new KnowledgeFragmentText(863).setPrice(5).setUnlockRequirements(860),
				new KnowledgeFragmentText(864).setPrice(4).setUnlockRequirements(863)
			})
		});
		
		// next: 1500
		// taken: 1000-1399
		
		Stopwatch.finish("KnowledgeRegistrations");
		
		if (Log.isDebugEnabled()){
			Stopwatch.time("KnowledgeRegistrations - Stats");
			
			for(KnowledgeCategory category:KnowledgeCategories.categoryList){
				if (category.getCategoryObject() == null)throw new IllegalStateException("Knowledge Category "+category.getTooltip()+" has null category object!");
			}
			
			int amtObjects = 0, amtFragments = 0, totalObjPrice = 0, totalFragPrice = 0, totalFragPriceExcCascaded = 0, totalFragPriceExcDiscovery = 0, totalReward = 0;
			TIntHashSet cascaded = new TIntHashSet();
			
			for(KnowledgeObject<?> obj:KnowledgeObject.getAllObjects()){
				if (obj == HELP)continue;
				else if (obj.getDiscoveryReward() == 0 || obj.getUnlockPrice() == 0)throw new IllegalStateException("Knowledge Object "+obj.globalID+"/"+obj.getTooltip()+" has illegal reward ("+obj.getDiscoveryReward()+") or unlock price ("+obj.getUnlockPrice()+").");
				
				for(KnowledgeFragment fragment:obj.getFragments()){
					if (fragment.getPrice() == 0)throw new IllegalStateException("Knowledge Fragment "+fragment.globalID+" has illegal unlock price.");
					else if (!fragment.isBuyable())continue;
					
					totalFragPrice += fragment.getPrice();
					if (!fragment.isUnlockedOnDiscovery())totalFragPriceExcDiscovery += fragment.getPrice();
					if (!cascaded.contains(fragment.globalID))totalFragPriceExcCascaded += fragment.getPrice();
					
					for(int id:fragment.getUnlockRequirements()){
						if (KnowledgeFragment.getById(id) == null)throw new IllegalStateException("Knowledge Fragment "+fragment.globalID+" has invalid unlock requirement ID "+id+".");
					}

					for(int id:fragment.getUnlockCascade()){
						if (KnowledgeFragment.getById(id) == null)throw new IllegalStateException("Knowledge Fragment "+fragment.globalID+" has invalid unlock cascade ID "+id+".");
						cascaded.add(id);
					}
				}
				
				++amtObjects;
				amtFragments += obj.getFragments().size();
				totalObjPrice += obj.getUnlockPrice();
				totalReward += obj.getDiscoveryReward();
			}
			
			for(IEnhancementEnum enhancement:EnhancementHandler.getAllEnhancements()){
				if (KnowledgeFragmentEnhancement.getEnhancementFragment(enhancement) == null)throw new IllegalStateException("Enhancement is missing a fragment: "+enhancement);
			}
			
			Log.debug("Knowledge Object amount: $0",amtObjects);
			Log.debug("Knowledge Fragment amount: $0",amtFragments);
			Log.debug("Total Object price: $0",totalObjPrice);
			Log.debug("Total Fragment price: $0",totalFragPrice);
			Log.debug("Total Fragment price, excluding cascaded: $0",totalFragPriceExcCascaded);
			Log.debug("Total Fragment price, excluding discovery: $0",totalFragPriceExcDiscovery);
			Log.debug("Total Fragment price, excluding all: $0",totalFragPriceExcCascaded-(totalFragPrice-totalFragPriceExcDiscovery));
			Log.debug("Total real price: $0",totalObjPrice+totalFragPriceExcCascaded-(totalFragPrice-totalFragPriceExcDiscovery));
			Log.debug("Total discovery reward: $0",totalReward);
			
			Stopwatch.finish("KnowledgeRegistrations - Stats");
		}
	}
	
	public static KnowledgeObject<ObjectBlock> create(Block block){
		return new KnowledgeObject<ObjectBlock>(new ObjectBlock(block));
	}
	
	public static KnowledgeObject<ObjectBlock> create(Block block, String tooltip){
		return new KnowledgeObject<ObjectBlock>(new ObjectBlock(block),tooltip);
	}
	
	public static KnowledgeObject<ObjectBlock> create(Block block, int metadata){
		return new KnowledgeObject<ObjectBlock>(new ObjectBlock(block,metadata));
	}
	
	public static KnowledgeObject<ObjectItem> create(Item item){
		return new KnowledgeObject<ObjectItem>(new ObjectItem(item));
	}
	
	public static KnowledgeObject<ObjectItem> create(Item item, String tooltip){
		return new KnowledgeObject<ObjectItem>(new ObjectItem(item),tooltip);
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
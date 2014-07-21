package chylex.hee;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidContainerRegistry;
import chylex.hee.block.BlockDragonEggCustom;
import chylex.hee.block.BlockEnderGoo;
import chylex.hee.block.BlockList;
import chylex.hee.block.BlockReplaceHelper;
import chylex.hee.entity.block.EntityBlockEnderCrystal;
import chylex.hee.entity.block.EntityBlockEnhancedTNTPrimed;
import chylex.hee.entity.block.EntityBlockFallingDragonEgg;
import chylex.hee.entity.block.EntityBlockFallingObsidian;
import chylex.hee.entity.block.EntityBlockTempleDragonEgg;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.EntityBossEnderDemon;
import chylex.hee.entity.boss.EntityMiniBossEnderEye;
import chylex.hee.entity.boss.EntityMiniBossFireFiend;
import chylex.hee.entity.item.EntityItemAltar;
import chylex.hee.entity.item.EntityItemEndPowder;
import chylex.hee.entity.item.EntityItemIgneousRock;
import chylex.hee.entity.item.EntityItemInstabilityOrb;
import chylex.hee.entity.mob.EntityMobAngryEnderman;
import chylex.hee.entity.mob.EntityMobBabyEnderman;
import chylex.hee.entity.mob.EntityMobCorporealMirage;
import chylex.hee.entity.mob.EntityMobEnderGuardian;
import chylex.hee.entity.mob.EntityMobFireGolem;
import chylex.hee.entity.mob.EntityMobForestGhost;
import chylex.hee.entity.mob.EntityMobInfestedBat;
import chylex.hee.entity.mob.EntityMobLouse;
import chylex.hee.entity.mob.EntityMobParalyzedEnderman;
import chylex.hee.entity.mob.EntityMobScorchingLens;
import chylex.hee.entity.mob.EntityMobVampiricBat;
import chylex.hee.entity.projectile.EntityProjectileCorporealMirageOrb;
import chylex.hee.entity.projectile.EntityProjectileDragonFireball;
import chylex.hee.entity.projectile.EntityProjectileDragonFreezeball;
import chylex.hee.entity.projectile.EntityProjectileEnhancedEnderPearl;
import chylex.hee.entity.projectile.EntityProjectileFlamingBall;
import chylex.hee.entity.projectile.EntityProjectileGolemFireball;
import chylex.hee.entity.projectile.EntityProjectilePotionOfInstability;
import chylex.hee.entity.projectile.EntityProjectileSpatialDash;
import chylex.hee.entity.weather.EntityWeatherLightningBoltDemon;
import chylex.hee.entity.weather.EntityWeatherLightningBoltSafe;
import chylex.hee.gui.GuiHandler;
import chylex.hee.item.ItemList;
import chylex.hee.item.block.ItemBlockEndFlower;
import chylex.hee.item.block.ItemBlockEnhancedTNT;
import chylex.hee.item.block.ItemBlockEssenceAltar;
import chylex.hee.item.block.ItemBlockSoulCharm;
import chylex.hee.item.block.ItemBlockWithSubtypes;
import chylex.hee.mechanics.MiscEvents;
import chylex.hee.mechanics.charms.CharmPouchHandler;
import chylex.hee.mechanics.infestation.InfestationEvents;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.mechanics.orb.OrbAcquirableItems;
import chylex.hee.mechanics.orb.OrbSpawnableMobs;
import chylex.hee.mechanics.temple.TempleEvents;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.proxy.FXCommonProxy;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.recipes.RecipeList;
import chylex.hee.system.ConfigHandler;
import chylex.hee.system.ReflectionPublicizer;
import chylex.hee.system.achievements.AchievementEvents;
import chylex.hee.system.achievements.AchievementManager;
import chylex.hee.system.commands.HeeAdminCommand;
import chylex.hee.system.commands.HeeDebugCommand;
import chylex.hee.system.creativetab.ModCreativeTab;
import chylex.hee.system.integration.ModIntegrationManager;
import chylex.hee.system.util.GameRegistryUtil;
import chylex.hee.tileentity.TileEntityCustomSpawner;
import chylex.hee.tileentity.TileEntityDecompositionTable;
import chylex.hee.tileentity.TileEntityEndermanHead;
import chylex.hee.tileentity.TileEntityEnergyCluster;
import chylex.hee.tileentity.TileEntityEnergyExtractionTable;
import chylex.hee.tileentity.TileEntityEnhancedBrewingStand;
import chylex.hee.tileentity.TileEntityEnhancedTNT;
import chylex.hee.tileentity.TileEntityEssenceAltar;
import chylex.hee.tileentity.TileEntityLaserBeam;
import chylex.hee.tileentity.TileEntitySoulCharm;
import chylex.hee.world.DimensionOverride;
import chylex.hee.world.loot.WorldLoot;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = "HardcoreEnderExpansion", name = "Hardcore Ender Expansion", version = "", useMetadata = true, guiFactory = "chylex.hee.gui.ModGuiFactory")
public class HardcoreEnderExpansion{
	@Instance("HardcoreEnderExpansion")
	public static HardcoreEnderExpansion instance;

	@SidedProxy(clientSide = "chylex.hee.proxy.ModClientProxy", serverSide = "chylex.hee.proxy.ModCommonProxy")
	public static ModCommonProxy proxy;
	
	@SidedProxy(clientSide = "chylex.hee.proxy.FXClientProxy", serverSide = "chylex.hee.proxy.FXCommonProxy")
	public static FXCommonProxy fx;
	
	public static String modVersion;
	public static String configPath;
	
	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e){
		ReflectionPublicizer.load();
		modVersion = e.getModMetadata().version;
		configPath = e.getSuggestedConfigurationFile().getParentFile().getName();
		
		// CONFIGURATION LOAD

		ConfigHandler.register(e.getSuggestedConfigurationFile());
		ModCreativeTab.registerTab();
		BlockList.loadBlocks();
		ItemList.loadItems();
		proxy.loadConfiguration();
		
		// DIMENSION
		
		DimensionOverride.setup();
		
		// BLOCKS
		
		BlockReplaceHelper.replaceBlock(Blocks.dragon_egg, BlockDragonEggCustom.class);
		
		GameRegistryUtil.registerBlock(BlockList.obsidian_end, "obsidian_end");
		GameRegistryUtil.registerBlock(BlockList.obsidian_stairs, "obsidian_stairs");
		GameRegistryUtil.registerBlock(BlockList.obsidian_special, "obsidian_special", ItemBlockWithSubtypes.class);
		GameRegistryUtil.registerBlock(BlockList.obsidian_special_glow, "obsidian_special_glow", ItemBlockWithSubtypes.class);
		GameRegistryUtil.registerBlock(BlockList.essence_altar, "essence_altar", ItemBlockEssenceAltar.class);
		GameRegistryUtil.registerBlock(BlockList.enhanced_brewing_stand, "enhanced_brewing_stand_block");
		GameRegistryUtil.registerBlock(BlockList.enhanced_tnt, "enhanced_tnt", ItemBlockEnhancedTNT.class);
		GameRegistryUtil.registerBlock(BlockList.decomposition_table, "decomposition_table");
		GameRegistryUtil.registerBlock(BlockList.energy_extraction_table, "energy_extraction_table");
		GameRegistryUtil.registerBlock(BlockList.end_powder_ore, "end_powder_ore");
		GameRegistryUtil.registerBlock(BlockList.stardust_ore, "stardust_ore");
		GameRegistryUtil.registerBlock(BlockList.igneous_rock_ore, "igneous_rock_ore");
		GameRegistryUtil.registerBlock(BlockList.instability_orb_ore, "instability_orb_ore");
		GameRegistryUtil.registerBlock(BlockList.ender_goo, "ender_goo");
		GameRegistryUtil.registerBlock(BlockList.end_terrain, "end_stone_terrain", ItemBlockWithSubtypes.class);
		GameRegistryUtil.registerBlock(BlockList.spooky_log, "spooky_log");
		GameRegistryUtil.registerBlock(BlockList.spooky_leaves, "spooky_leaves");
		GameRegistryUtil.registerBlock(BlockList.soul_charm, "soul_charm", ItemBlockSoulCharm.class);
		GameRegistryUtil.registerBlock(BlockList.crossed_decoration, "crossed_decoration", ItemBlockWithSubtypes.class);
		GameRegistryUtil.registerBlock(BlockList.death_flower, "death_flower", ItemBlockEndFlower.class);
		GameRegistryUtil.registerBlock(BlockList.death_flower_pot, "death_flower_pot");
		GameRegistryUtil.registerBlock(BlockList.enderman_head, "enderman_head_block");
		GameRegistryUtil.registerBlock(BlockList.sphalerite, "sphalerite", ItemBlockWithSubtypes.class);
		GameRegistryUtil.registerBlock(BlockList.ravaged_brick, "ravaged_brick", ItemBlockWithSubtypes.class);
		GameRegistryUtil.registerBlock(BlockList.dungeon_puzzle, "dungeon_puzzle", ItemBlockWithSubtypes.class);
		GameRegistryUtil.registerBlock(BlockList.energy_cluster, "energy_cluster");
		GameRegistryUtil.registerBlock(BlockList.corrupted_energy_high, "corrupted_energy_high");
		GameRegistryUtil.registerBlock(BlockList.corrupted_energy_low, "corrupted_energy_low");
		GameRegistryUtil.registerBlock(BlockList.laser_beam, "laser_beam");
		GameRegistryUtil.registerBlock(BlockList.custom_spawner, "custom_spawner");
		GameRegistryUtil.registerBlock(BlockList.temple_end_portal, "temple_end_portal");
		GameRegistryUtil.registerBlock(BlockList.biome_core, "biome_core");
		
		BlockList.obsidian_end.setHarvestLevel("pickaxe", 3);
		BlockList.obsidian_stairs.setHarvestLevel("pickaxe", 3);
		BlockList.obsidian_special.setHarvestLevel("pickaxe", 3);
		BlockList.obsidian_special_glow.setHarvestLevel("pickaxe", 3);
		BlockList.stardust_ore.setHarvestLevel("pickaxe", 3);
		BlockList.igneous_rock_ore.setHarvestLevel("pickaxe", 2);
		BlockList.instability_orb_ore.setHarvestLevel("pickaxe", 3);
		BlockList.sphalerite.setHarvestLevel("pickaxe", 1);
		BlockList.end_terrain.setHarvestLevel("pickaxe", 1);
		BlockList.spooky_log.setHarvestLevel("axe", 0);
		Blocks.fire.setFireInfo(BlockList.spooky_log, 10, 10);
		Blocks.fire.setFireInfo(BlockList.spooky_leaves, 40, 30);
		
		MinecraftForge.EVENT_BUS.register(BlockList.essence_altar);
		MinecraftForge.EVENT_BUS.register(BlockList.ender_goo);
		
		// ITEMS
		
		GameRegistryUtil.registerItem(ItemList.adventurers_diary, "adventurers_diary");
		GameRegistryUtil.registerItem(ItemList.altar_nexus, "altar_nexus");
		GameRegistryUtil.registerItem(ItemList.essence, "essence");
		GameRegistryUtil.registerItem(ItemList.end_powder, "end_powder");
		GameRegistryUtil.registerItem(ItemList.stardust, "stardust");
		GameRegistryUtil.registerItem(ItemList.enhanced_ender_pearl, "enhanced_ender_pearl");
		GameRegistryUtil.registerItem(ItemList.igneous_rock, "igneous_rock");
		GameRegistryUtil.registerItem(ItemList.instability_orb, "instability_orb");
		GameRegistryUtil.registerItem(ItemList.potion_of_instability, "potion_of_instability");
		GameRegistryUtil.registerItem(ItemList.spatial_dash_gem, "spatial_dash_gem");
		GameRegistryUtil.registerItem(ItemList.transference_gem, "transference_gem");
		GameRegistryUtil.registerItem(ItemList.enderman_head, "enderman_head");
		GameRegistryUtil.registerItem(ItemList.bucket_ender_goo, "bucket_ender_goo");
		GameRegistryUtil.registerItem(ItemList.temple_caller, "temple_caller");
		GameRegistryUtil.registerItem(ItemList.biome_compass, "biome_compass");
		GameRegistryUtil.registerItem(ItemList.silverfish_blood, "silverfish_blood");
		GameRegistryUtil.registerItem(ItemList.dry_splinter, "dry_splinter");
		GameRegistryUtil.registerItem(ItemList.infestation_remedy, "infestation_remedy");
		GameRegistryUtil.registerItem(ItemList.ghost_amulet, "ghost_amulet");
		GameRegistryUtil.registerItem(ItemList.ectoplasm, "endoplasm");
		GameRegistryUtil.registerItem(ItemList.rune, "rune");
		GameRegistryUtil.registerItem(ItemList.charm, "charm");
		GameRegistryUtil.registerItem(ItemList.corporeal_mirage_orb, "corporeal_mirage_orb");
		GameRegistryUtil.registerItem(ItemList.spectral_wand, "spectral_wand");
		GameRegistryUtil.registerItem(ItemList.scorching_pickaxe, "schorching_pickaxe");
		GameRegistryUtil.registerItem(ItemList.enderman_relic, "enderman_relic_repaired");
		GameRegistryUtil.registerItem(ItemList.enhanced_brewing_stand, "enhanced_brewing_stand");
		GameRegistryUtil.registerItem(ItemList.music_disk, "music_disk");
		GameRegistryUtil.registerItem(ItemList.ender_compendium, "ender_compendium");
		GameRegistryUtil.registerItem(ItemList.knowledge_fragment, "knowledge_fragment");
		GameRegistryUtil.registerItem(ItemList.spawn_eggs, "spawn_eggs");
		GameRegistryUtil.registerItem(ItemList.special_effects, "item_special_effects");
		
		MinecraftForge.EVENT_BUS.register(ItemList.enderman_head);
		MinecraftForge.EVENT_BUS.register(ItemList.scorching_pickaxe);
		GameRegistry.registerFuelHandler((IFuelHandler)ItemList.igneous_rock);
		
		FluidContainerRegistry.registerFluidContainer(BlockEnderGoo.fluid, new ItemStack(ItemList.bucket_ender_goo), FluidContainerRegistry.EMPTY_BUCKET);
		
		// ENTITIES
		
		EntityList.stringToClassMapping.remove("EnderCrystal");
		EntityList.IDtoClassMapping.remove(Integer.valueOf(200));
		EntityList.addMapping(EntityBlockEnderCrystal.class, "EnderCrystal", 200);
		
		EntityRegistry.registerModEntity(EntityBossDragon.class, "Dragon", 8, this, 320, 1, true);
		
		EntityRegistry.registerModEntity(EntityMiniBossEnderEye.class, "EnderEye", 11, this, 256, 1, true);
		EntityRegistry.registerModEntity(EntityMiniBossFireFiend.class, "FireFiend", 23, this, 256, 1, true);
		EntityRegistry.registerModEntity(EntityBossEnderDemon.class, "EnderDemon", 21, this, 512, 1, true);
		EntityRegistry.registerModEntity(EntityMobAngryEnderman.class, "AngryEnderman", 1, this, 256, 1, true);
		EntityRegistry.registerModEntity(EntityMobBabyEnderman.class, "BabyEnderman", 16, this, 256, 1, true);
		EntityRegistry.registerModEntity(EntityMobParalyzedEnderman.class, "ParalyzedEnderman", 24, this, 256, 1, true);
		EntityRegistry.registerModEntity(EntityMobEnderGuardian.class, "EnderGuardian", 22, this, 256, 1, true);
		EntityRegistry.registerModEntity(EntityMobVampiricBat.class, "VampireBat", 10, this, 256, 1, true);
		EntityRegistry.registerModEntity(EntityMobInfestedBat.class, "InfestedBat", 12, this, 128, 1, true);
		EntityRegistry.registerModEntity(EntityMobForestGhost.class, "ForestGhost", 13, this, 32, 1, true);
		EntityRegistry.registerModEntity(EntityMobLouse.class, "Louse", 34, this, 256, 1, true);
		EntityRegistry.registerModEntity(EntityMobFireGolem.class, "FireGolem", 14, this, 256, 1, true);
		EntityRegistry.registerModEntity(EntityMobScorchingLens.class, "ScorchedLens", 15, this, 256, 1, true);
		EntityRegistry.registerModEntity(EntityMobCorporealMirage.class, "CorporealMirage", 29, this, 256, 1, true);

		EntityRegistry.registerModEntity(EntityBlockFallingDragonEgg.class, "FallingDragonEgg", 25, this, 128, 1, true);
		EntityRegistry.registerModEntity(EntityBlockFallingObsidian.class, "FallingObsidian", 26, this, 128, 1, true);
		EntityRegistry.registerModEntity(EntityBlockTempleDragonEgg.class, "TempleEgg", 7, this, 420, 1, true);
		EntityRegistry.registerModEntity(EntityBlockEnhancedTNTPrimed.class, "EnhancedTNT", 31, this, 128, 1, true);

		EntityRegistry.registerModEntity(EntityItemIgneousRock.class, "ItemIgneousRock", 9, this, 64, 1, true);
		EntityRegistry.registerModEntity(EntityItemInstabilityOrb.class, "ItemInstabilityOrb", 6, this, 64, 1, true);
		EntityRegistry.registerModEntity(EntityItemAltar.class, "ItemAltar", 19, this, 128, 1, false);
		EntityRegistry.registerModEntity(EntityItemEndPowder.class, "ItemEndPowder", 27, this, 64 ,1, true);
		
		EntityRegistry.registerModEntity(EntityProjectileDragonFireball.class, "ProjectileDragonFireball", 2, this, 128, 1, true);
		EntityRegistry.registerModEntity(EntityProjectileDragonFreezeball.class, "ProjectileDragonFreezeball", 3, this, 128, 1, true);
		EntityRegistry.registerModEntity(EntityProjectileEnhancedEnderPearl.class, "ProjectileEnhancedEnderPearl", 5, this, 128, 1, true);
		EntityRegistry.registerModEntity(EntityProjectileFlamingBall.class, "ProjectileFlamingBall", 17, this, 128, 1, true);
		EntityRegistry.registerModEntity(EntityProjectileGolemFireball.class, "ProjectileGolemFireball", 18, this, 128, 1, true);
		EntityRegistry.registerModEntity(EntityProjectileCorporealMirageOrb.class, "ProjectileCorporealMirageOrb", 28, this, 128, 1, true);
		EntityRegistry.registerModEntity(EntityProjectilePotionOfInstability.class, "ProjectilePotionOfInstability", 30, this, 128, 1, true);
		EntityRegistry.registerModEntity(EntityProjectileSpatialDash.class, "ProjectileSpatialDash", 32, this, 128, 1, true);

		EntityRegistry.registerModEntity(EntityWeatherLightningBoltSafe.class, "LightningBoltSafe", 4, this, 512, 1, false);
		EntityRegistry.registerModEntity(EntityWeatherLightningBoltDemon.class, "LightningBoltDemon", 21, this, 512, 1, false);
		// last: 34

		// TILE ENTITIES

		GameRegistryUtil.registerTileEntity(TileEntityEssenceAltar.class, "EssenceAltar");
		GameRegistryUtil.registerTileEntity(TileEntityEnhancedBrewingStand.class, "EnhancedBrewingStand");
		GameRegistryUtil.registerTileEntity(TileEntityEndermanHead.class, "EndermanHead");
		GameRegistryUtil.registerTileEntity(TileEntityLaserBeam.class, "LaserBeam");
		GameRegistryUtil.registerTileEntity(TileEntityCustomSpawner.class, "EndermanSpawner");
		GameRegistryUtil.registerTileEntity(TileEntitySoulCharm.class, "SoulCharm");
		GameRegistryUtil.registerTileEntity(TileEntityDecompositionTable.class, "DecompositionTable");
		GameRegistryUtil.registerTileEntity(TileEntityEnergyExtractionTable.class, "EnergyExtractionTable");
		GameRegistryUtil.registerTileEntity(TileEntityEnergyCluster.class, "EnergyCluster");
		GameRegistryUtil.registerTileEntity(TileEntityEnhancedTNT.class, "EnhancedTNT");
		
		// ACHIEVEMENTS
		
		AchievementManager.register();
		AchievementEvents.register();
		
		// FORGE AND FML
		
		MinecraftForge.EVENT_BUS.register(new MiscEvents());
		TempleEvents.register();
		InfestationEvents.register();
		//ApocalypseEvents.register();
		CharmPouchHandler.register();
		
		proxy.registerSidedEvents();
		proxy.registerRenderers();
	}
	
	@EventHandler
	public void onInit(FMLInitializationEvent e){
		PacketPipeline.initializePipeline();
		NetworkRegistry.INSTANCE.registerGuiHandler(this,GuiHandler.instance);
		RecipeList.addRecipes();
		WorldLoot.registerWorldLoot();
		
		OrbAcquirableItems.initialize();
		OrbSpawnableMobs.initialize();
		KnowledgeRegistrations.initialize();
	}
	
	@EventHandler
	public void onPostInit(FMLPostInitializationEvent e){
		ModIntegrationManager.integrateMods();
		DimensionOverride.postInit();
	}

	@EventHandler
	public void onServerStarting(FMLServerStartingEvent e){
		e.registerServerCommand(new HeeAdminCommand());
		e.registerServerCommand(new HeeDebugCommand());
	}
}

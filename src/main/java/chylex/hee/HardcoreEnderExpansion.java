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
import chylex.hee.entity.mob.EntityMobHauntedMiner;
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
import chylex.hee.entity.projectile.EntityProjectileMinerShot;
import chylex.hee.entity.projectile.EntityProjectilePotionOfInstability;
import chylex.hee.entity.projectile.EntityProjectileSpatialDash;
import chylex.hee.entity.weather.EntityWeatherLightningBoltDemon;
import chylex.hee.entity.weather.EntityWeatherLightningBoltSafe;
import chylex.hee.gui.core.GuiHandler;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.MiscEvents;
import chylex.hee.mechanics.charms.handler.CharmPouchHandler;
import chylex.hee.mechanics.compendium.events.CompendiumEvents;
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
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.system.savedata.WorldDataHandler;
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

@Mod(modid = "HardcoreEnderExpansion", name = "Hardcore Ender Expansion", version = "", useMetadata = true, guiFactory = "chylex.hee.gui.core.ModGuiFactory")
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
		Stopwatch.time("PreInitEvent");
		
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
		BlockList.registerBlocks();
		
		BlockList.obsidian_falling.setHarvestLevel("pickaxe", 3);
		BlockList.obsidian_stairs.setHarvestLevel("pickaxe", 3);
		BlockList.obsidian_special.setHarvestLevel("pickaxe", 3);
		BlockList.obsidian_special_glow.setHarvestLevel("pickaxe", 3);
		BlockList.stardust_ore.setHarvestLevel("pickaxe", 3);
		BlockList.igneous_rock_ore.setHarvestLevel("pickaxe", 2);
		BlockList.instability_orb_ore.setHarvestLevel("pickaxe", 3);
		BlockList.sphalerite.setHarvestLevel("pickaxe", 1);
		BlockList.end_terrain.setHarvestLevel("pickaxe", 1);
		BlockList.cinder.setHarvestLevel("pickaxe", 2);
		BlockList.spooky_log.setHarvestLevel("axe", 0);
		Blocks.fire.setFireInfo(BlockList.spooky_log, 10, 10);
		Blocks.fire.setFireInfo(BlockList.spooky_leaves, 40, 30);
		
		MinecraftForge.EVENT_BUS.register(BlockList.essence_altar);
		MinecraftForge.EVENT_BUS.register(BlockList.ender_goo);
		
		// ITEMS
		
		ItemList.registerItems();
		
		MinecraftForge.EVENT_BUS.register(ItemList.enderman_head);
		MinecraftForge.EVENT_BUS.register(ItemList.scorching_pickaxe);
		GameRegistry.registerFuelHandler((IFuelHandler)ItemList.igneous_rock);
		
		FluidContainerRegistry.registerFluidContainer(BlockEnderGoo.fluid, new ItemStack(ItemList.bucket_ender_goo), FluidContainerRegistry.EMPTY_BUCKET);
		
		// ENTITIES
		
		EntityList.stringToClassMapping.remove("EnderCrystal");
		EntityList.IDtoClassMapping.remove(Integer.valueOf(200));
		EntityList.addMapping(EntityBlockEnderCrystal.class, "EnderCrystal", 200);
		
		EntityRegistry.registerModEntity(EntityBossDragon.class, "Dragon", 8, this, 320, 1, true);
		EntityRegistry.registerModEntity(EntityBossEnderDemon.class, "EnderDemon", 21, this, 512, 1, true);
		EntityRegistry.registerModEntity(EntityMiniBossEnderEye.class, "EnderEye", 11, this, 256, 1, true);
		EntityRegistry.registerModEntity(EntityMiniBossFireFiend.class, "FireFiend", 23, this, 256, 1, true);
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
		EntityRegistry.registerModEntity(EntityMobHauntedMiner.class, "HauntedMiner", 35, this, 256, 1, true);
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
		EntityRegistry.registerModEntity(EntityProjectileMinerShot.class, "ProjectileMinerShot", 36, this, 128, 1, true);
		EntityRegistry.registerModEntity(EntityProjectileCorporealMirageOrb.class, "ProjectileCorporealMirageOrb", 28, this, 128, 1, true);
		EntityRegistry.registerModEntity(EntityProjectilePotionOfInstability.class, "ProjectilePotionOfInstability", 30, this, 128, 1, true);
		EntityRegistry.registerModEntity(EntityProjectileSpatialDash.class, "ProjectileSpatialDash", 32, this, 128, 1, true);

		EntityRegistry.registerModEntity(EntityWeatherLightningBoltSafe.class, "LightningBoltSafe", 4, this, 512, 1, false);
		EntityRegistry.registerModEntity(EntityWeatherLightningBoltDemon.class, "LightningBoltDemon", 21, this, 512, 1, false);
		// last: 36

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
		CompendiumEvents.register();
		TempleEvents.register();
		InfestationEvents.register();
		CharmPouchHandler.register();
		WorldDataHandler.register();
		
		proxy.registerSidedEvents();
		proxy.registerRenderers();
		
		Stopwatch.finish("PreInitEvent");
	}
	
	@EventHandler
	public void onInit(FMLInitializationEvent e){
		Stopwatch.time("InitEvent");
		
		PacketPipeline.initializePipeline();
		NetworkRegistry.INSTANCE.registerGuiHandler(this,GuiHandler.instance);
		RecipeList.addRecipes();
		WorldLoot.registerWorldLoot();
		
		KnowledgeRegistrations.initialize();
		chylex.hee.mechanics.compendium.content.KnowledgeRegistrations.initialize();
		
		Stopwatch.finish("InitEvent");
	}
	
	@EventHandler
	public void onPostInit(FMLPostInitializationEvent e){
		Stopwatch.time("PostInitEvent");
		
		OrbAcquirableItems.initialize();
		OrbSpawnableMobs.initialize();
		ModIntegrationManager.integrateMods();
		DimensionOverride.postInit();
		
		Stopwatch.finish("PostInitEvent");
	}

	@EventHandler
	public void onServerStarting(FMLServerStartingEvent e){
		e.registerServerCommand(new HeeAdminCommand());
		e.registerServerCommand(new HeeDebugCommand());
	}
}

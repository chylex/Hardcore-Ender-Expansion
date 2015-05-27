package chylex.hee;
import java.io.File;
import net.minecraftforge.common.MinecraftForge;
import chylex.hee.api.HeeIMC;
import chylex.hee.entity.boss.dragon.managers.DragonChunkManager;
import chylex.hee.gui.core.GuiHandler;
import chylex.hee.init.BlockList;
import chylex.hee.init.EntityList;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.MiscEvents;
import chylex.hee.mechanics.RecipeList;
import chylex.hee.mechanics.causatum.CausatumEvents;
import chylex.hee.mechanics.charms.handler.CharmPouchHandler;
import chylex.hee.mechanics.compendium.KnowledgeRegistrations;
import chylex.hee.mechanics.compendium.content.fragments.KnowledgeFragmentCrafting;
import chylex.hee.mechanics.compendium.events.CompendiumEvents;
import chylex.hee.mechanics.curse.CurseEvents;
import chylex.hee.mechanics.energy.EnergyEvents;
import chylex.hee.mechanics.misc.PlayerDataHandler;
import chylex.hee.mechanics.misc.PlayerTransportBeacons;
import chylex.hee.mechanics.misc.TempleEvents;
import chylex.hee.mechanics.orb.OrbAcquirableItems;
import chylex.hee.mechanics.orb.OrbSpawnableMobs;
import chylex.hee.mechanics.voidchest.VoidChestEvents;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.proxy.FXCommonProxy;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.proxy.NotificationCommonProxy;
import chylex.hee.system.ConfigHandler;
import chylex.hee.system.ReflectionPublicizer;
import chylex.hee.system.achievements.AchievementEvents;
import chylex.hee.system.achievements.AchievementManager;
import chylex.hee.system.commands.HeeAdminCommand;
import chylex.hee.system.commands.HeeBaconCommand;
import chylex.hee.system.commands.HeeDebugCommand;
import chylex.hee.system.creativetab.ModCreativeTab;
import chylex.hee.system.integration.ModIntegrationManager;
import chylex.hee.system.logging.Log;
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.test.UnitTester;
import chylex.hee.system.test.data.RunTime;
import chylex.hee.system.update.UpdateNotificationManager;
import chylex.hee.world.DimensionOverride;
import chylex.hee.world.loot.WorldLoot;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid = "HardcoreEnderExpansion", name = "Hardcore Ender Expansion", version = "", useMetadata = true, guiFactory = "chylex.hee.gui.core.ModGuiFactory")
public class HardcoreEnderExpansion{
	@Instance("HardcoreEnderExpansion")
	public static HardcoreEnderExpansion instance;

	@SidedProxy(clientSide = "chylex.hee.proxy.ModClientProxy", serverSide = "chylex.hee.proxy.ModCommonProxy")
	public static ModCommonProxy proxy;
	
	@SidedProxy(clientSide = "chylex.hee.proxy.FXClientProxy", serverSide = "chylex.hee.proxy.FXCommonProxy")
	public static FXCommonProxy fx;
	
	@SidedProxy(clientSide = "chylex.hee.proxy.NotificationClientProxy", serverSide = "chylex.hee.proxy.NotificationCommonProxy")
	public static NotificationCommonProxy notifications;
	
	public static final int buildId = 24_94_15_0;
	
	public static String modVersion;
	public static String configPath;
	public static File sourceFile;

	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e){
		Stopwatch.time("PreInitEvent");
		
		ReflectionPublicizer.load();
		modVersion = e.getModMetadata().version;
		configPath = e.getSuggestedConfigurationFile().getParentFile().getName();
		sourceFile = e.getSourceFile();
		Log.initializeDebug();
		UnitTester.load();
		
		// CONFIGURATION LOAD

		ConfigHandler.register(e.getSuggestedConfigurationFile());
		BlockList.loadBlocks();
		ItemList.loadItems();
		proxy.loadConfiguration();
		
		// INITIALIZATION
		
		Stopwatch.time("PreInitEvent - data");
		
		ModCreativeTab.registerTabs();
		BlockList.registerBlocks();
		ItemList.registerItems();
		BlockList.configureBlocks();
		ItemList.configureItems();
		EntityList.registerEntities();
		BlockList.registerTileEntities();
		
		Stopwatch.finish("PreInitEvent - data");
		
		// DIMENSION

		DimensionOverride.setup();
		
		// ACHIEVEMENTS
		
		AchievementManager.register();
		AchievementEvents.register();
		
		// FORGE AND FML

		Stopwatch.time("PreInitEvent - events");
		
		MinecraftForge.EVENT_BUS.register(new MiscEvents());
		FMLCommonHandler.instance().bus().register(new UpdateNotificationManager());
		PlayerDataHandler.register();
		CompendiumEvents.register();
		CharmPouchHandler.register();
		WorldDataHandler.register();
		VoidChestEvents.register();
		EnergyEvents.register();
		TempleEvents.register();
		CurseEvents.register();
		CausatumEvents.register();
		PlayerTransportBeacons.register();
		DragonChunkManager.register();
		
		Stopwatch.finish("PreInitEvent - events");
		
		proxy.registerSidedEvents();
		proxy.registerRenderers();		
		notifications.register();
		
		UnitTester.trigger(RunTime.PREINIT);
		
		Stopwatch.finish("PreInitEvent");
	}
	
	@EventHandler
	public void onInit(FMLInitializationEvent e){
		Stopwatch.time("InitEvent");
		
		PacketPipeline.initializePipeline();
		NetworkRegistry.INSTANCE.registerGuiHandler(this,GuiHandler.instance);
		RecipeList.addRecipes();
		WorldLoot.registerWorldLoot();
		
		UnitTester.trigger(RunTime.INIT);
		
		Stopwatch.finish("InitEvent");
	}
	
	@EventHandler
	public void onPostInit(FMLPostInitializationEvent e){
		Stopwatch.time("PostInitEvent");
		
		HeeIMC.runPostInit();
		KnowledgeRegistrations.initialize();
		OrbAcquirableItems.initialize();
		OrbSpawnableMobs.initialize();
		ModIntegrationManager.integrateMods();
		DimensionOverride.postInit();
		
		UnitTester.trigger(RunTime.POSTINIT);
		
		Stopwatch.finish("PostInitEvent");
	}
	
	@EventHandler
	public void onLoadComplete(FMLLoadCompleteEvent e){
		Stopwatch.time("LoadCompleteEvent");
		
		try{
			DimensionOverride.verifyIntegrity();
			KnowledgeFragmentCrafting.verifyRecipes();
			HeeIMC.runLoadComplete();
			
			UnitTester.trigger(RunTime.LOADCOMPLETE);
			UnitTester.finalizeEventTests();
		}
		catch(Throwable t){
			FMLCommonHandler.instance().raiseException(t,"Critical error handling post-load data.",true);
		}
		
		Stopwatch.finish("LoadCompleteEvent");
	}

	@EventHandler
	public void onServerStarting(FMLServerStartingEvent e){
		e.registerServerCommand(new HeeAdminCommand());
		e.registerServerCommand(new HeeBaconCommand());
		e.registerServerCommand(new HeeDebugCommand());
	}
	
	@EventHandler
	public void onIMC(IMCEvent e){
		for(IMCMessage message:e.getMessages())HeeIMC.acceptIMC(message);
	}
}

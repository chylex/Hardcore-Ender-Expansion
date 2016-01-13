package chylex.hee;
import java.io.File;
import java.util.Map;
import chylex.hee.api.HeeIMC;
import chylex.hee.game.ConfigHandler;
import chylex.hee.game.ModIntegrity;
import chylex.hee.game.ModTransition;
import chylex.hee.game.achievements.AchievementEvents;
import chylex.hee.game.achievements.AchievementManager;
import chylex.hee.game.commands.HeeAdminCommand;
import chylex.hee.game.commands.HeeBaconCommand;
import chylex.hee.game.commands.HeeDebugCommand;
import chylex.hee.game.integration.ModIntegrationManager;
import chylex.hee.game.save.SaveData;
import chylex.hee.gui.core.GuiHandler;
import chylex.hee.init.BlockList;
import chylex.hee.init.EntityList;
import chylex.hee.init.ItemList;
import chylex.hee.init.ModInitHandler;
import chylex.hee.mechanics.MiscEvents;
import chylex.hee.mechanics.RecipeList;
import chylex.hee.mechanics.causatum.CausatumEventHandler;
import chylex.hee.mechanics.compendium.KnowledgeRegistrations;
import chylex.hee.mechanics.compendium.events.CompendiumEvents;
import chylex.hee.mechanics.enhancements.EnhancementRegistry;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.proxy.FXCommonProxy;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.proxy.NotificationCommonProxy;
import chylex.hee.system.ReflectionPublicizer;
import chylex.hee.system.logging.Log;
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.world.DimensionOverride;
import chylex.hee.world.end.server.TerritoryEvents;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLMissingMappingsEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
	
	public static final int buildId = 26_96_15_0;
	
	public static String modVersion;
	public static String configPath;
	public static File sourceFile;

	@EventHandler
	public void onPreInit(FMLPreInitializationEvent e){
		Stopwatch.time("PreInitEvent");
		
		modVersion = e.getModMetadata().version;
		configPath = e.getSuggestedConfigurationFile().getParentFile().getName();
		sourceFile = e.getSourceFile();
		
		e.getModMetadata().description = e.getModMetadata().description.replace('$','\u00a7');
		
		ReflectionPublicizer.load();
		Log.initializeDebug();
		
		// CONFIGURATION LOAD

		ConfigHandler.register(e.getSuggestedConfigurationFile());
		BlockList.loadBlocks();
		ItemList.loadItems();
		proxy.loadConfiguration();
		
		// INITIALIZATION
		
		Stopwatch.time("PreInitEvent - data");
		
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
		
		MiscEvents.register();
		CompendiumEvents.register();
		// TODO CharmPouchHandler.register();
		TerritoryEvents.register();
		SaveData.register();
		CausatumEventHandler.register();
		// TODO CurseEvents.register();
		// TODO DragonChunkManager.register();
		ModTransition.register();
		
		Stopwatch.finish("PreInitEvent - events");
		
		proxy.registerSidedEvents();
		proxy.registerRenderers();
		notifications.register();
		
		ModInitHandler.finishPreInit();
		ModIntegrationManager.sendIMCs();
		
		Stopwatch.finish("PreInitEvent");
	}
	
	@EventHandler
	public void onInit(FMLInitializationEvent e){
		Stopwatch.time("InitEvent");
		
		PacketPipeline.initializePipeline();
		NetworkRegistry.INSTANCE.registerGuiHandler(this,GuiHandler.instance);
		RecipeList.addRecipes();
		EnhancementRegistry.init();
		// TODO WorldLoot.registerWorldLoot();
		
		Stopwatch.finish("InitEvent");
	}
	
	@EventHandler
	public void onPostInit(FMLPostInitializationEvent e){
		Stopwatch.time("PostInitEvent");
		
		HeeIMC.runPostInit();
		KnowledgeRegistrations.initialize();
		// TODO OrbAcquirableItems.initialize();
		// TODO OrbSpawnableMobs.initialize();
		ModIntegrationManager.integrateMods();
		DimensionOverride.postInit();
		
		Stopwatch.finish("PostInitEvent");
	}
	
	@EventHandler
	public void onLoadComplete(FMLLoadCompleteEvent e){
		Stopwatch.time("LoadCompleteEvent");
		
		try{
			ModIntegrity.verify();
			HeeIMC.runLoadComplete();
		}
		catch(Throwable t){
			FMLCommonHandler.instance().raiseException(t,"Error running LoadComplete event in Hardcore Ender Expansion!",true);
		}
		
		Stopwatch.finish("LoadCompleteEvent");
	}
	
	@EventHandler
	@SideOnly(Side.SERVER)
	public void onServerAboutToStart(FMLServerAboutToStartEvent e){
		ModTransition.convertServer();
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
	
	@EventHandler
	public void onMissingMappings(FMLMissingMappingsEvent e){
		ModTransition.updateMappings(e);
	}
	
	@NetworkCheckHandler
	public boolean onNetworkCheck(Map<String,String> mods, Side side){
		return mods.getOrDefault("HardcoreEnderExpansion","").equals(modVersion);
	}
}

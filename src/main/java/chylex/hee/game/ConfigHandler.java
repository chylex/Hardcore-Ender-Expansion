package chylex.hee.game;
import java.io.File;
import java.util.List;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.api.HeeIMC;
import chylex.hee.block.BlockEnderGoo;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.mechanics.misc.StardustDecomposition;
import chylex.hee.mechanics.orb.OrbAcquirableItems;
import chylex.hee.proxy.ModClientProxy;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.sound.MusicManager;
import chylex.hee.system.logging.Log;
import chylex.hee.system.update.UpdateNotificationManager;
import chylex.hee.system.util.ItemPattern;
import chylex.hee.world.biome.BiomeGenHardcoreEnd;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class ConfigHandler{
	private static ConfigHandler instance;
	
	public static void register(File configFile){
		FMLCommonHandler.instance().bus().register(instance = new ConfigHandler(new Configuration(configFile)));
	}

	@SideOnly(Side.CLIENT)
	public static void loadClient(){
		instance.loadClientConfig();
		instance.loadNotificationConfig();
	}
	
	public static void loadGeneral(){
		instance.loadGeneralConfig();
	}
	
	public static List<IConfigElement> getGuiConfigElements(){
		List<IConfigElement> elements = new ConfigElement(instance.config.getCategory("client")).getChildElements();
		elements.addAll(new ConfigElement(instance.config.getCategory("notifications")).getChildElements());
		elements.addAll(new ConfigElement(instance.config.getCategory("general")).getChildElements());
		return elements;
	}
	
	public static String getConfigString(){
		return instance.config.toString();
	}

	private final Configuration config;
	private String currentCategory;
	private boolean firstTimeClient = true, firstTimeGeneral = true;
	
	private ConfigHandler(Configuration config){
		this.config = config;
		this.config.load();
		ModTransition.cleanupConfig(config);
	}
	
	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent e){
		if (e.modID.equals("HardcoreEnderExpansion"))HardcoreEnderExpansion.proxy.loadConfiguration();
	}
	
	// config loading
	
	@SideOnly(Side.CLIENT)
	private void loadClientConfig(){
		currentCategory = "client";
		
		if (firstTimeClient){
			ModClientProxy.loadEnderbacon(getInt("hardcoreEnderbaconMode", 0, "0 = enabled on April Fools, 1 = always enabled, 2 = never enabled.").setShowInGui(false).getInt());
			MusicManager.enableCustomMusic = getBool("enableMusic", true, "Custom music playing in the End dimension and custom Music Discs. If another mod that changes music is installed, HEE music will be disabled.").setRequiresMcRestart(true).getBoolean();
			MusicManager.removeVanillaDelay = getBool("removeVanillaDelay", false, "Removes long delays between vanilla music tracks. If another mod that changes music is installed, the config option will be disabled.").setRequiresMcRestart(true).getBoolean();
			firstTimeClient = false;
		}
		
		GuiEnderCompendium.pausesGame = getBoolValue("compendiumPausesGame", true, "If enabled, in singleplayer the Ender Compendium pauses the game when open.");
		GuiEnderCompendium.wasPaused = GuiEnderCompendium.pausesGame;
		
		if (config.hasChanged())config.save();
	}

	@SideOnly(Side.CLIENT)
	private void loadNotificationConfig(){
		currentCategory = "notifications";
		
		UpdateNotificationManager.enableNotifications = getBoolValue("enableUpdateNotifications", true, "Notifies users about new updates. The notifications can be customized with other options, and have no effect on the game performance.");
		UpdateNotificationManager.enableOneReportPerUpdate = getBoolValue("enableOneReportPerUpdate", false, "Each update only shows a single report.");
		UpdateNotificationManager.enableNewerMC = getBoolValue("enableNewerMC", false, "Checks whether a new version for newer Minecraft is available.");
		UpdateNotificationManager.enableBuildCheck = getBoolValue("enableBuildCheck", true, "It is highly suggested to keep this option enabled. This will detect broken builds with critical errors that can crash your game. These are usually fixed very quickly, but it is important to notify people who downloaded the broken build.");
		
		if (config.hasChanged())config.save();
	}
	
	private void loadGeneralConfig(){
		currentCategory = "general";
		
		ModCommonProxy.opMobs = getBoolValue("overpoweredMobs", false, "Additional abilities and increased attributes of mobs in the End, useful for modpacks with powerful weapons and armor.");
		BlockEnderGoo.shouldBattleWater = getBoolValue("gooBattlesWater", true, "Ender Goo interacts with Water by battling it, this might cause lag from block updates.");
		Log.forceDebugEnabled = getBool("logDebuggingInfo", false, "Only use for debugging, enabling debug logging will have severe impact on game performance!").getBoolean();
		
		if (firstTimeGeneral){
			BiomeGenHardcoreEnd.overworldEndermanMultiplier = (float)getDecimal("overworldEndermanMultiplier", 1F, "Multiplies spawn weight of Endermen for each overworld biome (the weight is adjusted automatically).").setRequiresMcRestart(true).getDouble();
			OrbAcquirableItems.overrideRemoveBrokenRecipes = getBool("overrideRemoveBrokenRecipes", false, "This will remove broken recipes that would normally crash the game. ALWAYS REPORT THE RECIPES TO THE AUTHORS OF THE BROKEN MODS FIRST!").setShowInGui(false).getBoolean();
			ModCommonProxy.achievementStartId = getInt("achievementStartId", 3500, "Starting ID of achievements, only change this if there is a conflict.").setShowInGui(false).getInt();
			
			StardustDecomposition.addToBlacklist(new ItemPattern().setItemName("minecraft","fire"));
			StardustDecomposition.addToBlacklist(new ItemPattern().setItemName("ExtraUtilities","unstableingot"));
			StardustDecomposition.addToBlacklist(new ItemPattern().setItemName("witchery","*"));
			
			String[] imcs = getStringArray("IMC", new String[]{ "Write your message here" }, "List of IMC/API messages, documentation can be found on http://hee-api.chylex.com").setShowInGui(false).getStringList();
			
			if (!(imcs.length == 1 && (imcs[0].isEmpty() || imcs[0].equals("Write your message here")))){
				for(String imc:imcs)HeeIMC.acceptString("HEE Configuration File",imc);
			}
			
			firstTimeGeneral = false;
		}
		
		if (config.hasChanged())config.save();
	}
	
	// utility methods
	
	private boolean getBoolValue(String key, boolean defaultValue, String comment){
		return config.get(currentCategory,key,defaultValue,comment).getBoolean();
	}
	
	private Property getBool(String key, boolean defaultValue, String comment){
		return config.get(currentCategory,key,defaultValue,comment);
	}
	
	private Property getInt(String key, int defaultValue, String comment){
		return config.get(currentCategory,key,defaultValue,comment);
	}
	
	private Property getDecimal(String key, float defaultValue, String comment){
		return config.get(currentCategory,key,defaultValue,comment);
	}
	
	private Property getStringArray(String key, String[] defaultValue, String comment){
		return config.get(currentCategory,key,defaultValue,comment);
	}
}

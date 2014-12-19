package chylex.hee.system;
import java.io.File;
import java.util.List;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.block.BlockEnderGoo;
import chylex.hee.item.ItemTempleCaller;
import chylex.hee.mechanics.compendium.content.fragments.KnowledgeFragmentText;
import chylex.hee.mechanics.misc.StardustDecomposition;
import chylex.hee.mechanics.orb.OrbAcquirableItems;
import chylex.hee.proxy.ModClientProxy;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.sound.MusicManager;
import chylex.hee.system.logging.Log;
import chylex.hee.system.update.UpdateNotificationManager;
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
	}
	
	public static void loadGeneral(){
		instance.loadGeneralConfig();
	}
	
	public static List<IConfigElement> getGuiConfigElements(){
		List<IConfigElement> elements = new ConfigElement(instance.config.getCategory("client")).getChildElements();
		elements.addAll(new ConfigElement(instance.config.getCategory("general")).getChildElements());		
		return elements;
	}
	
	public static String getConfigString(){
		return instance.config.toString();
	}

	private final Configuration config;
	private String currentCategory;
	private boolean firstTimeGeneral = true;
	
	private ConfigHandler(Configuration config){
		this.config = config;
		this.config.load();
		
		config.moveProperty("general","enableMusic","client");
		config.moveProperty("client","enableUpdateNotifications","general");
		config.getCategory("general").remove("achievementIdStart");
		
		if (config.get("client","compendiumSmoothText",0).getType() == Type.BOOLEAN)config.getCategory("client").remove("compendiumSmoothText");
	}
	
	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent e){
		if (e.modID.equals("HardcoreEnderExpansion"))HardcoreEnderExpansion.proxy.loadConfiguration();
	}
	
	// config loading
	
	@SideOnly(Side.CLIENT)
	private void loadClientConfig(){
		currentCategory = "client";
		
		KnowledgeFragmentText.smoothRenderingType = (byte)getInt("compendiumSmoothText",0).getInt();
		MusicManager.enableMusic = getBool("enableMusic",true).setRequiresMcRestart(true).getBoolean();
		ModClientProxy.loadEnderbacon(getInt("hardcoreEnderbacon",0,"0 = enabled on April Fools, 1 = always enabled, 2 = never enabled").setShowInGui(false).getInt());
		
		if (config.hasChanged())config.save();
	}
	
	private void loadGeneralConfig(){
		currentCategory = "general";
		
		ModCommonProxy.opMobs = getBoolValue("overpoweredMobs",false);
		BlockEnderGoo.shouldBattleWater = getBoolValue("gooBattlesWater",true);
		ItemTempleCaller.isEnabled = getBoolValue("enableTempleCaller",true);
		BiomeGenHardcoreEnd.overrideMobLists = getBool("overrideBiomeMobs",false,"Prevents other mods from changing mobs that spawn in the End.").setRequiresMcRestart(true).getBoolean();
		BiomeGenHardcoreEnd.overworldEndermanMultiplier = (float)getDecimal("overworldEndermanMultiplier",1F,"Multiplies spawn weight of Endermen for each overworld biome.").setRequiresMcRestart(true).getDouble();
		Log.forceDebugEnabled = getBool("logDebuggingInfo",false,"Only use for debugging, enabling debug logging will have severe impact on game performance!").getBoolean();
		
		if (firstTimeGeneral){
			OrbAcquirableItems.overrideRemoveBrokenRecipes = getBool("overrideRemoveBrokenRecipes",false,"This will remove broken recipes that would normally crash the game. ALWAYS REPORT THE RECIPES TO THE AUTHORS OF THE BROKEN MODS FIRST!").setShowInGui(false).getBoolean();
			UpdateNotificationManager.enableNotifications = getBool("enableUpdateNotifications",true).setShowInGui(false).getBoolean();
			UpdateNotificationManager.enableBuildCheck = getBool("enableBuildCheck",true,"It is highly suggested to keep this option enabled. This will detect broken builds with critical errors that can crash your game. These are usually fixed very quickly, but it is important to notify people who downloaded the broken build.").setShowInGui(false).getBoolean();
			ModCommonProxy.achievementStartId = getInt("achievementStartId",3500).setShowInGui(false).getInt();
			StardustDecomposition.addFromString(getString("decompositionBlacklist","","Blacklist of items that should not be decomposable or decomposed into. Visit http://hardcore-ender-expansion.wikia.com/wiki/Configuration for syntax and examples.").setRequiresMcRestart(true).getString());
			StardustDecomposition.addFromString("minecraft:fire, ExtraUtilities:unstableingot, witchery:*");
			firstTimeGeneral = false;
		}
		
		if (config.hasChanged())config.save();
	}
	
	// utility methods
	
	private boolean getBoolValue(String key, boolean defaultValue){
		return config.get(currentCategory,key,defaultValue).getBoolean();
	}
	
	private Property getBool(String key, boolean defaultValue){
		return config.get(currentCategory,key,defaultValue);
	}
	
	private Property getBool(String key, boolean defaultValue, String comment){
		return config.get(currentCategory,key,defaultValue,comment);
	}
	
	private Property getInt(String key, int defaultValue){
		return config.get(currentCategory,key,defaultValue);
	}
	
	private Property getInt(String key, int defaultValue, String comment){
		return config.get(currentCategory,key,defaultValue,comment);
	}
	
	private Property getDecimal(String key, float defaultValue, String comment){
		return config.get(currentCategory,key,defaultValue,comment);
	}
	
	private Property getString(String key, String defaultValue, String comment){
		return config.get(currentCategory,key,defaultValue,comment);
	}
}

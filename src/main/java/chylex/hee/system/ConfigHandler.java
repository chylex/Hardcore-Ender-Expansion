package chylex.hee.system;
import java.io.File;
import java.util.List;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.block.BlockEnderGoo;
import chylex.hee.item.ItemTempleCaller;
import chylex.hee.mechanics.misc.StardustDecomposition;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.logging.Log;
import chylex.hee.system.sound.MusicManager;
import chylex.hee.system.update.UpdateNotificationManager;
import chylex.hee.world.biome.BiomeGenHardcoreEnd;
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
	
	public static List getGuiConfigElements(){
		List elements = new ConfigElement(instance.config.getCategory("client")).getChildElements();
		elements.addAll(new ConfigElement(instance.config.getCategory("general")).getChildElements());		
		return elements;
	}
	
	public static String getConfigString(){
		return instance.config.toString();
	}

	private final Configuration config;
	private boolean firstTimeGeneral = true;
	
	private ConfigHandler(Configuration config){
		this.config = config;
		this.config.load();
		
		config.moveProperty("general","enableMusic","client");
		config.moveProperty("general","enableUpdateNotifications","client");
		config.getCategory("general").remove("achievementIdStart");
	}
	
	@SubscribeEvent
	public void onConfigChanged(OnConfigChangedEvent e){
		if (e.modID.equals("HardcoreEnderExpansion"))HardcoreEnderExpansion.proxy.loadConfiguration();
	}
	
	// config loading
	
	@SideOnly(Side.CLIENT)
	private void loadClientConfig(){
		MusicManager.enableMusic = config.get("client","enableMusic",true).getBoolean(true);
		UpdateNotificationManager.enableNotifications = config.get("client","enableUpdateNotifications",true).getBoolean(true);
		
		if (config.hasChanged())config.save();
	}
	
	private void loadGeneralConfig(){
		ModCommonProxy.opMobs = config.get("general","overpoweredMobs",false).getBoolean(false);
		BlockEnderGoo.shouldBattleWater = config.get("general","gooBattlesWater",true).getBoolean(true);
		ItemTempleCaller.isEnabled = config.get("general","enableTempleCaller",true).getBoolean(true);
		BiomeGenHardcoreEnd.overrideMobLists = config.get("general","overrideBiomeMobs",false).getBoolean(false);
		Log.forceDebugEnabled = config.get("general","logDebuggingInfo",false).getBoolean(false);
		
		if (firstTimeGeneral){
			ModCommonProxy.achievementStartId = hideAndReturn(config.get("general","achievementStartId",3500)).getInt(3500);
			StardustDecomposition.addFromString(hideAndReturn(config.get("general","decompositionBlacklist","")).getString());
			StardustDecomposition.addFromString("minecraft:fire, ExtraUtilities:unstableingot, witchery:*");
			
			firstTimeGeneral = false;
		}
		
		if (config.hasChanged())config.save();
	}
	
	private Property hideAndReturn(Property prop){
		prop.setShowInGui(false);
		return prop;
	}
}

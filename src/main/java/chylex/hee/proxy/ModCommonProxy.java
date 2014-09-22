package chylex.hee.proxy;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;
import chylex.hee.mechanics.compendium.player.PlayerCompendiumData;
import chylex.hee.system.ConfigHandler;

public class ModCommonProxy{
	public static boolean opMobs;
	public static int achievementStartId;
	public static int renderIdObsidianSpecial,renderIdFlowerPot,renderIdSpookyLeaves,renderIdCrossedDecoration;
	
	public void loadConfiguration(){
		ConfigHandler.loadGeneral();
	}
	
	public EntityPlayer getClientSidePlayer(){
		return null;
	}
	
	public PlayerCompendiumData getClientCompendiumData(){
		return null;
	}
	
	public void registerRenderers(){}
	public void registerSidedEvents(){}
	public void openGui(String type){}
	
	public void reportLogException(String message){
		ServerConfigurationManager manager = MinecraftServer.getServer().getConfigurationManager();
		List<EntityPlayer> players = manager.playerEntityList;
		
		for(EntityPlayer player:players){
			if (manager.func_152596_g(player.getGameProfile()))player.addChatMessage(new ChatComponentText(message));
		}
	}
}

/*
Blacklist | unlocalized | registry
---------
Gravity Gun | 
*/
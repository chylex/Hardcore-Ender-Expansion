package chylex.hee.proxy;
import net.minecraft.entity.player.EntityPlayer;
import chylex.hee.game.ConfigHandler;
import chylex.hee.mechanics.compendium.player.PlayerCompendiumData;

public class ModCommonProxy{
	public static boolean opMobs, hardcoreEnderbacon;
	public static int achievementStartId;
	public static int renderIdObsidianSpecial, renderIdFlowerPot, renderIdSpookyLeaves, renderIdCrossedDecoration;
	
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
	public void sendMessage(MessageType msgType, int[] data){}
	
	public static enum MessageType{
		DEBUG_TITLE_SET, ENHANCEMENT_SLOT_RESET
	}
}
package chylex.hee.proxy;
import net.minecraft.entity.player.EntityPlayer;
import chylex.hee.game.ConfigHandler;

public class ModCommonProxy{
	public static boolean opMobs, hardcoreEnderbacon;
	public static int achievementStartId;
	public static int renderIdObsidianSpecial, renderIdFlowerPot, renderIdSpookyLeaves, renderIdCrossedDecoration, renderIdLootChest, renderIdGloomtorch;
	
	public void loadConfiguration(){
		ConfigHandler.loadGeneral();
	}
	
	public EntityPlayer getClientSidePlayer(){
		return null;
	}
	
	public void registerRenderers(){}
	public void registerSidedEvents(){}
	public void sendMessage(MessageType msgType, int...data){}
	
	public static enum MessageType{
		DEBUG_TITLE_SET, SPEED_UP_PLAYER, VIEW_MOD_CONTENT
	}
}
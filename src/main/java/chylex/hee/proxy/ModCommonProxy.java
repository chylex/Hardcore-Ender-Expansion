package chylex.hee.proxy;
import net.minecraft.entity.player.EntityPlayer;
import chylex.hee.system.ConfigHandler;

public class ModCommonProxy{
	public static boolean opMobs = false;
	public static int achievementStartId = 3500;
	public static short renderIdObsidianSpecial,renderIdFlowerPot,renderIdSpookyLeaves,renderIdInfestationRemedyCauldron;
	
	public void loadConfiguration(){
		ConfigHandler.loadGeneral();
	}
	
	public EntityPlayer getClientSidePlayer(){
		return null;
	}
	
	public void registerRenderers(){}
	public void registerSidedEvents(){}
	public void openGui(String type){}
}

/*
Blacklist | unlocalized | registry
---------
Gravity Gun | 
*/
package chylex.hee.proxy;
import net.minecraft.entity.player.EntityPlayer;
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
}

/*
Blacklist | unlocalized | registry
---------
Gravity Gun | 
*/
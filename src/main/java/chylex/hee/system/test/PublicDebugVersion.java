package chylex.hee.system.test;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import chylex.hee.system.logging.Log;
import chylex.hee.system.logging.Stopwatch;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public final class PublicDebugVersion{
	public static void setup(){
		FMLCommonHandler.instance().bus().register(new PublicDebugVersion());
		
		Log.forceDebugEnabled = true;
		Stopwatch.isEnabled = false;
	}
	
	private PublicDebugVersion(){}
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent e){ // TODO cleanup
		if (e.player.worldObj.isRemote)return;
		e.player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED+"Hardcore Ender Expansion semi-public test 1.7"));
		e.player.addChatMessage(new ChatComponentText("Report all issues to HEE GitHub issue tracker, with their titles prefixed '1.7-test'"));
		e.player.addChatMessage(new ChatComponentText("Do not redistribute the mod to anyone not approved for testing."));
	}
}

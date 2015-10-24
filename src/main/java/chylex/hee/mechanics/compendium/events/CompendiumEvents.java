package chylex.hee.mechanics.compendium.events;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import chylex.hee.game.save.SaveData;
import chylex.hee.game.save.types.player.CompendiumFile;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C19CompendiumData;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public final class CompendiumEvents{
	public static void register(){
		CompendiumEvents instance = new CompendiumEvents();
		MinecraftForge.EVENT_BUS.register(instance);
		FMLCommonHandler.instance().bus().register(instance);
	}
	
	public static CompendiumFile getPlayerData(EntityPlayer player){
		return SaveData.player(player,CompendiumFile.class);
	}
	
	private CompendiumEvents(){}
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent e){
		PacketPipeline.sendToPlayer(e.player,new C19CompendiumData(e.player));
	}
}

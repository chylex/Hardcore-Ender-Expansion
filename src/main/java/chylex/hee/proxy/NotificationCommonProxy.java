package chylex.hee.proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraftforge.common.ForgeHooks;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C09SimpleEvent;
import chylex.hee.packets.client.C09SimpleEvent.EventType;
import chylex.hee.system.abstractions.util.EntitySelector;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public class NotificationCommonProxy{
	protected static final String prefix = "[HEE] ";
	protected List<String> notifications = Collections.synchronizedList(new ArrayList<String>());
	
	public final void register(){
		FMLCommonHandler.instance().bus().register(this);
	}
	
	@SubscribeEvent
	public final void onPlayerLogin(PlayerLoggedInEvent e){
		onPlayerLogin(e.player);
	}
	
	public final void report(String message){
		notifications.add(prefix+message);
		tryDeliverNotifications();
	}
	
	public final void report(String message, boolean noPrefix){
		notifications.add((noPrefix ? "" : prefix)+message);
		tryDeliverNotifications();
	}
	
	protected final void deliverNotificationsToPlayer(EntityPlayer player){
		for(String notification:notifications)player.addChatMessage(ForgeHooks.newChatWithLinks(notification));
	}
	
	protected final void clearNotifications(){
		notifications.clear();
	}
	
	protected void tryDeliverNotifications(){
		ServerConfigurationManager manager = MinecraftServer.getServer().getConfigurationManager();
		if (manager == null)return;
		
		boolean delivered = false;
		
		for(EntityPlayer player:EntitySelector.players()){
			if (manager.func_152596_g(player.getGameProfile())){
				deliverNotificationsToPlayer(player);
				delivered = true;
			}
		}
		
		if (delivered)clearNotifications();
	}
	
	protected void onPlayerLogin(EntityPlayer player){
		if (MinecraftServer.getServer().getConfigurationManager().func_152596_g(player.getGameProfile())){
			deliverNotificationsToPlayer(player);
			clearNotifications();
			PacketPipeline.sendToPlayer(player,new C09SimpleEvent(EventType.CHECK_UPDATES));
		}
	}
}

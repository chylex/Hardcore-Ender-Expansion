package chylex.hee.mechanics.charms;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public final class CharmPouchHandler{
	private static CharmPouchHandler instance;
	
	public static void register(){
		FMLCommonHandler.instance().bus().register(instance = new CharmPouchHandler());
	}
	
	public static void setActivePouch(EntityPlayer player, ItemStack is){
		instance.activePouchIDs.put(player.getGameProfile().getId(),new CharmPouchInfo(is));
		instance.refresh = true;
	}
	
	public static CharmPouchInfo getActivePouch(EntityPlayer player){
		return instance.activePouchIDs.get(player.getGameProfile().getId());
	}
	
	private final Map<UUID,CharmPouchInfo> activePouchIDs = new HashMap<>();
	private final CharmEvents events = new CharmEvents();
	private boolean isHandlerActive;
	private boolean refresh;
	
	private CharmPouchHandler(){}
	
	@SubscribeEvent
	public void onServerTick(ServerTickEvent e){
		if (e.phase != Phase.START || !refresh)return;
		
		if (!isHandlerActive && !activePouchIDs.isEmpty()){
			isHandlerActive = true;
			
			MinecraftForge.EVENT_BUS.register(events);
			FMLCommonHandler.instance().bus().register(events);
		}
		else if (isHandlerActive && activePouchIDs.isEmpty()){
			isHandlerActive = false;

			MinecraftForge.EVENT_BUS.unregister(events);
			FMLCommonHandler.instance().bus().unregister(events);
		}
		
		refresh = false;
	}
	
	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent e){
		
	}
	
	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerLoggedOutEvent e){
		if (activePouchIDs.remove(e.player.getGameProfile().getId()) != null)refresh = true;
	}
}

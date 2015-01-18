package chylex.hee.mechanics.charms.handler;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.charms.CharmPouchInfo;
import chylex.hee.system.util.ItemUtil;

public final class CharmPouchHandler{
	private static CharmPouchHandler instance;
	
	public static void register(){
		FMLCommonHandler.instance().bus().register(instance = new CharmPouchHandler());
	}
	
	public static void setActivePouch(EntityPlayer player, ItemStack is){
		if (is == null)instance.activePouchIDs.remove(player.getGameProfile().getId());
		else instance.activePouchIDs.put(player.getGameProfile().getId(),new CharmPouchInfo(is));
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
		if (e.phase != Phase.END || !refresh)return;
		
		if (!isHandlerActive && !activePouchIDs.isEmpty()){
			isHandlerActive = true;
			
			MinecraftForge.EVENT_BUS.register(events);
			FMLCommonHandler.instance().bus().register(events);
		}
		else if (isHandlerActive && activePouchIDs.isEmpty()){
			isHandlerActive = false;

			events.onDisabled();
			MinecraftForge.EVENT_BUS.unregister(events);
			FMLCommonHandler.instance().bus().unregister(events);
		}
		
		refresh = false;
	}
	
	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent e){
		ItemStack[] mainInv = e.player.inventory.mainInventory;
		
		for(int a = 0; a < mainInv.length; a++){
			if (mainInv[a] != null && mainInv[a].getItem() == ItemList.charm_pouch && ItemUtil.getNBT(mainInv[a],false).getBoolean("isPouchActive")){
				setActivePouch(e.player,mainInv[a]);
				break;
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerLoggedOut(PlayerLoggedOutEvent e){
		if (activePouchIDs.remove(e.player.getGameProfile().getId()) != null)refresh = true;
	}
}

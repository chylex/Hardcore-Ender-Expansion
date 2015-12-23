package chylex.hee.mechanics.causatum;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.game.save.SaveData;
import chylex.hee.game.save.handlers.PlayerDataHandler;
import chylex.hee.game.save.types.player.CausatumFile;
import chylex.hee.mechanics.causatum.events.CausatumEventInstance;
import chylex.hee.mechanics.causatum.events.CausatumEventInstance.EventState;
import chylex.hee.mechanics.causatum.events.CausatumEventInstance.EventTypes;
import chylex.hee.system.abstractions.entity.EntitySelector;
import chylex.hee.system.util.GameRegistryUtil;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public final class CausatumEventHandler{
	private static final CausatumEventHandler instance = new CausatumEventHandler();
	
	public static void register(){
		GameRegistryUtil.registerEventHandler(instance);
	}
	
	public static boolean hasActiveEvent(EntityPlayer player){
		CausatumEventInstance inst = instance.activeEvents.get(PlayerDataHandler.getID(player));
		return inst != null && inst.getState() == EventState.WAITING;
	}
	
	public static boolean tryStartEvent(EntityPlayerMP player, EventTypes type, Object[] params){
		if (hasActiveEvent(player))return false;
		
		instance.activeEvents.put(PlayerDataHandler.getID(player),type.createEvent(player,params));
		return true;
	}
	
	public static boolean tryStartEvent(EntityPlayerMP player, EventTypes type){
		return tryStartEvent(player,type,ArrayUtils.EMPTY_OBJECT_ARRAY);
	}
	
	private Map<String,CausatumEventInstance> activeEvents = new HashMap<>(4);
	private int nextAttemptTimer;
	
	private CausatumEventHandler(){}
	
	@SubscribeEvent
	public void onServerTick(ServerTickEvent e){
		if (e.phase == Phase.END)return;
		
		if (!activeEvents.isEmpty()){
			for(Iterator<CausatumEventInstance> iter = activeEvents.values().iterator(); iter.hasNext();){
				CausatumEventInstance inst = iter.next();
				inst.updateEvent();
				
				if (inst.getState() == EventState.FINISHED)iter.remove();
			}
		}
		
		if (++nextAttemptTimer > 1200){ // 1 minute
			nextAttemptTimer = 0;
			
			for(EntityPlayerMP player:EntitySelector.players()){
				if (player.getRNG().nextInt(4) == 0 && !hasActiveEvent(player)){
					EventTypes type = SaveData.player(player,CausatumFile.class).findRandomEvent(player.getRNG());
					if (type != null)activeEvents.put(PlayerDataHandler.getID(player),type.createEvent(player));
				}
			}
		}
	}
}

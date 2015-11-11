package chylex.hee.mechanics.causatum;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import chylex.hee.mechanics.causatum.events.CausatumEventInstance;
import chylex.hee.mechanics.causatum.events.CausatumEventInstance.EventState;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public final class CausatumEventHandler{
	private static CausatumEventHandler instance;
	
	public static void register(){
		if (instance == null)FMLCommonHandler.instance().bus().register(instance = new CausatumEventHandler());
	}
	
	private Map<String,CausatumEventInstance> activeEvents = new HashMap<>(4);
	
	private CausatumEventHandler(){}
	
	@SubscribeEvent
	public void onServerTick(ServerTickEvent e){
		if (e.phase == Phase.END)return;
		
		if (!activeEvents.isEmpty()){
			for(Iterator<CausatumEventInstance> iter = activeEvents.values().iterator(); iter.hasNext();){
				if (iter.next().updateEvent() == EventState.FINISHED)iter.remove();
			}
		}
	}
}

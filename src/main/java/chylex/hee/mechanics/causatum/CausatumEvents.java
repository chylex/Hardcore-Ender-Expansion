package chylex.hee.mechanics.causatum;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;

public final class CausatumEvents{
	public static void register(){
		FMLCommonHandler.instance().bus().register(new CausatumEvents());
	}
	
	private int timer;
	
	private CausatumEvents(){}
	
	@SubscribeEvent
	public void onServerTick(ServerTickEvent e){
		if (e.phase == Phase.START || ++timer < 2400)return;
		timer = 0;
		
		// TODO
	}
}

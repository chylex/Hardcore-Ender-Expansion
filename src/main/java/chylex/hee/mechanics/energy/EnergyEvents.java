package chylex.hee.mechanics.energy;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;

public class EnergyEvents{
	public static void register(){
		FMLCommonHandler.instance().bus().register(new EnergyEvents());
	}
	
	@SubscribeEvent
	public void onWorldTick(WorldTickEvent e){
		if (e.phase != Phase.START || e.world.provider.dimensionId != 1)return;
		
		
	}
	
	private EnergyEvents(){}
}

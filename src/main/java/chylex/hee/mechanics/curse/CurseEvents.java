package chylex.hee.mechanics.curse;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class CurseEvents{
	public static void register(){
		MinecraftForge.EVENT_BUS.register(new CurseEvents());
	}
	
	@SubscribeEvent
	public void onLivingHurt(LivingHurtEvent e){
		
	}
	
	private CurseEvents(){}
}

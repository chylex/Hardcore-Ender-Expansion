package chylex.hee.system.update;
import chylex.hee.HardcoreEnderExpansion;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public final class UpdateNotificationManager{
	public static boolean enableNotifications = true;
	private long lastNotificationTime = -1;
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent e){
		if (enableNotifications){
			long time = System.currentTimeMillis();
			
			if (lastNotificationTime == -1 || time-lastNotificationTime > 1200000){
				lastNotificationTime = time;
				new UpdateThread(HardcoreEnderExpansion.modVersion).start();
			}
		}
	}
}

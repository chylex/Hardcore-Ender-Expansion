package chylex.hee.system.update;
import chylex.hee.HardcoreEnderExpansion;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public final class UpdateNotificationManager{
	public static boolean enableNotifications = true;
	public static boolean enableBuildCheck = true;
	private long lastNotificationTime = -1;
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent e){
		if (enableNotifications || enableBuildCheck){
			long time = System.currentTimeMillis();
			
			if (lastNotificationTime == -1 || time-lastNotificationTime > 14400000){
				lastNotificationTime = time;
				new UpdateThread(HardcoreEnderExpansion.modVersion).start();
			}
		}
	}
}

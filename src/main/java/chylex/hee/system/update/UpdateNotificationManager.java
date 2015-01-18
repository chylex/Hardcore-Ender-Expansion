package chylex.hee.system.update;
import chylex.hee.HardcoreEnderExpansion;
import com.google.common.base.Joiner;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public final class UpdateNotificationManager{
	public static boolean enableNotifications = true;
	public static boolean enableBuildCheck = true;
	
	public static String mcVersions = "?";
	public static String releaseDate = "?";
	
	public static synchronized void refreshUpdateData(VersionEntry version){
		mcVersions = Joiner.on(", ").join(version.mcVersions);
		releaseDate = version.releaseDate;
	}
	
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

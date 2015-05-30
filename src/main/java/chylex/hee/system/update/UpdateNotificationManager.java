package chylex.hee.system.update;
import chylex.hee.system.commands.HeeDebugCommand.HeeTest;
import com.google.common.base.Joiner;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;

public final class UpdateNotificationManager{
	public static boolean enableNotifications = true;
	public static boolean enableOneReportPerUpdate = false;
	public static boolean enableNewerMC = false;
	public static boolean enableBuildCheck = true;
	
	public static String mcVersions = "?";
	public static String releaseDate = "?";
	
	public static synchronized void refreshUpdateData(VersionEntry version){
		mcVersions = Joiner.on(", ").join(version.mcVersions);
		releaseDate = version.releaseDate;
	}
	
	private UpdateSavefile saveFile;
	private long lastNotificationTime = -1;
	
	public UpdateNotificationManager(){
		UpdateSavefile.prepare();
		saveFile = new UpdateSavefile();
		saveFile.load();
	}
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent e){
		if (enableNotifications || enableBuildCheck){
			long time = System.currentTimeMillis();
			
			if (lastNotificationTime == -1 || time-lastNotificationTime > 43200000){
				lastNotificationTime = time;
				new UpdateThread().start();
			}
		}
	}
	
	public static final HeeTest $debugTest = new HeeTest(){
		@Override
		public void run(String...args){
			new UpdateThread("https://dl.dropboxusercontent.com/u/17157118/update/hee_test.txt").start();
		}
	};
}

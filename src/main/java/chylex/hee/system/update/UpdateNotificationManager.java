package chylex.hee.system.update;
import java.util.Calendar;
import chylex.hee.system.commands.HeeDebugCommand.HeeTest;
import com.google.common.base.Joiner;

public final class UpdateNotificationManager{
	public static boolean enableNotifications = true;
	public static boolean enableOneReportPerUpdate = false;
	public static boolean enableNewerMC = false;
	public static boolean enableBuildCheck = true;
	
	public static String lastCheckedMod;
	
	public static String mcVersions = "?";
	public static String releaseDate = "?";
	
	public static synchronized void refreshUpdateData(VersionEntry version){
		mcVersions = Joiner.on(", ").join(version.mcVersions);
		releaseDate = version.releaseDate;
	}
	
	private static UpdateSavefile saveFile;
	
	public static void tryRunUpdateCheck(){
		if (enableNotifications || enableBuildCheck){
			if (saveFile == null){
				UpdateSavefile.prepare();
				saveFile = new UpdateSavefile();
				saveFile.load();
				lastCheckedMod = saveFile.newestModVersion;
			}
			
			long time = Calendar.getInstance().getTimeInMillis();
			
			if (time-saveFile.lastCheckTime > 86400000L){
				saveFile.lastCheckTime = time;
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

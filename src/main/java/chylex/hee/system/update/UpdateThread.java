package chylex.hee.system.update;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.command.CommandBase;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.codec.Charsets;
import org.apache.commons.io.IOUtils;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.system.logging.Log;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

class UpdateThread extends Thread{
	private static final String defaultUrl = "https://raw.githubusercontent.com/chylex/Hardcore-Ender-Expansion/master/UpdateNotificationDataV2.txt";
	
	private final String url;
	private final String modVersion;
	private final String mcVersion;
	
	UpdateThread(){
		this(defaultUrl);
	}
	
	UpdateThread(String url){
		this.url = url;
		this.modVersion = HardcoreEnderExpansion.modVersion;
		this.mcVersion = MinecraftForge.MC_VERSION;
		setPriority(MIN_PRIORITY);
		setDaemon(true);
	}
	
	@Override
	public void run(){
		try{
			// TODO 3 second delay
			
			JsonElement root = new JsonParser().parse(IOUtils.toString(new URL(url),Charsets.UTF_8));
			
			List<VersionEntry> versionList = new ArrayList<>();
			VersionEntry currentVersion = null, newestVersion = null, newestVersionForCurrentMC = null;
			int counter = -1, buildId = 0;
			
			String downloadURL = "http://hee.chylex.com/download";
			
			Log.debug("Detecting HEE updates...");
			
			for(Entry<String,JsonElement> entry:root.getAsJsonObject().entrySet()){
				if (entry.getKey().charAt(0) == '~'){
					switch(entry.getKey().substring(1)){
						case "URL": downloadURL = entry.getValue().getAsString(); break;
					}
				}
				else versionList.add(new VersionEntry(entry.getKey(),entry.getValue().getAsJsonObject()));
			}
			
			Collections.sort(versionList);
			
			for(VersionEntry version:versionList){
				Log.debug("Reading update data: $0",version.versionIdentifier);

				if (newestVersion == null)newestVersion = version;
				
				if (version.isSupportedByMC(mcVersion)){
					if (newestVersionForCurrentMC == null)newestVersionForCurrentMC = version;
					++counter;
				}
				
				if (version.modVersion.equals(modVersion)){
					buildId = version.buildId;
					UpdateNotificationManager.refreshUpdateData(version);
					currentVersion = version;
					break;
				}
			}
			
			if (currentVersion == null){
				Log.debug("In-dev version used, notifications disabled.");
				return;
			}
			else Log.debug("Done.");
			
			StringBuilder message = null;
			
			if (buildId != HardcoreEnderExpansion.buildId){
				message = new StringBuilder()
					.append(EnumChatFormatting.LIGHT_PURPLE).append(" [Hardcore Ender Expansion ").append(modVersion).append("]").append(EnumChatFormatting.RESET)
					.append("\n Caution, you are using a broken build that can cause critical crashes! Please, ").append(counter == 0 ? "redownload" : "redownload or update").append(" the mod.");
			}
			else if (counter > 0 && UpdateNotificationManager.enableNotifications){
				message = new StringBuilder()
					.append(EnumChatFormatting.LIGHT_PURPLE).append(" [Hardcore Ender Expansion ").append(modVersion).append("]").append(EnumChatFormatting.RESET)
					.append("\n Found update ").append(EnumChatFormatting.YELLOW).append(newestVersionForCurrentMC.modVersionName).append(EnumChatFormatting.RESET)
					.append(" for MC ").append(mcVersion).append(", released ").append(newestVersionForCurrentMC.releaseDate).append(".");
				
				if (counter >= 1){
					int days = DragonUtil.getDayDifference(Calendar.getInstance(),currentVersion.convertReleaseDate());
					int months = MathUtil.floor((days+8D)/30D); // ~22 days rounds up to a full month
					
					if (months > 0)message.append(" Your version is ").append(months).append(months == 1 ? " month" : " months").append(" old, and you are ");
					else message.append(" You are ");
					
					message.append(counter).append(counter == 1 ? " version behind." : " versions behind.");
				}
				
				if (UpdateNotificationManager.enableNewerMC && newestVersion != newestVersionForCurrentMC){
					message.append("\n\n There is also an update ").append(EnumChatFormatting.GREEN).append(newestVersion.modVersion).append(EnumChatFormatting.RESET)
						   .append(" for Minecraft ").append(CommandBase.joinNiceString(newestVersion.mcVersions)).append('.');
				}
			}
			else if (UpdateNotificationManager.enableNewerMC && UpdateNotificationManager.enableNotifications && newestVersion != newestVersionForCurrentMC){
				message = new StringBuilder()
					.append(EnumChatFormatting.LIGHT_PURPLE).append(" [Hardcore Ender Expansion ").append(modVersion).append("]").append(EnumChatFormatting.RESET)
					.append("\n Found a new version ").append(EnumChatFormatting.GREEN).append(newestVersion.modVersion).append(EnumChatFormatting.RESET)
					.append(" for Minecraft ").append(CommandBase.joinNiceString(newestVersion.mcVersions)).append(", released ").append(newestVersion.releaseDate)
					.append(".");
			}
			
			if (message != null){
				message.append("\n ").append(EnumChatFormatting.GOLD).append("Click to Download: ").append(downloadURL);
				message.append("\n ").append(EnumChatFormatting.GOLD).append("Notification Settings Command: ").append(EnumChatFormatting.RESET).append("/hee");
				for(String s:message.toString().split("\n"))HardcoreEnderExpansion.notifications.report(s,true);
			}
		}
		catch(UnknownHostException e){}
		catch(Exception e){
			Log.throwable(e,"Error detecting updates!");
		}
	}
}
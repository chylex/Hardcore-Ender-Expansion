package chylex.hee.system.update;
import java.io.BufferedReader;
import java.io.InputStreamReader;
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
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.system.logging.Log;
import chylex.hee.system.util.DragonUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

class UpdateThread extends Thread{
	private static final String url = "https://raw.githubusercontent.com/chylex/Hardcore-Ender-Expansion/master/UpdateNotificationDataV2.txt";
	
	private final String modVersion;
	private final String mcVersion;
	
	UpdateThread(String modVersion){
		this.modVersion = modVersion;
		this.mcVersion = MinecraftForge.MC_VERSION;
		setPriority(MIN_PRIORITY);
		setDaemon(true);
	}
	
	@Override
	public void run(){
		try{
			String line;
			StringBuilder build = new StringBuilder();
			
			BufferedReader read = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
			while((line = read.readLine()) != null)build.append(line).append('\n');
			read.close();
			
			JsonElement root = new JsonParser().parse(build.toString());
			List<VersionEntry> versionList = new ArrayList<>();
			VersionEntry currentVersion = null, newestVersion = null, newestVersionForCurrentMC = null;
			int counter = -1, buildId = 0;
			boolean isInDev = true;
			
			String downloadURL = "http://tinyurl.com/hc-ender-expansion";
			
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
					isInDev = false;
					buildId = version.buildId;
					UpdateNotificationManager.refreshUpdateData(version);
					currentVersion = version;
					break;
				}
			}
			
			if (isInDev){
				Log.debug("In-dev version used, notifications disabled.");
				return;
			}
			else Log.debug("Done.");
			
			if (buildId != HardcoreEnderExpansion.buildId){
				StringBuilder message = new StringBuilder()
					.append(EnumChatFormatting.LIGHT_PURPLE).append(" [Hardcore Ender Expansion ").append(modVersion).append("]").append(EnumChatFormatting.RESET)
					.append("\n Caution, you are using a broken build that can cause critical crashes! Please, redownload the mod, or update it if there is an update available.")
					.append("\n\n ").append(EnumChatFormatting.GRAY).append(downloadURL);
				
				for(String s:message.toString().split("\n"))HardcoreEnderExpansion.notifications.report(s,true);
			}
			else if (counter > 0 && UpdateNotificationManager.enableNotifications){
				StringBuilder message = new StringBuilder()
					.append(EnumChatFormatting.LIGHT_PURPLE).append(" [Hardcore Ender Expansion ").append(modVersion).append("]").append(EnumChatFormatting.RESET)
					.append("\n Found a new version ").append(EnumChatFormatting.GREEN).append(newestVersionForCurrentMC.modVersionName).append(EnumChatFormatting.RESET)
					.append(" for Minecraft ").append(mcVersion).append(", released ").append(newestVersionForCurrentMC.releaseDate)
					.append(". You are currently ").append(counter).append(" version").append(counter == 1 ? "" : "s").append(" behind.");
				
				if (counter > 1){
					int days = DragonUtil.getDayDifference(Calendar.getInstance(),currentVersion.convertReleaseDate());
					
					if (days > 60){
						message.append("\nThe version you are using is over a month old, please update soon.");
					}
					else if (days > 30){
						message.append("\n").append(EnumChatFormatting.RED)
							   .append("The version you are using is over 2 months old and is no longer supported, please update as soon as possible.");
					}
				}
				
				if (newestVersion != newestVersionForCurrentMC){
					message.append("\n\n There is also an update ").append(EnumChatFormatting.GREEN).append(newestVersion.modVersion).append(EnumChatFormatting.RESET)
						   .append(" for Minecraft ").append(CommandBase.joinNiceString(newestVersion.mcVersions)).append('.');
				}
				
				message.append("\n\n ").append(EnumChatFormatting.GRAY).append(downloadURL);
				
				for(String s:message.toString().split("\n"))HardcoreEnderExpansion.notifications.report(s,true);
			}
			else if (newestVersion != newestVersionForCurrentMC && UpdateNotificationManager.enableNotifications){
				StringBuilder message = new StringBuilder()
					.append(EnumChatFormatting.LIGHT_PURPLE).append(" [Hardcore Ender Expansion ").append(modVersion).append("]").append(EnumChatFormatting.RESET)
					.append("\n Found a new version ").append(EnumChatFormatting.GREEN).append(newestVersion.modVersion).append(EnumChatFormatting.RESET)
					.append(" for Minecraft ").append(CommandBase.joinNiceString(newestVersion.mcVersions)).append(", released ").append(newestVersion.releaseDate)
					.append(".");
				
				for(String s:message.toString().split("\n"))HardcoreEnderExpansion.notifications.report(s,true);
			}
		}
		catch(UnknownHostException e){}
		catch(Exception e){
			Log.throwable(e,"Error detecting updates!");
		}
	}
}
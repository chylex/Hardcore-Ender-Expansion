package chylex.hee.system.update;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import chylex.hee.system.util.DragonUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import cpw.mods.fml.relauncher.FMLInjectionData;

class UpdateThread extends Thread{
	private final String modVersion;
	private final String mcVersion;
	
	UpdateThread(String modVersion){
		this.modVersion = modVersion;
		this.mcVersion = (String)FMLInjectionData.data()[4];
		setPriority(MIN_PRIORITY);
	}
	
	@Override
	public void run(){
		try{
			String line;
			StringBuilder build = new StringBuilder();
			
			BufferedReader read = new BufferedReader(new InputStreamReader(new URL("https://dl.dropboxusercontent.com/u/17157118/update/hardcoreenderexpansion.txt").openStream()));
			while((line = read.readLine()) != null)build.append(line).append('\n');
			read.close();
			
			JsonElement root = new JsonParser().parse(build.toString());
			List<VersionEntry> versionList = new ArrayList<>();
			VersionEntry newestVersion = null,newestVersionForCurrentMC = null;
			int counter = -1;
			boolean isInDev = true;
			
			DragonUtil.info("Detecting HEE updates...");
			for(Entry<String,JsonElement> entry:root.getAsJsonObject().entrySet()){
				versionList.add(new VersionEntry(entry.getKey(),entry.getValue().getAsJsonObject()));
			}
			
			Collections.sort(versionList);
			
			for(VersionEntry version:versionList){
				DragonUtil.info("Reading update data: "+version.versionIdentifier);

				if (newestVersion == null)newestVersion = version;
				if (version.isSupportedByMC(mcVersion)){
					if (newestVersionForCurrentMC == null)newestVersionForCurrentMC = version;
					++counter;
				}
				
				if (version.modVersion.equals(modVersion)){
					isInDev = false;
					break;
				}
			}
			
			if (isInDev){
				DragonUtil.info("In-dev version used, notifications disabled.");
				return;
			}
			else DragonUtil.info("Done.");
			
			if (counter > 0){
				StringBuilder message = new StringBuilder()
					.append(EnumChatFormatting.LIGHT_PURPLE).append(" [Hardcore Ender Expansion ").append(modVersion).append("]").append(EnumChatFormatting.RESET)
					.append("\n Found a new version ").append(EnumChatFormatting.GREEN).append(newestVersionForCurrentMC.modVersionName).append(EnumChatFormatting.RESET)
					.append(" for Minecraft ").append(mcVersion).append(", released ").append(newestVersionForCurrentMC.releaseDate)
					.append(". You are currently ").append(counter).append(" version").append(counter == 1?"":"s").append(" behind.");
				
				if (newestVersion != newestVersionForCurrentMC){
					message.append("\n\n There is also an update ").append(EnumChatFormatting.GREEN).append(newestVersion.modVersion).append(EnumChatFormatting.RESET)
						   .append(" for Minecraft ").append(CommandBase.joinNiceString(newestVersion.mcVersions)).append('.');
				}
				
				message.append("\n\n ").append(EnumChatFormatting.GRAY).append("http://tinyurl.com/hc-ender-expansion");
				
				for(String s:message.toString().split("\n")){
					Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(s));
				}
			}
			else if (newestVersion != newestVersionForCurrentMC){
				StringBuilder message = new StringBuilder()
					.append(EnumChatFormatting.LIGHT_PURPLE).append(" [Hardcore Ender Expansion ").append(modVersion).append("]").append(EnumChatFormatting.RESET)
					.append("\n Found a new version ").append(EnumChatFormatting.GREEN).append(newestVersion.modVersion).append(EnumChatFormatting.RESET)
					.append(" for Minecraft ").append(CommandBase.joinNiceString(newestVersion.mcVersions)).append(", released ").append(newestVersion.releaseDate)
					.append(".");
				
				for(String s:message.toString().split("\n")){
					Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(s));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			DragonUtil.severe("Error detecting updates!");
		}
	}
}
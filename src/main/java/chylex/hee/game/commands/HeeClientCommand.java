package chylex.hee.game.commands;
import static net.minecraft.util.EnumChatFormatting.*;
import java.util.Optional;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.client.ClientCommandHandler;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.game.ConfigHandler;
import chylex.hee.system.logging.Log;
import chylex.hee.system.update.UpdateNotificationManager;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HeeClientCommand extends BaseCommand{
	public static void register(){
		ClientCommandHandler.instance.registerCommand(new HeeClientCommand());
	}
	
	private HeeClientCommand(){
		super("hee");
	}
	
	@Override
	public int getRequiredPermissionLevel(){
		return 0;
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender){
		return true;
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args){
		if (args.length == 1){
			if (args[0].equalsIgnoreCase("version")){
				sendMessage(sender,DARK_PURPLE+"Hardcore Ender Expansion");
				sendMessage(sender,LIGHT_PURPLE+"Version: "+RESET+HardcoreEnderExpansion.modVersion+"/"+HardcoreEnderExpansion.buildId);
				sendMessage(sender,LIGHT_PURPLE+"Mod file: "+RESET+(Log.isDeobfEnvironment ? "<deobf>" : HardcoreEnderExpansion.sourceFile.getName()));
				
				if (UpdateNotificationManager.enableNotifications || UpdateNotificationManager.enableBuildCheck){
					sendMessage(sender,LIGHT_PURPLE+"Available for: "+RESET+UpdateNotificationManager.mcVersions);
					sendMessage(sender,LIGHT_PURPLE+"Release date: "+RESET+UpdateNotificationManager.releaseDate);
				}
				else sendMessage(sender,GRAY+"Update information unavailable, notifications are disabled.");
			}
			else sendMessage(sender,LIGHT_PURPLE+" [HEE] "+RESET+"Command not found.");
		}
		else if (args.length == 2 && (args[1].equalsIgnoreCase("enable") || args[1].equalsIgnoreCase("disable"))){
			final String prop;
			
			if (args[0].equalsIgnoreCase("global"))prop = "enableUpdateNotifications";
			else if (args[0].equalsIgnoreCase("oncePerUpdate"))prop = "enableOneReportPerUpdate";
			else if (args[0].equalsIgnoreCase("checkMC"))prop = "enableNewerMC";
			else if (args[0].equalsIgnoreCase("brokenBuild"))prop = "enableBuildCheck";
			else prop = null;
			
			if (prop != null){
				Optional<IConfigElement> element = ConfigHandler.getGuiConfigElements().stream().filter(ele -> ele.getName().equals(prop) && ele.isProperty()).findAny();
				
				if (element.isPresent()){
					element.get().set(args[1].equalsIgnoreCase("enable"));
					sendMessage(sender,LIGHT_PURPLE+" [HEE] "+RESET+"Configuration updated.");
					HardcoreEnderExpansion.proxy.loadConfiguration(); // does not reload, just saves changes
				}
				else sendMessage(sender,LIGHT_PURPLE+" [HEE] "+RESET+"Could not find property "+prop+", please report this error.");
				
				return;
			}
		}
		else{
			sendMessage(sender,LIGHT_PURPLE+" [Hardcore Ender Expansion] client notifications");
			sendMessage(sender," /hee global <enable|disable>  "+getOnOff(UpdateNotificationManager.enableNotifications));
			sendMessage(sender,GRAY+"  Setting for all update notifications.");
			sendMessage(sender," /hee oncePerUpdate <enable|disable>  "+getOnOff(UpdateNotificationManager.enableOneReportPerUpdate));
			sendMessage(sender,GRAY+"  Each update is reported just once.");
			sendMessage(sender," /hee checkMC <enable|disable>  "+getOnOff(UpdateNotificationManager.enableNewerMC));
			sendMessage(sender,GRAY+"  Check updates for newer versions of Minecraft.");
			sendMessage(sender," /hee brokenBuild <enable|disable>  "+getOnOff(UpdateNotificationManager.enableBuildCheck));
			sendMessage(sender,GRAY+"  Broken build notifications (keep enabled if possible).");
			
			sendMessage(sender,LIGHT_PURPLE+" [Hardcore Ender Expansion] commands");
			sendMessage(sender," /hee version");
			sendMessage(sender,GRAY+"  Prints current version information.");
		}
	}
	
	private static final String getOnOff(boolean state){
		return "["+(state ? GREEN+"ON" : RED+"OFF")+RESET+"]";
	}
}

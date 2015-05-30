package chylex.hee.system.commands;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.system.ConfigHandler;
import chylex.hee.system.update.UpdateNotificationManager;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class HeeClientCommand extends BaseCommand{
	public static void register(){
		ClientCommandHandler.instance.registerCommand(new HeeClientCommand());
	}
	
	HeeClientCommand(){
		super("hee");
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args){
		if (args.length == 2 && (args[1].equalsIgnoreCase("enable") || args[1].equalsIgnoreCase("disable"))){
			String prop = null;
			
			if (args[0].equalsIgnoreCase("global"))prop = "enableUpdateNotifications";
			else if (args[0].equalsIgnoreCase("oncePerUpdate"))prop = "enableOneReportPerUpdate";
			else if (args[0].equalsIgnoreCase("checkMC"))prop = "enableNewerMC";
			else if (args[0].equalsIgnoreCase("brokenBuild"))prop = "enableBuildCheck";
			
			if (prop != null){
				boolean found = false;
				
				for(IConfigElement element:ConfigHandler.getGuiConfigElements()){
					if (element.getName().equals(prop) && element.isProperty()){
						element.set(args[1].equalsIgnoreCase("enable"));
						found = true;
						sendMessage(sender,EnumChatFormatting.LIGHT_PURPLE+"[HEE] "+EnumChatFormatting.RESET+"Configuration updated.");
						break;
					}
				}
				
				if (found)HardcoreEnderExpansion.proxy.loadConfiguration(); // does not reload, just saves changes
				else sendMessage(sender,EnumChatFormatting.LIGHT_PURPLE+"[HEE] "+EnumChatFormatting.RESET+"Could not find property "+prop+", please report this error.");
				
				return;
			}
		}
		
		sendMessage(sender,EnumChatFormatting.LIGHT_PURPLE+"[Hardcore Ender Expansion] client notifications");
		sendMessage(sender,"/hee global <enable|disable>  "+getOnOff(UpdateNotificationManager.enableNotifications));
		sendMessage(sender,EnumChatFormatting.GRAY+"Setting for all update notifications.");
		sendMessage(sender,"/hee oncePerUpdate <enable|disable>  "+getOnOff(UpdateNotificationManager.enableOneReportPerUpdate));
		sendMessage(sender,EnumChatFormatting.GRAY+"Each update is reported just once.");
		sendMessage(sender,"/hee checkMC <enable|disable>  "+getOnOff(UpdateNotificationManager.enableNewerMC));
		sendMessage(sender,EnumChatFormatting.GRAY+"Check updates for newer versions of Minecraft.");
		sendMessage(sender,"/hee brokenBuild <enable|disable>  "+getOnOff(UpdateNotificationManager.enableBuildCheck));
		sendMessage(sender,EnumChatFormatting.GRAY+"Broken build notifications (keep enabled if possible).");
	}
	
	private String getOnOff(boolean state){
		return "["+(state ? EnumChatFormatting.GREEN+"ON" : EnumChatFormatting.RED+"OFF")+EnumChatFormatting.RESET+"]";
	}
}

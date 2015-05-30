package chylex.hee.system.commands;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
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
		if (args.length > 0){
			
		}
		
		sendMessage(sender,EnumChatFormatting.LIGHT_PURPLE+"[Hardcore Ender Expansion] client notifications");
		sendMessage(sender,"/hee global <enable|disable>");
		sendMessage(sender,EnumChatFormatting.GRAY+"Setting for all update notifications.");
		sendMessage(sender,"/hee checkMC <enable|disable>");
		sendMessage(sender,EnumChatFormatting.GRAY+"Check updates for newer versions of Minecraft.");
		sendMessage(sender,"/hee brokenBuild <enable|disable>");
		sendMessage(sender,EnumChatFormatting.GRAY+"Broken build notifications (keep enabled if possible).");
	}
}

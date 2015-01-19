package chylex.hee.system.commands;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import chylex.hee.mechanics.misc.Baconizer;

abstract class HeeCommand extends CommandBase{
	private final String cmdName;
	
	HeeCommand(String cmdName){
		this.cmdName = cmdName;
	}
	
	@Override
	public String getName(){
		return cmdName;
	}

	@Override
	public String getUsage(ICommandSender sender){
		return "/"+cmdName;
	}
	
	@Override
	public int getRequiredPermissionLevel(){
		return 4;
	}
	
	protected final void sendMessage(ICommandSender receiver, String message){
		receiver.addChatMessage(new ChatComponentText(Baconizer.sentence(message)));
	}
}

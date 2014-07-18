package chylex.hee.system.commands;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class HeeDebugCommand extends CommandBase{
	private final HeeDebugCommandProcessor processor = new HeeDebugCommandProcessor();
	
	@Override
	public String getCommandName(){
		return "heedebug";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender){
		return "/heedebug";
	}

	@Override
	public List getCommandAliases(){
		return null;
	}

	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring){
		processor.onCommand(icommandsender,astring);
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender icommandsender){
		return true;
	}
	
	@Override
	public int getRequiredPermissionLevel(){
		return 4;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender icommandsender, String[] astring){
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] astring, int i){
		return false;
	}
	
	@Override
	public int compareTo(Object obj){
		return 0;
	}
}

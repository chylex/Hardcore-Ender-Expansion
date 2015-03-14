package chylex.hee.system.commands;
import net.minecraft.command.ICommandSender;

abstract class SubCommand{
	final String name;
	final String arguments;
	final byte argCount;
	final boolean requiresPlayer;
	
	SubCommand(String name, int argCount, boolean requiresPlayer){
		this(name,null,argCount,requiresPlayer);
	}
	
	SubCommand(String name, String arguments, int argCount, boolean requiresPlayer){
		this.name = name;
		this.arguments = arguments;
		this.argCount = (byte)argCount;
		this.requiresPlayer = requiresPlayer;
	}
	
	abstract void run(ICommandSender sender, String[] args);
}

package chylex.hee.system.logging;
import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Log{
	static final Logger logger = LogManager.getLogger("HardcoreEnderExpansion");
	
	public static final boolean showDebug;
	public static final boolean showInfo;
	public static final boolean showWarnings;
	public static final boolean showErrors;
	
	static{
		showDebug = ((Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment")).booleanValue();
		showInfo = true;
		showWarnings = true;
		showErrors = true;
	}
	
	public static void debug(String message, Object...data){
		if (showDebug)logger.debug(getMessage(message,data));
	}
	
	public static void info(String message, Object...data){
		if (showInfo)logger.info(getMessage(message,data));
	}
	
	public static void warn(String message, Object...data){
		if (showWarnings)logger.warn(getMessage(message,data));
	}
	
	public static void error(String message, Object...data){
		if (showErrors)logger.error(getMessage(message,data));
	}
	
	public static void throwable(Throwable throwable, String message, Object...data){
		if (showErrors){
			logger.catching(Level.ERROR,throwable);
			logger.error(getMessage(message,data));
		}
	}
	
	private static String getMessage(String message, Object...data){
		for(int a = data.length-1; a >= 0; a--)message = message.replace("$"+a,data[a] == null ? "null" : String.valueOf(data[a]));
		return message;
	}
}

package chylex.hee.system.logging;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;
import chylex.hee.HardcoreEnderExpansion;

public final class Log{
	static final Logger logger = LogManager.getLogger("HardcoreEnderExpansion");
	
	public static final boolean isDeobfEnvironment;
	public static boolean forceDebugEnabled;
	private static FastDateFormat dateFormat = FastDateFormat.getInstance("HH:mm:ss");
	private static long lastLogReport = -1;
	private static byte obfEnvironmentWarning = 0;
	
	static{
		isDeobfEnvironment = ((Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment")).booleanValue();
		
		if (isDeobfEnvironment && MinecraftServer.getServer() instanceof DedicatedServer){
			try(FileOutputStream fos = new FileOutputStream(new File("eula.txt"))){
				Properties properties = new Properties();
				properties.setProperty("eula","true");
				properties.store(fos,"Screw your EULA, I don't want that stuff in my workspace.");
			}catch(Exception e){}
		}
	}
	
	public static void initializeDebug(){
		if (forceDebugEnabled || isDeobfEnvironment){
			Display.setTitle(new StringBuilder().append(Display.getTitle()).append(" - HardcoreEnderExpansion - ").append(isDeobfEnvironment ? "dev" : "debug").append(' ').append(HardcoreEnderExpansion.modVersion).toString());
		}
	}
	
	public static boolean isDebugEnabled(){
		return forceDebugEnabled || isDeobfEnvironment;
	}

	/** Use $x where x is between 0 and data.length-1 to input variables. */
	public static void debug(String message, Object...data){
		if (forceDebugEnabled || isDeobfEnvironment)logger.info(getMessage(message,data));
		
		if (forceDebugEnabled && !isDeobfEnvironment && ++obfEnvironmentWarning >= 30){
			logger.warn(getMessage("Detected obfuscated environment, don't forget to disable logging debug info after you are done debugging!"));
			obfEnvironmentWarning = 0;
		}
	}

	/** Use $x where x is between 0 and data.length-1 to input variables. */
	public static void info(String message, Object...data){
		logger.info(getMessage(message,data));
	}

	/** Use $x where x is between 0 and data.length-1 to input variables. */
	public static void warn(String message, Object...data){
		logger.warn(getMessage(message,data));
	}

	/** Use $x where x is between 0 and data.length-1 to input variables. */
	public static void error(String message, Object...data){
		logger.error(getMessage(message,data));
	}

	/** Use $x where x is between 0 and data.length-1 to input variables. */
	public static void reportedError(String message, Object...data){
		logger.error(getMessage(message,data));
		HardcoreEnderExpansion.notifications.report("["+dateFormat.format(Calendar.getInstance().getTime())+"] "+message+" Check the log for stack trace to report.");
	}

	/** Use $x where x is between 0 and data.length-1 to input variables. */
	public static void throwable(Throwable throwable, String message, Object...data){
		logger.catching(Level.ERROR,throwable);
		logger.error(getMessage(message,data));
		
		if (lastLogReport == -1 || TimeUnit.NANOSECONDS.toSeconds(System.nanoTime()-lastLogReport) >= 10)HardcoreEnderExpansion.notifications.report("["+dateFormat.format(Calendar.getInstance().getTime())+"] "+message+" Check the log for stack trace to report.");
		lastLogReport = System.nanoTime();
	}
	
	private static String getMessage(String message, Object...data){
		for(int a = data.length-1; a >= 0; a--)message = message.replace("$"+a,data[a] == null ? "null" : String.valueOf(data[a]));
		return message;
	}
}

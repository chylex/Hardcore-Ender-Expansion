package chylex.hee.system.logging;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import chylex.hee.HardcoreEnderExpansion;

public final class Log{
	static final Logger logger = LogManager.getLogger("HardcoreEnderExpansion");
	
	public static boolean forceDebugEnabled;
	private static boolean isDeobfEnvironment;
	private static long lastLogReport = -1;
	
	static{
		isDeobfEnvironment = ((Boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment")).booleanValue();
		
		if (isDeobfEnvironment && MinecraftServer.getServer() instanceof DedicatedServer){
			File eula = new File("eula.txt");
			FileOutputStream fos = null;

			try{
				fos = new FileOutputStream(eula);
				Properties properties = new Properties();
				properties.setProperty("eula","true");
				properties.store(fos,"Screw your EULA, I don't want that stuff in my workspace.");
			}catch(Exception e){}
			finally{
				IOUtils.closeQuietly(fos);
			}
		}
	}
	
	public static boolean isDebugEnabled(){
		return forceDebugEnabled || isDeobfEnvironment;
	}
	
	public static void debug(String message, Object...data){
		if (forceDebugEnabled || isDeobfEnvironment)logger.info(getMessage(message,data));
	}
	
	public static void info(String message, Object...data){
		logger.info(getMessage(message,data));
	}
	
	public static void warn(String message, Object...data){
		logger.warn(getMessage(message,data));
	}
	
	public static void error(String message, Object...data){
		logger.error(getMessage(message,data));
	}
	
	public static void throwable(Throwable throwable, String message, Object...data){
		logger.catching(Level.ERROR,throwable);
		logger.error(getMessage(message,data));
		
		if (lastLogReport == -1 || TimeUnit.NANOSECONDS.toSeconds(System.nanoTime()-lastLogReport) >= 10)HardcoreEnderExpansion.proxy.reportLogException("[Hardcore Ender Expansion] "+message+" Check the log for stack trace to report.");
		lastLogReport = System.nanoTime();
	}
	
	private static String getMessage(String message, Object...data){
		for(int a = data.length-1; a >= 0; a--)message = message.replace("$"+a,data[a] == null ? "null" : String.valueOf(data[a]));
		return message;
	}
}

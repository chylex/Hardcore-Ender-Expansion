package chylex.hee.api.message.utils;
import chylex.hee.system.logging.Log;

public final class MessageLogger{
	private static final String prefixState = "[HEE-IMC] [STATE] ";
	private static final String prefixRun = "[HEE-IMC] [RUN] ";
	private static final String prefixError = "[HEE-IMC] [ERROR] ";
	private static final String prefixOk = "[HEE-IMC] [OK] ";
	private static final String prefixWarn = "[HEE-IMC] [WARN] ";
	private static final String prefixFail = "[HEE-IMC] [FAIL] ";
	
	public static void logState(String message, Object...data){
		Log.info(prefixState+message,data);
	}
	
	public static void logRun(String message, Object...data){
		Log.info(prefixRun+message,data);
	}
	
	public static void logError(String message, Object...data){
		Log.error(prefixError+message,data);
	}
	
	public static void logOk(String message, Object...data){
		Log.info(prefixOk+message,data);
	}
	
	public static void logWarn(String message, Object...data){
		Log.info(prefixWarn+message,data);
	}
	
	public static void logFail(String message, Object...data){
		Log.info(prefixFail+message,data);
	}
	
	private MessageLogger(){}
}

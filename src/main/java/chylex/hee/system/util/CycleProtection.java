package chylex.hee.system.util;
import chylex.hee.system.logging.Log;

public final class CycleProtection{
	public static boolean suppressLogging = false;
	private static int counter = 128;
	
	public static void setCounter(int counter){
		CycleProtection.counter = counter;
	}
	
	public static boolean proceed(){
		if (--counter >= 0)return true;
		if (suppressLogging)return false;
		
		if (Log.isDebugEnabled())Thread.dumpStack();
		Log.warn("Broke out of possible infinite cycle. "+(Log.isDebugEnabled() ? "Printed stack trace above." : "Stack trace is only printed in debug mode."));
		return false;
	}
	
	public static boolean failed(){
		boolean failed = counter == -1;
		counter = 128;
		return failed;
	}
	
	public static void reset(){
		counter = 128;
	}
}

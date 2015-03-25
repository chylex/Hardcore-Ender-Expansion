package chylex.hee.system.test;

public enum RunTime{
	PREINIT,
	
	INIT,
	
	POSTINIT,
	
	LOADCOMPLETE,
	
	/**
	 * In-game tests can use the trigger property to be only called with a special trigger.
	 */
	INGAME
}

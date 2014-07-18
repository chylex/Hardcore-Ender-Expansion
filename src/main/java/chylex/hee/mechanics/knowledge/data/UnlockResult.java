package chylex.hee.mechanics.knowledge.data;

public enum UnlockResult{
	BAD_LUCK, NO_COMPENDIUM, NO_PAPER, NOTHING_TO_UNLOCK(false), SUCCESSFUL;
	
	public final boolean stopTrying;
	
	UnlockResult(){
		stopTrying = true;
	}
	
	UnlockResult(boolean stopTrying){
		this.stopTrying = stopTrying;
	}
}

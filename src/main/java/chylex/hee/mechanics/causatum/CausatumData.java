package chylex.hee.mechanics.causatum;

public final class CausatumData{
	public enum Actions{
		;
		
		public final boolean canRepeat;
		public final short levelIncrease;
		
		private Actions(boolean canRepeat, int levelIncrease){
			this.canRepeat = canRepeat;
			this.levelIncrease = (short)levelIncrease;
		}
	}
	
	public enum Events{
		;
		
		public final short requiredLevel;
		
		private Events(int requiredLevel){
			this.requiredLevel = (short)requiredLevel;
		}
	}
	
	private CausatumData(){}
}

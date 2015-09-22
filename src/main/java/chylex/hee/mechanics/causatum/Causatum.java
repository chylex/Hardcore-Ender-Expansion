package chylex.hee.mechanics.causatum;
import net.minecraft.entity.player.EntityPlayer;
import chylex.hee.game.save.SaveData;
import chylex.hee.game.save.types.player.CausatumFile;

public final class Causatum{
	public static void trigger(EntityPlayer player, Actions action){
		SaveData.<CausatumFile>player(player,CausatumFile.class).trigger(action);
	}
	
	public enum Progress{
		START
	}
	
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
	
	private Causatum(){}
}

package chylex.hee.mechanics.causatum;
import net.minecraft.entity.player.EntityPlayer;
import chylex.hee.game.save.SaveData;
import chylex.hee.game.save.types.player.CausatumFile;

public final class Causatum{
	public static boolean progress(EntityPlayer player, Progress nextStage){
		return SaveData.player(player,CausatumFile.class).tryProgress(nextStage);
	}
	
	public static boolean progress(EntityPlayer player, Progress nextStage, Actions advanceAction){
		CausatumFile file = SaveData.player(player,CausatumFile.class);
		return file.tryProgress(nextStage) && file.tryTrigger(advanceAction);
	}
	
	public static boolean trigger(EntityPlayer player, Actions action){
		return SaveData.player(player,CausatumFile.class).tryTrigger(action);
	}
	
	public enum Progress{
		INITIAL, ENDERMAN_KILLED, INTO_THE_END
	}
	
	public enum Actions{
		STAGE_ADVANCE_TO_ENDERMAN_KILLED(false,100); // TODO 100
		
		public final boolean canRepeat;
		public final short levelIncrease;
		
		private Actions(boolean canRepeat, int levelIncrease){
			this.canRepeat = canRepeat;
			this.levelIncrease = (short)levelIncrease;
		}
	}
	
	public enum Events{
		STAGE_ADVANCE_TO_ENDERMAN_KILLED(-1);
		
		public final short requiredLevel;
		
		private Events(int requiredLevel){
			this.requiredLevel = (short)requiredLevel;
		}
	}
	
	private Causatum(){}
}

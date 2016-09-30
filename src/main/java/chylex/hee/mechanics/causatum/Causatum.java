package chylex.hee.mechanics.causatum;
import net.minecraft.entity.player.EntityPlayer;
import chylex.hee.game.save.SaveData;
import chylex.hee.game.save.types.player.CausatumFile;

public final class Causatum{
	public static boolean progress(EntityPlayer player, Progress nextStage){
		return SaveData.player(player, CausatumFile.class).tryProgress(nextStage);
	}
	
	public static boolean progress(EntityPlayer player, Progress nextStage, Actions advanceAction){
		CausatumFile file = SaveData.player(player, CausatumFile.class);
		return file.tryProgress(nextStage) && file.tryTrigger(advanceAction);
	}
	
	public static boolean trigger(EntityPlayer player, Actions action){
		return SaveData.player(player, CausatumFile.class).tryTrigger(action);
	}
	
	public static boolean hasReached(EntityPlayer player, Progress stage){
		return SaveData.player(player, CausatumFile.class).getStage().ordinal() >= stage.ordinal();
	}
	
	public enum Progress{
		INITIAL, ENDERMAN_KILLED, INTO_THE_END
	}
	
	public enum Actions{ // TODO update
		STAGE_ADVANCE_TO_ENDERMAN_KILLED(false, 100),
		
		KILL_ENDERMAN(true, 20);
		
		public final boolean canRepeat;
		public final short levelIncrease;
		
		private Actions(boolean canRepeat, int levelIncrease){
			this.canRepeat = canRepeat;
			this.levelIncrease = (short)levelIncrease;
		}
	}
	
	private Causatum(){}
}

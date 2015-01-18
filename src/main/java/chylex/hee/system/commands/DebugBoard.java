package chylex.hee.system.commands;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.world.World;

public class DebugBoard{
	private static Scoreboard sb;
	private static ScoreObjective objective;
	
	static void startDebug(World world){
		if (sb != null)stopDebug();
		sb = world.getScoreboard();
		objective = sb.getObjective("HEE_DEBUG");
		if (objective == null){
			objective = sb.addScoreObjective("HEE_DEBUG",IScoreObjectiveCriteria.DUMMY);
			objective.setDisplayName("HEE Debug");
		}
		sb.setObjectiveInDisplaySlot(1,objective);
	}
	
	static void stopDebug(){
		if (sb != null){
			sb.func_96519_k(objective);
			sb = null;
			objective = null;
		}
	}
	
	public static void updateValue(String name, int newValue){
		if (sb == null)return;
		sb.getValueFromObjective(name,objective).setScorePoints(newValue);
	}
}

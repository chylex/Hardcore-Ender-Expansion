package chylex.hee.game.commands;
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
			objective = sb.addScoreObjective("HEE_DEBUG", IScoreObjectiveCriteria.field_96641_b);
			objective.setDisplayName("HEE Debug");
		}
		
		sb.func_96530_a(1, objective);
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
		sb.func_96529_a(name, objective).setScorePoints(newValue);
	}
}

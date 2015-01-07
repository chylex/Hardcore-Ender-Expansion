package chylex.hee.entity.boss.dragon.managers;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.system.commands.DebugBoard;
import chylex.hee.system.util.MathUtil;

public class DragonDebugManager{
	public static void updateBoard(EntityBossDragon dragon){
		DebugBoard.updateValue("LoadTimer",dragon.loadTimer);
		DebugBoard.updateValue("SpawnCooldown",dragon.spawnCooldown);
		DebugBoard.updateValue("HealthPerc",dragon.attacks.getHealthPercentage());
		DebugBoard.updateValue("HasTarget",dragon.target == null ? 0 : 1);
		DebugBoard.updateValue("NextAttackTicks",dragon.nextAttackTicks);
		DebugBoard.updateValue("Rwrd_Handicap",MathUtil.floor(dragon.rewards.getExtraHandicap()));
		DebugBoard.updateValue("Rwrd_FinalDiff",dragon.rewards.getFinalDifficulty());
	}
}

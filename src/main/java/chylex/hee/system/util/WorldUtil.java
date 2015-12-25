package chylex.hee.system.util;
import net.minecraft.world.World;

public final class WorldUtil{
	public enum GameRule{
		MOB_GRIEFING("mobGriefing"),
		DO_TILE_DROPS("doTileDrops"),
		DO_MOB_LOOT("doMobLoot"),
		KEEP_INVENTORY("keepInventory");
		
		private final String ruleName;
		
		private GameRule(String ruleName){
			this.ruleName = ruleName;
		}
	}
	
	public static boolean getRuleBool(World world, GameRule rule){
		return world.getGameRules().getGameRuleBooleanValue(rule.ruleName);
	}
	
	private WorldUtil(){}
}

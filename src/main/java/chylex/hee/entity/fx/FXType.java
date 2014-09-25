package chylex.hee.entity.fx;

public final class FXType{
	public enum Basic{
		ESSENCE_ALTAR_SMOKE,
		LASER_BEAM_DESTROY,
		SPOOKY_LOG_DECAY,
		SPOOKY_LEAVES_DECAY,
		DUNGEON_PUZZLE_BURN,
		GEM_LINK,
		GEM_TELEPORT_TO,
		ENDER_PEARL_FREEZE,
		IGNEOUS_ROCK_MELT,
		ENDERMAN_BLOODLUST_TRANSFORMATION,
		ENDER_GUARDIAN_TELEPORT,
		LOUSE_ARMOR_HIT;
		
		public static FXType.Basic[] values = values();
	}
	
	public enum Entity{
		CHARM_CRITICAL,
		CHARM_WITCH,
		CHARM_BLOCK_EFFECT,
		CHARM_LAST_RESORT,
		GEM_TELEPORT_FROM,
		ORB_TRANSFORMATION,
		LOUSE_REGEN;
		
		public static FXType.Entity[] values = values();
	}
	
	public enum Line{
		DRAGON_EGG_TELEPORT,
		CHARM_SLAUGHTER_IMPACT,
		CHARM_DAMAGE_REDIRECTION,
		LOUSE_HEAL_ENTITY;
		
		public static FXType.Line[] values = values();
	}
}

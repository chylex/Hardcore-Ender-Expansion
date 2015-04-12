package chylex.hee.mechanics.causatum;

public enum CausatumMeters{
	OVERWORLD_ENDERMAN_DAMAGE(1500),
	OVERWORLD_PORTAL(500),
	DRAGON_KILL_PARTICIPATION(1000),
	DRAGON_DAMAGE(5000),
	ENDER_EYE_DAMAGE(2800),
	END_ORE_MINING(2000),
	END_SPAWNER_MINING(3200),
	END_MOB_DAMAGE(15000),
	END_ENERGY(1500),
	END_EXPLORATION(4000),
	ITEM_USAGE(2000);
	
	public final int limit;
	
	CausatumMeters(int limit){
		this.limit = limit;
	}
}

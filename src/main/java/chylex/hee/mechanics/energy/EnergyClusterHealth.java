package chylex.hee.mechanics.energy;
import chylex.hee.system.collections.WeightedList;
import chylex.hee.system.collections.weight.IWeightProvider;

public enum EnergyClusterHealth implements IWeightProvider{
	HEALTHY(0x54ff54, 10, 15, 1F, 90, "energy.status.healthy"),
	WEAKENED(0xffc454, 7, 32, 0.75F, 40, "energy.status.weakened"),
	TIRED(0xff7a54, 3, 58, 0.4F, 10, "energy.status.tired"),
	UNSTABLE(0xff5454, 1, -1, 0F, 0, "energy.status.unstable");
	
	public static final EnergyClusterHealth[] values = values();
	public static final WeightedList<EnergyClusterHealth> spawnWeightedList = new WeightedList<>(values);
	
	public final int color;
	public final byte spawnWeight;
	public final byte regenTimer;
	public final float regenMultiplier;
	public final byte chanceToWeaken;
	public final String translationText;
	
	EnergyClusterHealth(int color, int spawnWeight, int regenTimer, float regenMultiplier, int chanceToWeaken, String translationText){
		this.color = color;
		this.spawnWeight = (byte)spawnWeight;
		this.regenTimer = (byte)regenTimer;
		this.regenMultiplier = regenMultiplier;
		this.chanceToWeaken = (byte)chanceToWeaken;
		this.translationText = translationText;
	}

	@Override
	public int getWeight(){
		return spawnWeight;
	}
}

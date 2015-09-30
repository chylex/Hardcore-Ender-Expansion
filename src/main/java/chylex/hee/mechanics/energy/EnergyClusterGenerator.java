package chylex.hee.mechanics.energy;
import java.util.Random;
import java.util.function.Function;
import chylex.hee.world.util.Range;

public final class EnergyClusterGenerator{
	public static final EnergyClusterGenerator
	
		creative = new EnergyClusterGenerator(
			new Range(0,1000),
			new Range(0,1000),
			rand -> {
				int health = rand.nextInt(100);
				
				return health < 30 ? EnergyClusterHealth.HEALTHY :
					   health < 55 ? EnergyClusterHealth.WEAKENED :
					   health < 75 ? EnergyClusterHealth.TIRED :
					   health < 90 ? EnergyClusterHealth.DAMAGED : EnergyClusterHealth.UNSTABLE;
			}
		),
		
		overworld = new EnergyClusterGenerator(
			new Range(0,7),
			new Range(8,13),
			rand -> {
				int chance = rand.nextInt(4);
				return chance == 0 ? EnergyClusterHealth.HEALTHY : chance == 1 ? EnergyClusterHealth.TIRED : EnergyClusterHealth.WEAKENED;
			}
		),
		
		stronghold = new EnergyClusterGenerator(
			new Range(4,23),
			new Range(18,35),
			rand -> {
				int health = rand.nextInt(100);
				
				return health < 35 ? EnergyClusterHealth.DAMAGED :
					   health < 70 ? EnergyClusterHealth.TIRED :
					   health < 95 ? EnergyClusterHealth.WEAKENED : EnergyClusterHealth.HEALTHY;
			}
		),
		
		energyShrine = new EnergyClusterGenerator(
			new Range(60,150),
			new Range(110,220),
			rand -> EnergyClusterHealth.HEALTHY
		);
	
	private final Range unitsSpawned;
	private final Range unitsMax;
	private final Function<Random,EnergyClusterHealth> healthGen;
	
	EnergyClusterGenerator(Range unitsSpawned, Range unitsMax, Function<Random,EnergyClusterHealth> healthGen){
		this.unitsSpawned = unitsSpawned;
		this.unitsMax = unitsMax;
		this.healthGen = healthGen;
	}
	
	public EnergyClusterData generate(Random rand){
		float max = EnergyValues.unit*(unitsMax.min+rand.nextFloat()*(unitsMax.max-unitsMax.min));
		float current = EnergyValues.unit*(unitsSpawned.min+rand.nextFloat()*(unitsSpawned.max-unitsSpawned.min));
		return new EnergyClusterData(Math.min(current,max),max,healthGen.apply(rand));
	}
}

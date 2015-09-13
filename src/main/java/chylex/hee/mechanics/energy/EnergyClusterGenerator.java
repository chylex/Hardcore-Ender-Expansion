package chylex.hee.mechanics.energy;
import java.util.Random;
import java.util.function.Function;
import chylex.hee.world.structure.util.Range;

public final class EnergyClusterGenerator{
	public static final EnergyClusterGenerator
	
		creative = new EnergyClusterGenerator(
			new Range(0,50),
			new Range(0,50),
			rand -> {
				int health = rand.nextInt(100);
				
				return health < 40 ? EnergyClusterHealth.HEALTHY :
					   health < 70 ? EnergyClusterHealth.WEAKENED :
					   health < 85 ? EnergyClusterHealth.TIRED :
					   health < 95 ? EnergyClusterHealth.DAMAGED : EnergyClusterHealth.UNSTABLE;
			}
		),
		
		overworld = new EnergyClusterGenerator(
			new Range(0,7),
			new Range(8,13),
			rand -> EnergyClusterHealth.HEALTHY // TODO
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
		return new EnergyClusterData(EnergyValues.unit*(unitsSpawned.min+rand.nextFloat()*(unitsSpawned.max-unitsSpawned.min)),EnergyValues.unit*(unitsMax.min+rand.nextFloat()*(unitsMax.max-unitsMax.min)),healthGen.apply(rand));
	}
}

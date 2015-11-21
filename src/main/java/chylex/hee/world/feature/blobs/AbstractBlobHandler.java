package chylex.hee.world.feature.blobs;
import java.util.Random;
import chylex.hee.system.collections.weight.IWeightProvider;

public abstract class AbstractBlobHandler implements IWeightProvider{
	private final int weight;
	
	public AbstractBlobHandler(int weight){
		this.weight = weight;
	}
	
	@Override
	public int getWeight(){
		return weight;
	}
	
	@FunctionalInterface
	protected static interface IRandGenerator{
		double generate(Random rand);
	}
}
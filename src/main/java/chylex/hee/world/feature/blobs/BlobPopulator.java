package chylex.hee.world.feature.blobs;
import java.util.Random;
import chylex.hee.system.collections.weight.IWeightProvider;
import chylex.hee.world.feature.util.DecoratorFeatureGenerator;

public abstract class BlobPopulator implements IWeightProvider{
	private final int weight;
	
	public BlobPopulator(int weight){
		this.weight = weight;
	}
	
	@Override
	public int getWeight(){
		return weight;
	}
	
	public abstract void generate(DecoratorFeatureGenerator gen, Random rand);
}

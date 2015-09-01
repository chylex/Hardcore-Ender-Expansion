package chylex.hee.world.feature.old_blobs;
import chylex.hee.system.collections.weight.IWeightProvider;

public abstract class BlobPopulator implements IWeightProvider{
	private final int weight;
	
	public BlobPopulator(int weight){
		this.weight = weight;
	}
	
	@Override
	public int getWeight(){
		return weight;
	}
	
	//public abstract void generate(DecoratorFeatureGenerator gen, Random rand);
}

package chylex.hee.world.feature.blobs.populators;
import java.util.Random;
import chylex.hee.system.collections.weight.IWeightProvider;
import chylex.hee.world.feature.blobs.StructureWorldBlob;

public abstract class BlobPopulator implements IWeightProvider{
	private final int weight;
	
	public BlobPopulator(int weight){
		this.weight = weight;
	}
	
	@Override
	public int getWeight(){
		return weight;
	}
	
	public abstract void populate(StructureWorldBlob world, Random rand);
}

package chylex.hee.world.feature.blobs.populators;
import java.util.Random;
import chylex.hee.world.feature.blobs.AbstractBlobHandler;
import chylex.hee.world.feature.blobs.StructureWorldBlob;

public abstract class BlobPopulator extends AbstractBlobHandler{
	public BlobPopulator(int weight){
		super(weight);
	}
	
	public abstract void populate(StructureWorldBlob world, Random rand);
}

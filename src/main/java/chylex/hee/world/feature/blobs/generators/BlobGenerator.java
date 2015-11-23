package chylex.hee.world.feature.blobs.generators;
import java.util.Random;
import chylex.hee.world.feature.blobs.AbstractBlobHandler;
import chylex.hee.world.feature.blobs.StructureWorldBlob;

public abstract class BlobGenerator extends AbstractBlobHandler{
	public BlobGenerator(int weight){
		super(weight);
	}
	
	public abstract void generate(StructureWorldBlob world, Random rand);
}

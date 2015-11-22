package chylex.hee.world.feature.blobs.populators;
import java.util.Random;
import chylex.hee.world.feature.blobs.AbstractBlobHandler;
import chylex.hee.world.feature.blobs.StructureWorldBlob;

public abstract class BlobPopulator extends AbstractBlobHandler{
	/**
	 * A large weight value that almost guarantees a Blob will spawn with this populator, if the populator amount is large enough.
	 * <br><br>
	 * The value is 1/100 of {@link Integer#MAX_VALUE}, which gives enough space to avoid overflowing, but provides a very low
	 * chance of failing.
	 */
	public static final int WEIGHT_GUARANTEED = 21_474_836;
	
	public BlobPopulator(int weight){
		super(weight);
	}
	
	public abstract void populate(StructureWorldBlob world, Random rand);
}

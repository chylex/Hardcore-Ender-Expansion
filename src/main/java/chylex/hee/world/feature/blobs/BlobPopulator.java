package chylex.hee.world.feature.blobs;
import java.util.Random;
import chylex.hee.world.feature.util.DecoratorFeatureGenerator;

public abstract class BlobPopulator{
	public abstract void generate(DecoratorFeatureGenerator gen, Random rand);
}

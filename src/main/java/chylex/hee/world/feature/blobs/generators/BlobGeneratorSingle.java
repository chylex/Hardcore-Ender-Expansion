package chylex.hee.world.feature.blobs.generators;
import java.util.Random;
import chylex.hee.world.feature.blobs.BlobGenerator;
import chylex.hee.world.feature.util.DecoratorFeatureGenerator;

public class BlobGeneratorSingle extends BlobGenerator{
	protected double minRad, maxRad;
	
	public BlobGeneratorSingle(int weight){
		super(weight);
	}
	
	public BlobGeneratorSingle rad(double minRad, double maxRad){
		this.minRad = minRad;
		this.maxRad = maxRad;
		return this;
	}
	
	@Override
	public void generate(DecoratorFeatureGenerator gen, Random rand){
		genBlob(gen,0D,0D,0D,minRad+rand.nextDouble()*(maxRad-minRad));
	}
}

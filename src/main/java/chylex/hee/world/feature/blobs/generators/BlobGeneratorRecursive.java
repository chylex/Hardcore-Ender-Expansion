package chylex.hee.world.feature.blobs.generators;
import java.util.Random;
import chylex.hee.world.feature.blobs.BlobGenerator;
import chylex.hee.world.feature.util.DecoratorFeatureGenerator;
import chylex.hee.world.util.IRandomAmount;

public class BlobGeneratorRecursive extends BlobGenerator{ // TODO everything
	private IRandomAmount amountGen;
	private int minAmount, maxAmount;
	private double minRad, maxRad;
	
	public BlobGeneratorRecursive(int weight){
		super(weight);
	}
	
	public BlobGeneratorRecursive amount(IRandomAmount amountGen, int minAmount, int maxAmount){
		this.amountGen = amountGen;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
		return this;
	}
	
	public BlobGeneratorRecursive rad(double minRad, double maxRad){
		this.minRad = minRad;
		this.maxRad = maxRad;
		return this;
	}
	
	@Override
	public void generate(DecoratorFeatureGenerator gen, Random rand){
		
	}
}

package chylex.hee.world.feature.blobs.generators;
import java.util.Random;
import net.minecraft.util.Vec3;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.world.feature.blobs.BlobGenerator;
import chylex.hee.world.feature.util.DecoratorFeatureGenerator;
import chylex.hee.world.util.IRandomAmount;

public class BlobGeneratorFromCenter extends BlobGenerator{
	private IRandomAmount amountGen = IRandomAmount.exact;
	private byte minAmount, maxAmount;
	private double minRad, maxRad, minDist, maxDist;
	private boolean unifiedSize, limitDist;
	
	public BlobGeneratorFromCenter(int weight){
		super(weight);
	}
	
	public BlobGeneratorFromCenter amount(IRandomAmount amountGen, int minAmount, int maxAmount){
		this.amountGen = amountGen;
		this.minAmount = (byte)minAmount;
		this.maxAmount = (byte)maxAmount;
		return this;
	}
	
	public BlobGeneratorFromCenter rad(double minRad, double maxRad){
		this.minRad = minRad;
		this.maxRad = maxRad;
		return this;
	}
	
	public BlobGeneratorFromCenter dist(double minDist, double maxDist){
		this.minDist = minDist;
		this.maxDist = maxDist;
		return this;
	}
	
	/**
	 * Makes each blob the same size.
	 */
	public BlobGeneratorFromCenter unifySize(){
		this.unifiedSize = true;
		return this;
	}
	
	/**
	 * Forces blob distance to be limited to radius of center blob.
	 */
	public BlobGeneratorFromCenter limitDist(){
		this.limitDist = true;
		return this;
	}
	
	@Override
	public void generate(DecoratorFeatureGenerator gen, Random rand){
		double rad = minRad+rand.nextDouble()*(maxRad-minRad), firstDist = 0D;
		
		for(int a = 0, amt = amountGen.generate(rand,minAmount,maxAmount); a < amt; a++){
			Vec3 vec = a == 0 ? Vec3.createVectorHelper(0D,0D,0D) : DragonUtil.getRandomVector(rand);
			double dist = a == 0 ? 0D : minDist+rand.nextDouble()*(maxDist-minDist);
			
			if (limitDist){
				if (a == 0)firstDist = rad;
				else dist *= firstDist/maxDist;
			}
			
			genBlob(gen,vec.xCoord*dist,vec.yCoord*dist,vec.zCoord*dist,rad);
			
			if (a <= amt-1 && !unifiedSize)rad = minRad+rand.nextDouble()*(maxRad-minRad);
		}
	}
}

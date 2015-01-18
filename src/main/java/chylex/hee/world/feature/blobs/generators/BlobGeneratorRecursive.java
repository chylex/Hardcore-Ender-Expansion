package chylex.hee.world.feature.blobs.generators;
import java.util.Random;
import net.minecraft.util.Vec3;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.world.feature.blobs.BlobGenerator;
import chylex.hee.world.feature.util.DecoratorFeatureGenerator;
import chylex.hee.world.util.IRandomAmount;

public class BlobGeneratorRecursive extends BlobGenerator{
	private IRandomAmount baseAmountGen = IRandomAmount.exact, totalAmountGen = IRandomAmount.exact, recursionAmountGen = IRandomAmount.exact;
	private byte minAmount, maxAmount, minTotalAmountLimit, maxTotalAmountLimit, minRecursionAmount, maxRecursionAmount, maxRecursion;
	private double minRad, maxRad, minRecursionChance, maxRecursionChance, recursionChanceMp, minBlobDistMp, maxBlobDistMp;
	private boolean recursionChanceCached;
	
	private int tmpBlobsLeft;
	private double tmpRecursionChance;
	
	public BlobGeneratorRecursive(int weight){
		super(weight);
	}
	
	public BlobGeneratorRecursive baseAmount(IRandomAmount baseAmountGen, int minAmount, int maxAmount){
		this.baseAmountGen = baseAmountGen;
		this.minAmount = (byte)minAmount;
		this.maxAmount = (byte)maxAmount;
		return this;
	}
	
	public BlobGeneratorRecursive totalAmount(IRandomAmount totalAmountGen, int minTotalAmountLimit, int maxTotalAmountLimit){
		this.totalAmountGen = totalAmountGen;
		this.minTotalAmountLimit = (byte)minTotalAmountLimit;
		this.maxTotalAmountLimit = (byte)maxTotalAmountLimit;
		return this;
	}
	
	public BlobGeneratorRecursive recursionAmount(IRandomAmount recursionAmountGen, int minRecursionAmount, int maxRecursionAmount){
		this.recursionAmountGen = recursionAmountGen;
		this.minRecursionAmount = (byte)minRecursionAmount;
		this.maxRecursionAmount = (byte)maxRecursionAmount;
		return this;
	}
	
	public BlobGeneratorRecursive rad(double minRad, double maxRad){
		this.minRad = minRad;
		this.maxRad = maxRad;
		return this;
	}
	
	public BlobGeneratorRecursive distMp(double minBlobDistMp, double maxBlobDistMp){
		this.minBlobDistMp = minBlobDistMp;
		this.maxBlobDistMp = maxBlobDistMp;
		return this;
	}
	
	public BlobGeneratorRecursive recursionChance(double minRecursionChance, double maxRecursionChance, double recursionChanceMp, int maxRecursion){
		this.minRecursionChance = minRecursionChance;
		this.maxRecursionChance = maxRecursionChance;
		this.recursionChanceMp = recursionChanceMp;
		this.maxRecursion = (byte)maxRecursion;
		return this;
	}
	
	/**
	 * Recursion chance is generated only once.
	 */
	public BlobGeneratorRecursive cacheRecursionChance(){
		this.recursionChanceCached = true;
		return this;
	}
	
	@Override
	public void generate(DecoratorFeatureGenerator gen, Random rand){
		tmpBlobsLeft = totalAmountGen.generate(rand,minTotalAmountLimit,maxTotalAmountLimit);
		tmpRecursionChance = minRecursionChance+rand.nextDouble()*(maxRecursionChance-minRecursionChance);
		
		double firstRad = 0D, rad, dist;
		Vec3 vec;
		
		for(int a = 0, amount = baseAmountGen.generate(rand,minAmount,maxAmount); a < amount; a++){
			rad = minRad+rand.nextDouble()*(maxRad-minRad);
			if (a == 0)firstRad = rad;
			
			vec = a == 0 ? new Vec3(0D,0D,0D) : DragonUtil.getRandomVector(rand);
			dist = a == 0 ? 0D : firstRad*(minBlobDistMp+rand.nextDouble()*(maxBlobDistMp-minBlobDistMp));
			
			genNewBlob(gen,rand,vec.xCoord*dist,vec.yCoord*dist,vec.zCoord*dist,rad,0);
		}
	}
	
	private void genNewBlob(DecoratorFeatureGenerator gen, Random rand, double x, double y, double z, double rad, int recursionLevel){
		if (tmpBlobsLeft < 0 || recursionLevel >= maxRecursion)return;
		
		genBlob(gen,x,y,z,rad);
		--tmpBlobsLeft;
		
		if (rand.nextDouble() < tmpRecursionChance*(Math.pow(recursionChanceMp,recursionLevel))){
			for(int a = 0, amount = recursionAmountGen.generate(rand,minRecursionAmount,maxRecursionAmount); a < amount; a++){
				Vec3 dir = DragonUtil.getRandomVector(rand);
				double dist = rad*(minBlobDistMp+rand.nextDouble()*(maxBlobDistMp-minBlobDistMp));
				genNewBlob(gen,rand,x+dir.xCoord*dist,y+dir.yCoord*dist,z+dir.zCoord*dist,minRad+rand.nextDouble()*(maxRad-minRad),recursionLevel+1);
			}
		}
		
		if (!recursionChanceCached)tmpRecursionChance = minRecursionChance+rand.nextDouble()*(maxRecursionChance-minRecursionChance);
	}
}

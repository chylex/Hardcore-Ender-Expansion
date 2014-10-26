package chylex.hee.world.feature.blobs.generators;
import java.util.Random;
import net.minecraft.util.Vec3;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.world.feature.blobs.BlobGenerator;
import chylex.hee.world.feature.util.DecoratorFeatureGenerator;
import chylex.hee.world.util.IRandomAmount;

public class BlobGeneratorChain extends BlobGenerator{
	private IRandomAmount amountGen;
	private byte minAmount, maxAmount;
	private double minRad, maxRad, minChainDistMp, maxChainDistMp;
	
	public BlobGeneratorChain(int weight){
		super(weight);
	}
	
	public BlobGeneratorChain amount(IRandomAmount amountGen, int minAmount, int maxAmount){
		this.amountGen = amountGen;
		this.minAmount = (byte)minAmount;
		this.maxAmount = (byte)maxAmount;
		return this;
	}
	
	public BlobGeneratorChain rad(double minRad, double maxRad){
		this.minRad = minRad;
		this.maxRad = maxRad;
		return this;
	}
	
	public BlobGeneratorChain distMp(double minChainDistMp, double maxChainDistMp){
		this.minChainDistMp = minChainDistMp;
		this.maxChainDistMp = maxChainDistMp;
		return this;
	}
	
	@Override
	public void generate(DecoratorFeatureGenerator gen, Random rand){
		double prevRad = 0D;
		
		for(int a = 0, amt = amountGen.generate(rand,minAmount,maxAmount); a < amt; a++){
			double rad = minRad+rand.nextDouble()*(maxRad-minRad);
			
			Vec3 vec = a == 0 ? Vec3.createVectorHelper(0D,0D,0D) : DragonUtil.getRandomVector(rand);
			double dist = a == 0 ? 0D : prevRad*(minChainDistMp+rand.nextDouble()*(maxChainDistMp-minChainDistMp));
			
			genBlob(gen,vec.xCoord*dist,vec.yCoord*dist,vec.zCoord*dist,rad);
		}
	}
}

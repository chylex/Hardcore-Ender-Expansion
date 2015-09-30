package chylex.hee.world.feature.old_blobs.generators;
import chylex.hee.world.feature.old_blobs.BlobGenerator;
import chylex.hee.world.util.RandomAmount;

public class BlobGeneratorChain extends BlobGenerator{
	private RandomAmount amountGen = RandomAmount.exact;
	private byte minAmount, maxAmount;
	private double minRad, maxRad, minChainDistMp, maxChainDistMp;
	private boolean unifiedSize;
	
	public BlobGeneratorChain(int weight){
		super(weight);
	}
	
	public BlobGeneratorChain amount(RandomAmount amountGen, int minAmount, int maxAmount){
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
	
	/**
	 * Makes each blob the same size.
	 */
	public BlobGeneratorChain unifySize(){
		this.unifiedSize = true;
		return this;
	}
	
	/*@Override
	public void generate(DecoratorFeatureGenerator gen, Random rand){
		double prevRad = 0D;
		double x = 0D, y = 0D, z = 0D;
		
		for(int a = 0, amount = amountGen.generate(rand,minAmount,maxAmount); a < amount; a++){
			double rad = unifiedSize && a > 0 ? prevRad : minRad+rand.nextDouble()*(maxRad-minRad);
			
			Vec3 vec = a == 0 ? Vec3.createVectorHelper(0D,0D,0D) : DragonUtil.getRandomVector(rand);
			double dist = a == 0 ? 0D : prevRad*(minChainDistMp+rand.nextDouble()*(maxChainDistMp-minChainDistMp));
			
			genBlob(gen,x += vec.xCoord*dist,y += vec.yCoord*dist,z += vec.zCoord*dist,rad);
			prevRad = rad;
		}
	}*/
}

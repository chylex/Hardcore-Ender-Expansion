package chylex.hee.world.feature.blobs.generators;
import java.util.Random;
import net.minecraft.util.Vec3;
import chylex.hee.world.feature.blobs.BlobGenerator;
import chylex.hee.world.feature.util.DecoratorFeatureGenerator;
import chylex.hee.world.util.IRandomAmount;

public class BlobGeneratorAroundCenter extends BlobGenerator{
	private IRandomAmount amountGen;
	private int maxAmount;
	private double minRad, maxRad, minDist, maxDist;
	private boolean unified;
	
	public BlobGeneratorAroundCenter amount(IRandomAmount amountGen, int maxAmount){
		this.amountGen = amountGen;
		this.maxAmount = maxAmount;
		return this;
	}
	
	public BlobGeneratorAroundCenter rad(double minRad, double maxRad){
		this.minRad = minRad;
		this.maxRad = maxRad;
		return this;
	}
	
	public BlobGeneratorAroundCenter dist(double minDist, double maxDist){
		this.minDist = minDist;
		this.maxDist = maxDist;
		return this;
	}
	
	public BlobGeneratorAroundCenter unified(boolean isSizeUnified){
		this.unified = isSizeUnified;
		return this;
	}
	
	@Override
	public void generate(DecoratorFeatureGenerator gen, Random rand){
		double rad = minRad+rand.nextDouble()*(maxRad-minRad);
		
		for(int a = 0, amt = amountGen.generate(rand,maxAmount); a < amt; a++){
			Vec3 vec = a == 0 ? Vec3.createVectorHelper(0D,0D,0D) : Vec3.createVectorHelper(rand.nextDouble()-0.5D,rand.nextDouble()-0.5D,rand.nextDouble()-0.5D).normalize();
			double dist = a == 0 ? 0D : minDist+rand.nextDouble()*(maxDist-minDist);
			
			genBlob(gen,vec.xCoord*dist,vec.yCoord*dist,vec.zCoord*dist,rad);
			
			if (a < amt-1 && !unified)rad = minRad+rand.nextDouble()*(maxRad-minRad);
		}
	}
}

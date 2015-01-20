package chylex.hee.world.feature.blobs.generators;

import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.Vec3M;
import chylex.hee.world.feature.util.DecoratorFeatureGenerator;

public class BlobGeneratorSingleCut extends BlobGeneratorSingle{
	private double minRadMp, maxRadMp, minDistMp, maxDistMp;
	
	public BlobGeneratorSingleCut(int weight){
		super(weight);
	}
	
	public BlobGeneratorSingleCut cutRadMp(double minRadMp, double maxRadMp){
		this.minRadMp = minRadMp;
		this.maxRadMp = maxRadMp;
		return this;
	}
	
	public BlobGeneratorSingleCut cutDistMp(double minDistMp, double maxDistMp){
		this.minDistMp = minDistMp;
		this.maxDistMp = maxDistMp;
		return this;
	}
	
	@Override
	public void generate(DecoratorFeatureGenerator gen, Random rand){
		double rad = minRad+rand.nextDouble()*(maxRad-minRad);
		genBlob(gen,0D,0D,0D,rad);
		
		Vec3M vec = DragonUtil.getRandomVector(rand);
		double dist = minDistMp+rand.nextDouble()*(maxDistMp-minDistMp);
		genBlob(gen,vec.x*dist,vec.y*dist,vec.z*dist,rad*(minRadMp+rand.nextDouble()*(maxRadMp-minRadMp)),Blocks.air);
	}
}

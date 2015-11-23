package chylex.hee.world.feature.blobs.generators;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.tuple.Pair;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.system.collections.CollectionUtil;
import chylex.hee.world.feature.blobs.StructureWorldBlob;
import chylex.hee.world.util.RandomAmount;
import chylex.hee.world.util.RangeGenerator;

public class BlobGeneratorAttaching extends BlobGenerator{
	protected RangeGenerator amount;
	protected IRandGenerator radiusFirst = rand -> 0D, radiusOther = rand -> 0D;
	protected IRandGenerator distance = rand -> 0.05D+rand.nextDouble()*0.7D;
	
	public BlobGeneratorAttaching(int weight){
		super(weight);
	}
	
	public BlobGeneratorAttaching setAmount(int amount){
		this.amount = new RangeGenerator(amount,amount,RandomAmount.exact);
		return this;
	}
	
	public BlobGeneratorAttaching setAmount(int minAmount, int maxAmount){
		this.amount = new RangeGenerator(minAmount,maxAmount,RandomAmount.linear);
		return this;
	}
	
	public BlobGeneratorAttaching setAmount(RangeGenerator amountGenerator){
		this.amount = amountGenerator;
		return this;
	}
	
	public BlobGeneratorAttaching setRadiusFirst(double minRadius, double maxRadius){
		this.radiusFirst = rand -> minRadius+rand.nextDouble()*(maxRadius-minRadius);
		return this;
	}
	
	public BlobGeneratorAttaching setRadiusFirst(IRandGenerator radiusGenerator){
		this.radiusFirst = radiusGenerator;
		return this;
	}
	
	public BlobGeneratorAttaching setRadiusOther(double minRadius, double maxRadius){
		this.radiusOther = rand -> minRadius+rand.nextDouble()*(maxRadius-minRadius);
		return this;
	}
	
	public BlobGeneratorAttaching setRadiusOther(IRandGenerator radiusGenerator){
		this.radiusOther = radiusGenerator;
		return this;
	}
	
	public BlobGeneratorAttaching setRadiusBoth(double minRadius, double maxRadius){
		this.radiusFirst = this.radiusOther = rand -> minRadius+rand.nextDouble()*(maxRadius-minRadius);
		return this;
	}
	
	public BlobGeneratorAttaching setRadiusBoth(IRandGenerator radiusGenerator){
		this.radiusFirst = this.radiusOther = radiusGenerator;
		return this;
	}
	
	public BlobGeneratorAttaching setDistanceMp(double minMp, double maxMp){
		this.distance = rand -> minMp+rand.nextDouble()*(maxMp-minMp);
		return this;
	}
	
	public BlobGeneratorAttaching setDistanceMp(IRandGenerator mpGenerator){
		this.distance = mpGenerator;
		return this;
	}
	
	@Override
	public void generate(StructureWorldBlob world, Random rand){
		int blobsLeft = (amount == null ? 0 : amount.next(rand));
		if (blobsLeft < 0)return;
		
		List<Pair<Vec,Double>> radii = new ArrayList<>();
		
		double radFirst = radiusFirst.generate(rand);
		generateBlob(world,0D,world.getCenterY(),0D,radFirst);
		radii.add(Pair.of(Vec.xyz(0D,world.getCenterY(),0D),radFirst));
		
		int attempts = (blobsLeft--)*3;
		
		while(--attempts > 0 && blobsLeft > 0){
			Pair<Vec,Double> target = CollectionUtil.randomOrNull(radii,rand);
			
			double rad = radiusOther.generate(rand);
			Vec pos = target.getKey().offset(Vec.xyzRandom(rand),(target.getValue()+rad)*distance.generate(rand));
			
			if (isBlobInsideWorld(world,pos.x,pos.y,pos.z,rad)){
				generateBlob(world,pos.x,pos.y,pos.z,rad);
				radii.add(Pair.of(pos,rad));
				--blobsLeft;
			}
		}
	}
}

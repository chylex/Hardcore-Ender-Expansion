package chylex.hee.world.feature.blobs.generators;
import java.util.Random;
import chylex.hee.world.feature.blobs.StructureWorldBlob;

public class BlobGeneratorSingle extends BlobGenerator{
	protected IRandGenerator radius = rand -> 0D;
	
	public BlobGeneratorSingle(int weight){
		super(weight);
	}
	
	public BlobGeneratorSingle setRadius(IRandGenerator radiusGenerator){
		this.radius = radiusGenerator;
		return this;
	}
	
	public BlobGeneratorSingle setRadius(double minRadius, double maxRadius){
		this.radius = rand -> minRadius+rand.nextDouble()*(maxRadius-minRadius);
		return this;
	}

	@Override
	public void generate(StructureWorldBlob world, Random rand){
		generateBlob(world,0D,world.getCenterY(),0D,radius.generate(rand));
	}
}

package chylex.hee.world.feature.blobs.generators;
import java.util.Random;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.world.feature.blobs.StructureWorldBlob;

public class BlobGeneratorFromCenter extends BlobGeneratorAttaching{
	public BlobGeneratorFromCenter(int weight){
		super(weight);
	}
	
	@Override
	public void generate(StructureWorldBlob world, Random rand){
		int blobsLeft = (amount == null ? 0 : amount.next(rand));
		if (blobsLeft < 0)return;
		
		double radFirst = radiusFirst.generate(rand);
		Vec posFirst = Vec.xyz(0D,world.getCenterY(),0D);
		generateBlob(world,posFirst.x,posFirst.y,posFirst.z,radFirst);
		
		int attempts = (blobsLeft--)*3;
		
		while(--attempts > 0 && blobsLeft > 0){
			double rad = radiusOther.generate(rand);
			Vec pos = posFirst.offset(Vec.xyzRandom(rand),(radFirst+rad)*distance.generate(rand));
			
			if (isBlobInsideWorld(world,pos.x,pos.y,pos.z,rad)){
				generateBlob(world,pos.x,pos.y,pos.z,rad);
				--blobsLeft;
			}
		}
	}
}

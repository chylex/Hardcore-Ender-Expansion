package chylex.hee.world.feature.blobs.generators;
import java.util.Random;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.world.feature.blobs.StructureWorldBlob;

public class BlobGeneratorChain extends BlobGeneratorAttaching{
	public BlobGeneratorChain(int weight){
		super(weight);
	}
	
	@Override
	public void generate(StructureWorldBlob world, Random rand){
		int blobsLeft = (amount == null ? 0 : amount.next(rand));
		if (blobsLeft < 0)return;
		
		double lastRad = radiusFirst.generate(rand);
		Vec lastPos = Vec.xyz(0D, world.getCenterY(), 0D);
		
		generateBlob(world, lastPos.x, lastPos.y, lastPos.z, lastRad);
		
		int attempts = (blobsLeft--)*3;
		
		while(--attempts > 0 && blobsLeft > 0){
			double rad = radiusOther.generate(rand);
			Vec pos = lastPos.offset(Vec.xyzRandom(rand), (lastRad+rad)*distance.generate(rand));
			
			if (isBlobInsideWorld(world, pos.x, pos.y, pos.z, rad)){
				generateBlob(world, pos.x, pos.y, pos.z, rad);
				--blobsLeft;
				
				lastRad = rad;
				lastPos = pos;
			}
		}
	}
}

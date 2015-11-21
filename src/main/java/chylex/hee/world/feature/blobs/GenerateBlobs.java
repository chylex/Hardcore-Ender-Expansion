package chylex.hee.world.feature.blobs;
import java.util.Random;
import chylex.hee.system.collections.weight.WeightedList;
import chylex.hee.world.feature.blobs.generators.IBlobGeneratorPass;
import chylex.hee.world.feature.blobs.populators.BlobPopulator;
import chylex.hee.world.structure.StructureWorld;

public class GenerateBlobs{
	private final WeightedList<BlobPattern> patterns = new WeightedList<>();
	private int worldRad = 16;
	
	public void addPattern(BlobPattern pattern){
		patterns.add(pattern);
	}
	
	public boolean generateBlobAt(StructureWorld world, Random rand, int centerX, int centerY, int centerZ){
		BlobPattern pattern = patterns.getRandomItem(rand);
		if (pattern == null)return false;
		
		StructureWorldBlob blobWorld = new StructureWorldBlob(worldRad,worldRad*2,worldRad);
		
		pattern.selectGenerator(rand).ifPresent(generator -> {
			generator.generate(blobWorld,rand);
			IBlobGeneratorPass.passSmoothing.run(blobWorld);
		});
		
		for(BlobPopulator populator:pattern.selectPopulators(rand))populator.populate(blobWorld,rand);
		
		blobWorld.insertInto(world,centerX,centerY-worldRad,centerZ);
		return true;
	}
}

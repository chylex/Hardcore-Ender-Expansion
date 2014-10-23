package chylex.hee.world.feature;
import java.util.List;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.commons.lang3.tuple.Pair;
import chylex.hee.system.weight.ObjectWeightPair;
import chylex.hee.system.weight.WeightedList;
import chylex.hee.world.feature.blobs.BlobGenerator;
import chylex.hee.world.feature.blobs.BlobPattern;
import chylex.hee.world.feature.blobs.BlobPopulator;
import chylex.hee.world.feature.util.DecoratorFeatureGenerator;

public class WorldGenBlob extends WorldGenerator{
	private enum BlobType{
		COMMON, UNCOMMON, RARE;
		
		BlobPattern[] patterns;
	}
	
	private static final WeightedList<ObjectWeightPair<BlobType>> types = new WeightedList<>();
	
	static{
		types.add(ObjectWeightPair.of(BlobType.COMMON,20));
		types.add(ObjectWeightPair.of(BlobType.UNCOMMON,4));
		types.add(ObjectWeightPair.of(BlobType.RARE,1));
		
		BlobType.COMMON.patterns = new BlobPattern[]{
			
		};
	}
	
	private DecoratorFeatureGenerator gen = new DecoratorFeatureGenerator();
	
	@Override
	public boolean generate(World world, Random rand, int x, int y, int z){
		BlobPattern[] patterns = types.getRandomItem(rand).getObject().patterns;
		Pair<BlobGenerator,List<BlobPopulator>> pattern = patterns[rand.nextInt(patterns.length)].generatePattern(rand);
		
		pattern.getLeft().generate(gen,rand);
		for(BlobPopulator populator:pattern.getRight())populator.generate(gen,rand);
		
		return false;
	}
}

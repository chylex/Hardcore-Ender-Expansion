package chylex.hee.world.feature;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.commons.lang3.tuple.Pair;
import chylex.hee.system.logging.Log;
import chylex.hee.system.weight.ObjectWeightPair;
import chylex.hee.system.weight.WeightedList;
import chylex.hee.world.feature.blobs.BlobGenerator;
import chylex.hee.world.feature.blobs.BlobPattern;
import chylex.hee.world.feature.blobs.BlobPopulator;
import chylex.hee.world.feature.util.DecoratorFeatureGenerator;
import chylex.hee.world.util.IRandomAmount;

public class WorldGenBlob extends WorldGenerator{
	private enum BlobType{
		COMMON, UNCOMMON, RARE;
		
		WeightedList<BlobPattern> patterns = new WeightedList<>();
	}
	
	private static final WeightedList<ObjectWeightPair<BlobType>> types = new WeightedList<>();
	
	static{
		types.add(ObjectWeightPair.of(BlobType.COMMON,20));
		types.add(ObjectWeightPair.of(BlobType.UNCOMMON,4));
		types.add(ObjectWeightPair.of(BlobType.RARE,1));
		
		BlobType.COMMON.patterns.addAll(new BlobPattern[]{
			
		});
	}
	
	private DecoratorFeatureGenerator gen = new DecoratorFeatureGenerator();
	
	@Override
	public boolean generate(World world, Random rand, int x, int y, int z){
		if (Log.isDeobfEnvironment){ // TODO remove debug
			BlobType.COMMON.patterns.clear();
			
			BlobType.COMMON.patterns.addAll(new BlobPattern[]{
				new BlobPattern(10).addGenerators(new BlobGenerator[]{
					
				}).addPopulators(new BlobPopulator[]{
					
				}).setPopulatorAmountProvider(IRandomAmount.linear,1,5)
			});
			
			Pair<BlobGenerator,List<BlobPopulator>> pattern = BlobType.COMMON.patterns.getRandomItem(rand).generatePattern(rand);
			
			pattern.getLeft().generate(gen,rand);
			for(BlobPopulator populator:pattern.getRight())populator.generate(gen,rand);
			
			gen.generate(world,rand,x,y,z);
			return true;
		}
		
		if (world.getBlock(x-8,y,z) != Blocks.air ||
			world.getBlock(x+8,y,z) != Blocks.air ||
			world.getBlock(x,y,z-8) != Blocks.air ||
			world.getBlock(x,y,z+8) != Blocks.air ||
			world.getBlock(x,y-8,z) != Blocks.air ||
			world.getBlock(x,y+8,z) != Blocks.air)return false;

		Pair<BlobGenerator,List<BlobPopulator>> pattern = types.getRandomItem(rand).getObject().patterns.getRandomItem(rand).generatePattern(rand);
		
		pattern.getLeft().generate(gen,rand);
		for(BlobPopulator populator:pattern.getRight())populator.generate(gen,rand);
		
		gen.generate(world,rand,x,y,z);
		return true;
	}
}

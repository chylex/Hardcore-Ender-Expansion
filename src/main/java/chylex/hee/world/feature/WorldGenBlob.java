package chylex.hee.world.feature;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import org.apache.commons.lang3.tuple.Pair;
import chylex.hee.system.commands.HeeDebugCommand.HeeTest;
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.system.weight.ObjectWeightPair;
import chylex.hee.system.weight.WeightedList;
import chylex.hee.world.feature.blobs.BlobGenerator;
import chylex.hee.world.feature.blobs.BlobPattern;
import chylex.hee.world.feature.blobs.BlobPopulator;
import chylex.hee.world.feature.blobs.generators.BlobGeneratorSingle;
import chylex.hee.world.feature.util.DecoratorFeatureGenerator;
import chylex.hee.world.feature.util.DecoratorFeatureGenerator.IDecoratorGenPass;
import chylex.hee.world.util.BlockLocation;
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
			new BlobPattern(1).addGenerators(new BlobGenerator[]{
				new BlobGeneratorSingle(5).rad(2D,5D),
				new BlobGeneratorSingle(3).rad(4D,10D)
			}).addPopulators(new BlobPopulator[]{
				
			})
		});
	}
	
	private static final IDecoratorGenPass genSmootherPass = new IDecoratorGenPass(){
		private final byte[] airOffX = new byte[]{ -1, 1, 0, 0, 0, 0 },
							 airOffY = new byte[]{ 0, 0, 0, 0, -1, 1 },
							 airOffZ = new byte[]{ 0, 0, -1, 1, 0, 0 };
		
		@Override
		public void run(DecoratorFeatureGenerator gen, List<BlockLocation> blocks){
			for(BlockLocation loc:blocks){
				for(int a = 0, adjacentAir = 0; a < 6; a++){
					if (gen.getBlock(loc.x+airOffX[a],loc.y+airOffY[a],loc.z+airOffZ[a]) == Blocks.air && ++adjacentAir >= 4){
						gen.setBlock(loc.x,loc.y,loc.z,Blocks.air);
						break;
					}
				}
			}
		}
	};
	
	@Override
	public boolean generate(World world, Random rand, int x, int y, int z){
		if (world.getBlock(x-7,y,z) != Blocks.air ||
			world.getBlock(x+7,y,z) != Blocks.air ||
			world.getBlock(x,y,z-7) != Blocks.air ||
			world.getBlock(x,y,z+7) != Blocks.air ||
			world.getBlock(x,y-7,z) != Blocks.air ||
			world.getBlock(x,y+7,z) != Blocks.air)return false;
		
		DecoratorFeatureGenerator gen = new DecoratorFeatureGenerator();
		Pair<BlobGenerator,List<BlobPopulator>> pattern = types.getRandomItem(rand).getObject().patterns.getRandomItem(rand).generatePattern(rand);
		
		pattern.getLeft().generate(gen,rand);
		gen.runPass(genSmootherPass);
		for(BlobPopulator populator:pattern.getRight())populator.generate(gen,rand);
		
		if (gen.getOutOfBoundsCounter() > 6)return false;
		
		gen.generate(world,rand,x,y,z);
		return true;
	}
	
	public static final HeeTest $debugTest = new HeeTest(){
		@Override
		public void run(){
			WeightedList<BlobPattern> patterns = new WeightedList<>(new BlobPattern[]{
				new BlobPattern(10).addGenerators(new BlobGenerator[]{
					new BlobGeneratorSingle(3).rad(4D,10D)
				}).addPopulators(new BlobPopulator[]{
					
				}).setPopulatorAmountProvider(IRandomAmount.exact,1,1)
			});
			
			DecoratorFeatureGenerator gen = new DecoratorFeatureGenerator();
			Pair<BlobGenerator,List<BlobPopulator>> pattern = patterns.getRandomItem(world.rand).generatePattern(world.rand);
			
			Stopwatch.time("WorldGenBlob - test blob generator");
			pattern.getLeft().generate(gen,world.rand);
			Stopwatch.finish("WorldGenBlob - test blob generator");
			
			Stopwatch.time("WorldGenBlob - test smoother pass");
			gen.runPass(genSmootherPass);
			Stopwatch.finish("WorldGenBlob - test smoother pass");
			
			Stopwatch.time("WorldGenBlob - test pattern generator");
			for(BlobPopulator populator:pattern.getRight())populator.generate(gen,world.rand);
			Stopwatch.finish("WorldGenBlob - test pattern generator");
			
			Stopwatch.time("WorldGenBlob - test generate");
			gen.generate(world,world.rand,(int)player.posX+10,(int)player.posY-5,(int)player.posZ);
			Stopwatch.finish("WorldGenBlob - test generate");
		}
	};
}

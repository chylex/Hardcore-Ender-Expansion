package chylex.hee.world.feature.blobs;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.init.Blocks;
import chylex.hee.game.commands.HeeDebugCommand.HeeTest;
import chylex.hee.system.collections.weight.WeightedList;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.feature.GenerateFeature;
import chylex.hee.world.feature.blobs.generators.BlobGeneratorChain;
import chylex.hee.world.feature.blobs.generators.IBlobGeneratorPass;
import chylex.hee.world.feature.blobs.populators.BlobPopulator;
import chylex.hee.world.feature.blobs.populators.BlobPopulatorLiquidPool;
import chylex.hee.world.feature.blobs.populators.BlobPopulatorLiquidStream;
import chylex.hee.world.structure.IWorldPositionPredicate;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.StructureWorldPart;

public class GenerateBlobs extends GenerateFeature{
	public static IWorldPositionPredicate inDistanceFromCenter(final GenerateBlobs generator, final double minDistance, final double maxDistance){
		final double minDistSq = minDistance*minDistance;
		final double maxDistSq = maxDistance*maxDistance;
		final int chunkSize = generator.chunkSize, halfChunkSize = chunkSize/2;
		
		return (world, rand, x, y, z) -> {
			double distSq = MathUtil.distanceSquared((x/chunkSize)*chunkSize+halfChunkSize,0,(z/chunkSize)*chunkSize+halfChunkSize);
			return distSq >= minDistSq && distSq <= maxDistSq;
		};
	}
	
	private final WeightedList<BlobPattern> patterns = new WeightedList<>();
	private int worldRad = 16;
	private IWorldPositionPredicate predicate = null;
	
	public void addPattern(BlobPattern pattern){
		patterns.add(pattern);
	}
	
	public void setBlobWorldRad(int worldRad){
		this.worldRad = worldRad;
	}
	
	public void setPredicate(IWorldPositionPredicate predicate){
		this.predicate = predicate;
	}
	
	@Override
	public void generateSplit(StructureWorld world, Random rand){
		generateSplitInternal(world,rand,0);
	}
	
	@Override
	public void generateFull(StructureWorld world, Random rand){
		generateFullInternal(world,rand,8);
	}
	
	@Override
	protected boolean tryGenerateFeature(StructureWorld world, Random rand, int x, int y, int z){
		return (predicate == null || predicate.check(world,rand,x,y,z)) && generateBlobAt(world,rand,x,y,z,StructureWorldPart.requireAir);
	}
	
	/**
	 * Generates a single blob.
	 */
	protected boolean generateBlobAt(StructureWorld world, Random rand, int centerX, int centerY, int centerZ, @Nullable IWorldPositionPredicate predicate){
		BlobPattern pattern = patterns.getRandomItem(rand);
		if (pattern == null)return false;
		
		StructureWorldBlob blobWorld = new StructureWorldBlob(worldRad,worldRad*2,worldRad);
		
		pattern.selectGenerator(rand).ifPresent(generator -> {
			generator.generate(blobWorld,rand);
			IBlobGeneratorPass.passSmoothing.run(blobWorld);
		});
		
		for(BlobPopulator populator:pattern.selectPopulators(rand))populator.populate(blobWorld,rand);
		
		return blobWorld.insertIf(world,rand,centerX,centerY-worldRad,centerZ,predicate);
	}
	
	public static final HeeTest $debugTest = new HeeTest(){
		@Override
		public void run(String...args){
			BlobPattern pattern = new BlobPattern(1);
			pattern.addGenerator(new BlobGeneratorChain(1).setAmount(6).setRadiusBoth(2D,5D).setDistanceMp(1D,1.5D));
			pattern.addPopulator(new BlobPopulatorLiquidPool(1).setAmount(9,9).setBlock(Blocks.flowing_water));
			pattern.addPopulator(new BlobPopulatorLiquidStream(1).setAmount(6,6).setBlock(Blocks.flowing_water).setCanHaveAirAbove().setStrictFlowCheck());
			pattern.setPopulatorAmount(99);
			
			GenerateBlobs gen = new GenerateBlobs();
			gen.addPattern(pattern);
			
			StructureWorld structureWorld = new StructureWorld(16,32,16);
			gen.generateBlobAt(structureWorld,world.rand,0,16,0,null);
			
			structureWorld.generateInWorld(world,world.rand,MathUtil.floor(player.posX),MathUtil.floor(player.posY)-32,MathUtil.floor(player.posZ));
		}
	};
}

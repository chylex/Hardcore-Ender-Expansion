package chylex.hee.world.feature.blobs;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.game.commands.HeeDebugCommand.HeeTest;
import chylex.hee.init.BlockList;
import chylex.hee.system.collections.weight.WeightedList;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.feature.blobs.generators.BlobGeneratorSingle;
import chylex.hee.world.feature.blobs.generators.IBlobGeneratorPass;
import chylex.hee.world.feature.blobs.populators.BlobPopulator;
import chylex.hee.world.feature.blobs.populators.BlobPopulatorCaves;
import chylex.hee.world.feature.blobs.populators.BlobPopulatorOre;
import chylex.hee.world.feature.ores.GenerateOres;
import chylex.hee.world.feature.ores.IOreGenerator;
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
	
	public static final HeeTest $debugTest = new HeeTest(){
		@Override
		public void run(String...args){
			GenerateOres ores = new GenerateOres(Blocks.end_stone,BlockList.end_powder_ore);
			ores.setAttemptsPerChunk(50);
			ores.setClustersPerChunk(9,10);
			ores.setOresPerCluster(1,15);
			ores.setY(0,32);
			ores.setOreGenerator(new IOreGenerator.AdjacentSpread(false));
			
			BlobPattern pattern = new BlobPattern(1);
			pattern.addGenerator(new BlobGeneratorSingle(1).setRadius(12D,12D));
			pattern.addPopulator(new BlobPopulatorOre(1).setGenerator(ores));
			pattern.addPopulator(new BlobPopulatorCaves(1)
									.setMainAmount(1,1).setMainRadius(2D,2D).setMainCycles(100,100));
			pattern.setPopulatorAmount(99);
			
			GenerateBlobs gen = new GenerateBlobs();
			gen.addPattern(pattern);
			
			StructureWorld structureWorld = new StructureWorld(16,32,16);
			gen.generateBlobAt(structureWorld,world.rand,0,16,0);
			structureWorld.generateInWorld(world,world.rand,MathUtil.floor(player.posX),MathUtil.floor(player.posY)-32,MathUtil.floor(player.posZ));
		}
	};
}

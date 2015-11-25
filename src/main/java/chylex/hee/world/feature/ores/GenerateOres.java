package chylex.hee.world.feature.ores;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import chylex.hee.game.commands.HeeDebugCommand.HeeTest;
import chylex.hee.init.BlockList;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.structure.util.IBlockPicker;
import chylex.hee.world.util.BoundingBox;
import chylex.hee.world.util.RandomAmount;
import chylex.hee.world.util.RangeGenerator;

public final class GenerateOres{
	final Block toReplace;
	final IBlockPicker orePicker;
	
	private int chunkSize = 16;
	private int minY = 0;
	private int maxY = 256;
	private int attemptsPerChunk;
	private RangeGenerator clustersPerChunk;
	private RangeGenerator oresPerCluster;
	private IOreGenerator oreGenerator;
	
	public GenerateOres(Block toReplace, IBlockPicker orePicker){
		this.toReplace = toReplace;
		this.orePicker = orePicker;
	}
	
	public GenerateOres(Block toReplace, Block oreBlock){
		this.toReplace = toReplace;
		this.orePicker = new BlockInfo(oreBlock);
	}
	
	public GenerateOres(Block toReplace, Block oreBlock, int oreMeta){
		this.toReplace = toReplace;
		this.orePicker = new BlockInfo(oreBlock,oreMeta);
	}
	
	public void setChunkSize(int chunkSize){
		this.chunkSize = chunkSize;
	}
	
	public void setY(int minY, int maxY){
		if (maxY < minY)throw new IllegalArgumentException("maxY cannot be smaller than minY!");
		this.minY = minY;
		this.maxY = maxY;
	}
	
	public void setAttemptsPerChunk(int attempts){
		this.attemptsPerChunk = attempts;
	}
	
	public void setClustersPerChunk(int min, int max){
		this.clustersPerChunk = new RangeGenerator(min,max,RandomAmount.linear);
	}
	
	public void setClustersPerChunk(int min, int max, RandomAmount distribution){
		this.clustersPerChunk = new RangeGenerator(min,max,distribution);
	}
	
	public void setOresPerCluster(int min, int max){
		this.oresPerCluster = new RangeGenerator(min,max,RandomAmount.linear);
	}
	
	public void setOresPerCluster(int min, int max, RandomAmount distribution){
		this.oresPerCluster = new RangeGenerator(min,max,distribution);
	}
	
	public void setOreGenerator(IOreGenerator generator){
		this.oreGenerator = generator;
	}
	
	/**
	 * Divides the world into chunks of customizable size, and cycles through all of them.<br>
	 * Caution: does not generate ores in edge chunks to prevent cut off ore clusters.
	 */
	public void generateSplit(StructureWorld world, Random rand){
		BoundingBox worldBox = world.getArea();
		int totalChunksX = MathUtil.ceil((worldBox.x2-worldBox.x1)/(float)chunkSize);
		int totalChunksZ = MathUtil.ceil((worldBox.z2-worldBox.z1)/(float)chunkSize);
		PosMutable mpos = new PosMutable();
		
		for(int chunkX = 1; chunkX < totalChunksX-1; chunkX++){
			for(int chunkZ = 1; chunkZ < totalChunksZ-1; chunkZ++){
				int clusters = clustersPerChunk.next(rand);
				
				for(int attempt = 0; attempt < attemptsPerChunk && clusters > 0; attempt++){
					mpos.set(worldBox.x1+chunkX*chunkSize,minY,worldBox.z1+chunkZ*chunkSize).move(rand.nextInt(chunkSize),rand.nextInt(maxY-minY+1),rand.nextInt(chunkSize));
					
					if (oreGenerator.canPlaceAt(this,world,rand,mpos.x,mpos.y,mpos.z)){
						oreGenerator.generate(this,world,rand,mpos.x,mpos.y,mpos.z,oresPerCluster.next(rand));
						--clusters;
					}
				}
			}
		}
	}
	
	/**
	 * Uses the entire world area as a single chunk. Use {@code edgeDistance} to prevent cut off ore clusters.
	 */
	public void generateFull(StructureWorld world, Random rand, int edgeDistance){
		BoundingBox worldBox = world.getArea();
		PosMutable mpos = new PosMutable();
		
		int clusters = clustersPerChunk.next(rand);
		
		for(int attempt = 0; attempt < attemptsPerChunk && clusters > 0; attempt++){
			mpos.set(worldBox.x1+edgeDistance+rand.nextInt(worldBox.x2-worldBox.x1+1-2*edgeDistance),
					 minY+rand.nextInt(1+maxY-minY),
					 worldBox.z1+edgeDistance+rand.nextInt(worldBox.z2-worldBox.z1+1-2*edgeDistance));
			
			if (world.getBlock(mpos) == toReplace){
				oreGenerator.generate(this,world,rand,mpos.x,mpos.y,mpos.z,oresPerCluster.next(rand));
				--clusters;
			}
		}
	}
	
	public static final HeeTest $debugTest = new HeeTest(){
		@Override
		public void run(String...args){
			int amtX = DragonUtil.tryParse(args.length > 0 ? args[0] : "",1);
			int amtZ = DragonUtil.tryParse(args.length > 1 ? args[1] : "",1);
			
			GenerateOres gen = new GenerateOres(Blocks.air,BlockList.end_powder_ore);
			gen.setChunkSize(12);
			gen.setY(16,16);
			gen.setAttemptsPerChunk(1);
			gen.setClustersPerChunk(1,1);
			
			gen.setOresPerCluster(4,4,RandomAmount.preferSmaller);
			gen.setOreGenerator(new IOreGenerator.AttachingLines(new RangeGenerator(4,4,RandomAmount.aroundCenter),false));
			
			StructureWorld structureWorld = new StructureWorld(world,12+6*amtX,33,12+6*amtZ);
			gen.generateSplit(structureWorld,world.rand);
			structureWorld.generateInWorld(world,world.rand,MathUtil.floor(player.posX),MathUtil.floor(player.posY)-24,MathUtil.floor(player.posZ));
		}
	};
}

package chylex.hee.world.feature.ores;
import java.util.Random;
import net.minecraft.block.Block;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.Pos.PosMutable;
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
	
	public void generate(StructureWorld world, Random rand){
		BoundingBox worldBox = world.getArea();
		int totalChunksX = MathUtil.ceil((worldBox.x2-worldBox.x1)/(float)chunkSize);
		int totalChunksZ = MathUtil.ceil((worldBox.z2-worldBox.z1)/(float)chunkSize);
		PosMutable mpos = new PosMutable();
		
		for(int chunkX = 0; chunkX < totalChunksX; chunkX++){
			for(int chunkZ = 0; chunkZ < totalChunksZ; chunkZ++){
				int clusters = clustersPerChunk.next(rand);
				
				for(int attempt = 0; attempt < attemptsPerChunk && clusters > 0; attempt++){
					mpos.set(worldBox.x1+chunkX*chunkSize,minY,worldBox.z1+chunkZ*chunkSize).move(rand.nextInt(chunkSize),rand.nextInt(maxY-minY+1),rand.nextInt(chunkSize));
					
					if (world.getBlock(mpos.x,mpos.y,mpos.z) == toReplace){
						oreGenerator.generate(this,world,rand,mpos.x,mpos.y,mpos.z,oresPerCluster.next(rand));
						--clusters;
					}
				}
			}
		}
	}
}

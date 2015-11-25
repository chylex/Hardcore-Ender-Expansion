package chylex.hee.world.feature;
import java.util.Random;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.StructureWorld;
import chylex.hee.world.util.BoundingBox;
import chylex.hee.world.util.IRangeGenerator;
import chylex.hee.world.util.IRangeGenerator.RangeGenerator;
import chylex.hee.world.util.RandomAmount;

public abstract class GenerateFeature{
	protected int chunkSize = 16;
	protected IRangeGenerator yGenerator = new RangeGenerator(0,256,RandomAmount.linear);
	
	protected int attemptsPerChunk;
	protected IRangeGenerator generatedPerChunk;
	
	public final void setChunkSize(int chunkSize){
		this.chunkSize = chunkSize;
	}
	
	public final void setY(int minY, int maxY){
		this.yGenerator = new RangeGenerator(minY,maxY,RandomAmount.linear);
	}
	
	public final void setY(int minY, int maxY, RandomAmount distribution){
		this.yGenerator = new RangeGenerator(minY,maxY,distribution);
	}
	
	public final void setY(IRangeGenerator yGenerator){
		this.yGenerator = yGenerator;
	}
	
	public final void setAttemptsPerChunk(int attempts){
		this.attemptsPerChunk = attempts;
	}
	
	public final void setGeneratedPerChunk(int min, int max){
		this.generatedPerChunk = new RangeGenerator(min,max,RandomAmount.linear);
	}
	
	public final void setGeneratedPerChunk(int min, int max, RandomAmount distribution){
		this.generatedPerChunk = new RangeGenerator(min,max,distribution);
	}
	
	/**
	 * Divides the world into chunks of customizable size, and cycles through all of them.
	 */
	protected void generateSplitInternal(StructureWorld world, Random rand, int edgeChunkDistance){
		BoundingBox worldBox = world.getArea();
		int totalChunksX = MathUtil.ceil((worldBox.x2-worldBox.x1)/(float)chunkSize);
		int totalChunksZ = MathUtil.ceil((worldBox.z2-worldBox.z1)/(float)chunkSize);
		
		for(int chunkX = edgeChunkDistance; chunkX < totalChunksX-edgeChunkDistance; chunkX++){
			for(int chunkZ = edgeChunkDistance; chunkZ < totalChunksZ-edgeChunkDistance; chunkZ++){
				int featuresLeft = generatedPerChunk.next(rand);
				
				for(int attempt = 0; attempt < attemptsPerChunk && featuresLeft > 0; attempt++){
					int x = worldBox.x1+chunkX*chunkSize+rand.nextInt(chunkSize);
					int y = yGenerator.next(rand);
					int z = worldBox.z1+chunkZ*chunkSize+rand.nextInt(chunkSize);
					
					if (tryGenerateFeature(world,rand,x,y,z))--featuresLeft;
				}
			}
		}
	}
	
	protected void generateFullInternal(StructureWorld world, Random rand, int edgeBlockDistance){
		BoundingBox worldBox = world.getArea();
		int featuresLeft = generatedPerChunk.next(rand);
		
		for(int attempt = 0; attempt < attemptsPerChunk && featuresLeft > 0; attempt++){
			int x = worldBox.x1+edgeBlockDistance+rand.nextInt(worldBox.x2-worldBox.x1+1-2*edgeBlockDistance);
			int y = yGenerator.next(rand);
			int z = worldBox.z1+edgeBlockDistance+rand.nextInt(worldBox.z2-worldBox.z1+1-2*edgeBlockDistance);
			
			if (tryGenerateFeature(world,rand,x,y,z))--featuresLeft;
		}
	}

	/**
	 * Divides the world into chunks of customizable size, and cycles through all of them.
	 */
	public abstract void generateSplit(StructureWorld world, Random rand);
	
	/**
	 * Uses the entire world area as a single chunk.
	 */
	public abstract void generateFull(StructureWorld world, Random rand);
	
	/**
	 * Generates the feature at the specified location.
	 */
	protected abstract boolean tryGenerateFeature(StructureWorld world, Random rand, int x, int y, int z);
}

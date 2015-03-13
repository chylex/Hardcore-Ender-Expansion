package chylex.hee.world.feature.blobs.populators;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.block.BlockList;
import chylex.hee.world.feature.blobs.BlobPopulator;
import chylex.hee.world.feature.util.DecoratorFeatureGenerator;
import chylex.hee.world.util.BlockLocation;
import chylex.hee.world.util.IRandomAmount;

public class BlobPopulatorEndermanSpawner extends BlobPopulator{
	private IRandomAmount blockAmountGen = IRandomAmount.exact;
	private byte minAttempts, maxAttempts, minBlockAmount, maxBlockAmount, visiblePlacementAttempts;
	private boolean knownBlockLocations;
	
	public BlobPopulatorEndermanSpawner(int weight){
		super(weight);
	}
	
	public BlobPopulatorEndermanSpawner attempts(int minAttempts, int maxAttempts){
		this.minAttempts = (byte)minAttempts;
		this.maxAttempts = (byte)maxAttempts;
		return this;
	}
	
	public BlobPopulatorEndermanSpawner blockAmount(IRandomAmount blockAmountGen, int minBlockAmount, int maxBlockAmount){
		this.blockAmountGen = blockAmountGen;
		this.minBlockAmount = (byte)minBlockAmount;
		this.maxBlockAmount = (byte)maxBlockAmount;
		return this;
	}
	
	/**
	 * Amount of attempts per block to find a spot adjacent to air. After the attempts run out, it will choose the first location found.
	 */
	public BlobPopulatorEndermanSpawner visiblePlacementAttempts(int visiblePlacementAttempts){
		this.visiblePlacementAttempts = (byte)visiblePlacementAttempts;
		return this;
	}
	
	/**
	 * Populator will know all generated blocks and use those to place ores, attempt amount will still have an effect in cases when the random block is not End Stone.
	 */
	public BlobPopulatorEndermanSpawner knownBlockLocations(){
		this.knownBlockLocations = true;
		return this;
	}

	@Override
	public void generate(DecoratorFeatureGenerator gen, Random rand){
		int blocks = blockAmountGen.generate(rand,minBlockAmount,maxBlockAmount);
		List<BlockLocation> locs = knownBlockLocations ? gen.getUsedLocations() : null;
		
		for(int attempt = 0, attempts = minAttempts+rand.nextInt(maxAttempts-minAttempts+1), x, y, z; attempt < attempts && blocks > 0; attempt++){
			if (knownBlockLocations){
				if (locs.isEmpty())return;
				
				BlockLocation loc = locs.get(rand.nextInt(locs.size()));
				x = loc.x;
				y = loc.y;
				z = loc.z;
			}
			else{
				x = rand.nextInt(32)-16;
				y = rand.nextInt(32)-16;
				z = rand.nextInt(32)-16;
			}
			
			if (gen.getBlock(x,y,z) == Blocks.end_stone){
				if (visiblePlacementAttempts > 0){
					int origX = x, origY = y, origZ = z;
					
					for(int airAttempt = 0; airAttempt <= visiblePlacementAttempts; airAttempt++){
						if (gen.getBlock(x,y,z) == Blocks.end_stone && isAirAdjacent(gen,x,y,z))break;
						
						if (airAttempt == visiblePlacementAttempts){
							x = origX;
							y = origY;
							z = origZ;
							break;
						}
						
						x += rand.nextInt(6)-3;
						y += rand.nextInt(6)-3;
						z += rand.nextInt(6)-3;
					}
				}
				
				gen.setBlock(x,y,z,BlockList.custom_spawner,4);
				--blocks;
			}
		}
	}
	
	private boolean isAirAdjacent(DecoratorFeatureGenerator gen, int x, int y, int z){
		return gen.getBlock(x-1,y,z) == Blocks.air || gen.getBlock(x+1,y,z) == Blocks.air ||
			   gen.getBlock(x,y-1,z) == Blocks.air || gen.getBlock(x,y+1,z) == Blocks.air ||
			   gen.getBlock(x,y,z-1) == Blocks.air || gen.getBlock(x,y,z+1) == Blocks.air;
	}
}

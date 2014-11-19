package chylex.hee.world.feature.blobs.populators;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import chylex.hee.world.feature.blobs.BlobPopulator;
import chylex.hee.world.feature.util.DecoratorFeatureGenerator;
import chylex.hee.world.util.BlockLocation;
import chylex.hee.world.util.IRandomAmount;

public class BlobPopulatorPlant extends BlobPopulator{
	private Block plant;
	private IRandomAmount amountGen = IRandomAmount.exact;
	private byte minAttempts, maxAttempts, minPlantAmount, maxPlantAmount;
	private boolean knownBlockLocations;
	
	public BlobPopulatorPlant(int weight){
		super(weight);
	}
	
	public BlobPopulatorPlant block(Block plant){
		this.plant = plant;
		return this;
	}
	
	public BlobPopulatorPlant attempts(int minAttempts, int maxAttempts){
		this.minAttempts = (byte)minAttempts;
		this.maxAttempts = (byte)maxAttempts;
		return this;
	}
	
	public BlobPopulatorPlant blockAmount(IRandomAmount amountGen, int minPlantAmount, int maxPlantAmount){
		this.amountGen = amountGen;
		this.minPlantAmount = (byte)minPlantAmount;
		this.maxPlantAmount = (byte)maxPlantAmount;
		return this;
	}
	
	/**
	 * Populator will know all generated blocks and use those to place ores, attempt amount will have an effect in cases when the random block is not air above End Stone.
	 */
	public BlobPopulatorPlant knownBlockLocations(){
		this.knownBlockLocations = true;
		return this;
	}

	@Override
	public void generate(DecoratorFeatureGenerator gen, Random rand){
		int blocks = amountGen.generate(rand,minPlantAmount,maxPlantAmount);
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
			
			if (gen.getBlock(x,y+1,z) == Blocks.air && gen.getBlock(x,y,z) == Blocks.end_stone){
				gen.setBlock(x,y+1,z,plant);
				--blocks;
			}
		}
	}
}

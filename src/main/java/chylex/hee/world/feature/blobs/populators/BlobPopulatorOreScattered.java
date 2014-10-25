package chylex.hee.world.feature.blobs.populators;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import chylex.hee.world.feature.blobs.BlobPopulator;
import chylex.hee.world.feature.util.DecoratorFeatureGenerator;
import chylex.hee.world.util.BlockLocation;
import chylex.hee.world.util.IRandomAmount;

public class BlobPopulatorOreScattered extends BlobPopulator{
	private Block ore;
	private IRandomAmount blockAmountGen;
	private int minAttempts, maxAttempts, minBlockAmount, maxBlockAmount;
	private boolean knownBlockLocations;
	
	public BlobPopulatorOreScattered(int weight){
		super(weight);
	}
	
	public BlobPopulatorOreScattered block(Block ore){
		this.ore = ore;
		return this;
	}
	
	public BlobPopulatorOreScattered attempts(int minAttempts, int maxAttempts){
		this.minAttempts = minAttempts;
		this.maxAttempts = maxAttempts;
		return this;
	}
	
	public BlobPopulatorOreScattered blockAmount(IRandomAmount blockAmountGen, int minBlockAmount, int maxBlockAmount){
		this.blockAmountGen = blockAmountGen;
		this.minBlockAmount = minBlockAmount;
		this.maxBlockAmount = maxBlockAmount;
		return this;
	}
	
	/**
	 * Populator will know all generated blocks and use those to place ores, attempt amount will still have an effect in cases when the random block is not End Stone.
	 */
	public BlobPopulatorOreScattered knownBlockLocations(){
		this.knownBlockLocations = true;
		return this;
	}

	@Override
	public void generate(DecoratorFeatureGenerator gen, Random rand){
		int blocks = blockAmountGen.generate(rand,minBlockAmount,maxBlockAmount);
		List<BlockLocation> locs = null;
		
		for(int attempt = 0, attempts = minAttempts+rand.nextInt(maxAttempts-minAttempts+1), x, y, z; attempt < attempts && blocks > 0; attempt++){
			if (knownBlockLocations){
				if (attempt == 0)locs = gen.getUsedLocations();
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
				gen.setBlock(x,y,z,ore);
				--blocks;
			}
		}
	}
}

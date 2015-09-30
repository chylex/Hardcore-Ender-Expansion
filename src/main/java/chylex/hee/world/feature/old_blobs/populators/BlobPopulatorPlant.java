package chylex.hee.world.feature.old_blobs.populators;
import net.minecraft.block.Block;
import chylex.hee.world.feature.old_blobs.BlobPopulator;
import chylex.hee.world.util.RandomAmount;

public class BlobPopulatorPlant extends BlobPopulator{
	private Block plant;
	private RandomAmount amountGen = RandomAmount.exact;
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
	
	public BlobPopulatorPlant blockAmount(RandomAmount amountGen, int minPlantAmount, int maxPlantAmount){
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

	/*@Override
	public void generate(DecoratorFeatureGenerator gen, Random rand){
		int blocks = amountGen.generate(rand,minPlantAmount,maxPlantAmount);
		List<BlockPosM> locs = knownBlockLocations ? gen.getUsedLocations() : null;
		
		for(int attempt = 0, attempts = minAttempts+rand.nextInt(maxAttempts-minAttempts+1), x, y, z; attempt < attempts && blocks > 0; attempt++){
			if (knownBlockLocations){
				if (locs.isEmpty())return;
				
				BlockPosM loc = locs.get(rand.nextInt(locs.size()));
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
	}*/
}

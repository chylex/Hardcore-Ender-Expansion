package chylex.hee.world.feature.old_blobs.populators;
import net.minecraft.block.Block;
import chylex.hee.world.feature.old_blobs.BlobPopulator;

public class BlobPopulatorFiller extends BlobPopulator{
	private static final byte[] offX = new byte[]{ -1, 1, 0, 0, 0, 0 },
								offY = new byte[]{ 0, 0, -1, 1, 0, 0 },
								offZ = new byte[]{ 0, 0, 0, 0, -1, 1 };
	
	private Block block;
	
	public BlobPopulatorFiller(int weight){
		super(weight);
	}
	
	public BlobPopulatorFiller block(Block block){
		this.block = block;
		return this;
	}

	/*@Override
	public void generate(DecoratorFeatureGenerator gen, Random rand){
		List<BlockPosM> list = gen.getUsedLocations();
		
		for(Iterator<BlockPosM> iter = list.iterator(); iter.hasNext();){
			BlockPosM loc = iter.next();
			
			for(int a = 0; a < 6; a++){
				if (gen.getBlock(loc.x+offX[a],loc.y+offY[a],loc.z+offZ[a]) != Blocks.end_stone){
					iter.remove();
					break;
				}
			}
		}
		
		for(BlockPosM loc:list)gen.setBlock(loc.x,loc.y,loc.z,block);
	}*/
}

package chylex.hee.world.feature.blobs.populators;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import chylex.hee.world.feature.blobs.BlobPopulator;
import chylex.hee.world.feature.util.DecoratorFeatureGenerator;
import chylex.hee.world.util.BlockLocation;

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

	@Override
	public void generate(DecoratorFeatureGenerator gen, Random rand){
		List<BlockLocation> list = gen.getUsedLocations();
		
		for(Iterator<BlockLocation> iter = list.iterator(); iter.hasNext();){
			BlockLocation loc = iter.next();
			
			for(int a = 0; a < 6; a++){
				if (gen.getBlock(loc.x+offX[a],loc.y+offY[a],loc.z+offZ[a]) != Blocks.end_stone){
					iter.remove();
					break;
				}
			}
		}
		
		for(BlockLocation loc:list)gen.setBlock(loc.x,loc.y,loc.z,block);
	}
}

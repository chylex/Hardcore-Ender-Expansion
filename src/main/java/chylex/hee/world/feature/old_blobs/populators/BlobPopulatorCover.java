package chylex.hee.world.feature.old_blobs.populators;
import net.minecraft.block.Block;
import chylex.hee.world.feature.old_blobs.BlobPopulator;

public class BlobPopulatorCover extends BlobPopulator{
	private Block cover;
	private boolean replaceTopBlock;
	
	public BlobPopulatorCover(int weight){
		super(weight);
	}
	
	public BlobPopulatorCover block(Block cover){
		this.cover = cover;
		return this;
	}
	
	public BlobPopulatorCover replaceTopBlock(){
		this.replaceTopBlock = true;
		return this;
	}

	/*@Override
	public void generate(DecoratorFeatureGenerator gen, Random rand){
		for(int x = -16; x < 16; x++){
			for(int z = -16; z < 16; z++){
				int y = gen.getTopBlockY(x,z);
				if (y != Integer.MIN_VALUE)gen.setBlock(x,replaceTopBlock ? y : y+1,z,cover);
			}
		}
	}*/
}

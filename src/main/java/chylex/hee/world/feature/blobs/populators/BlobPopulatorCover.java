package chylex.hee.world.feature.blobs.populators;
import java.util.Random;
import net.minecraft.block.Block;
import chylex.hee.world.feature.blobs.BlobPopulator;
import chylex.hee.world.feature.util.DecoratorFeatureGenerator;

public class BlobPopulatorCover extends BlobPopulator{
	private Block cover;
	
	public BlobPopulatorCover(int weight){
		super(weight);
	}
	
	public BlobPopulatorCover block(Block cover){
		this.cover = cover;
		return this;
	}

	@Override
	public void generate(DecoratorFeatureGenerator gen, Random rand){
		for(int x = -16; x <= 16; x++){
			for(int z = -16; z <= 16; z++){
				int y = gen.getTopBlockY(x,z);
				if (y != -1)gen.setBlock(x,y+1,z,cover);
			}
		}
	}
}

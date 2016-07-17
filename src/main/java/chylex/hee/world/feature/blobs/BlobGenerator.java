package chylex.hee.world.feature.blobs;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import chylex.hee.system.collections.weight.IWeightProvider;
import chylex.hee.system.util.BlockPosM;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.feature.util.DecoratorFeatureGenerator;

public abstract class BlobGenerator implements IWeightProvider{
	private final int weight;
	
	public BlobGenerator(int weight){
		this.weight = weight;
	}
	
	@Override
	public int getWeight(){
		return weight;
	}
	
	public abstract void generate(DecoratorFeatureGenerator gen, Random rand);
	
	/**
	 * Returns true if at least one block has changed.
	 */
	public static final boolean genBlob(DecoratorFeatureGenerator gen, double x, double y, double z, double rad){
		return genBlob(gen,x,y,z,rad,Blocks.end_stone);
	}
	
	/**
	 * Returns true if at least one block has changed.
	 */
	public static final boolean genBlob(DecoratorFeatureGenerator gen, double x, double y, double z, double rad, Block block){
		boolean generatedSomething = false;
		double radSq = MathUtil.square(rad+0.5D);
		int size = MathUtil.ceil(rad), ix = MathUtil.floor(x), iy = MathUtil.floor(y), iz = MathUtil.floor(z);
		List<BlockPosM> locs = new ArrayList<>();
		
		for(int xx = ix-size; xx <= ix+size; xx++){
			for(int yy = iy-size; yy <= iy+size; yy++){
				for(int zz = iz-size; zz <= iz+size; zz++){
					if (MathUtil.distanceSquared(xx-x,yy-y,zz-z) <= radSq){
						if (gen.getBlock(xx,yy,zz) != block && gen.setBlock(xx,yy,zz,block)){
							generatedSomething = true;
							locs.add(new BlockPosM(xx,yy,zz));
						}
					}
				}
			}
		}
		
		return generatedSomething;
	}
}

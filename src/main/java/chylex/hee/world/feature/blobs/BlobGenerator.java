package chylex.hee.world.feature.blobs;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import chylex.hee.system.util.MathUtil;
import chylex.hee.system.weight.IWeightProvider;
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
	
	protected final void genBlob(DecoratorFeatureGenerator gen, double x, double y, double z, double rad){
		genBlob(gen,x,y,z,rad,Blocks.end_stone);
	}
	
	protected final void genBlob(DecoratorFeatureGenerator gen, double x, double y, double z, double rad, Block block){
		double radSq = MathUtil.square(rad+0.5D);
		int size = (int)Math.ceil(rad), ix = (int)Math.floor(x), iy = (int)Math.floor(y), iz = (int)Math.floor(z);
		
		for(int xx = ix-size; xx <= ix+size; xx++){
			for(int yy = iy-size; yy <= iy+size; yy++){
				for(int zz = iz-size; zz <= iz+size; zz++){
					if (MathUtil.distanceSquared(xx-x,yy-y,zz-z) <= radSq){
						gen.setBlock(xx,yy,zz,block);
					}
				}
			}
		}
	}
}

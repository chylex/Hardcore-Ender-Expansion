package chylex.hee.world.feature.blobs;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.feature.util.DecoratorFeatureGenerator;

public abstract class BlobGenerator{
	public abstract void generate(DecoratorFeatureGenerator gen, Random rand);
	
	protected final void genBlob(DecoratorFeatureGenerator gen, double x, double y, double z, double rad){
		double radSq = MathUtil.square(rad+0.5D);
		int size = (int)Math.ceil(rad), ix = (int)Math.floor(x), iy = (int)Math.floor(y), iz = (int)Math.floor(z);
		
		for(int xx = ix-size; xx <= ix+size; xx++){
			for(int yy = iy-size; yy <= iy+size; yy++){
				for(int zz = iz-size; zz <= iz+size; zz++){
					if (MathUtil.distanceSquared(xx-x,yy-y,zz-z) <= radSq){
						gen.setBlock(xx,yy,zz,Blocks.end_stone);
					}
				}
			}
		}
	}
}

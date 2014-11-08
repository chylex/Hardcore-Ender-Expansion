package chylex.hee.world.feature.blobs;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import chylex.hee.system.util.MathUtil;
import chylex.hee.system.weight.IWeightProvider;
import chylex.hee.world.feature.util.DecoratorFeatureGenerator;
import chylex.hee.world.util.BlockLocation;

public abstract class BlobGenerator implements IWeightProvider{
	private static final byte[] airOffX = new byte[]{ -1, 1, 0, 0, 0, 0 },
								airOffY = new byte[]{ 0, 0, 0, 0, -1, 1 },
								airOffZ = new byte[]{ 0, 0, -1, 1, 0, 0 };
	
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
		int size = (int)Math.ceil(rad), ix = (int)Math.floor(x), iy = (int)Math.floor(y), iz = (int)Math.floor(z);
		List<BlockLocation> locs = new ArrayList<BlockLocation>();
		
		for(int xx = ix-size; xx <= ix+size; xx++){
			for(int yy = iy-size; yy <= iy+size; yy++){
				for(int zz = iz-size; zz <= iz+size; zz++){
					if (MathUtil.distanceSquared(xx-x,yy-y,zz-z) <= radSq){
						if (gen.getBlock(xx,yy,zz) != block && gen.setBlock(xx,yy,zz,block)){
							generatedSomething = true;
							locs.add(new BlockLocation(xx,yy,zz));
						}
					}
				}
			}
		}
		
		if (generatedSomething && block != Blocks.air){
			for(BlockLocation loc:locs){
				int adjacentAir = 0;
				
				for(int a = 0; a < 6; a++){
					if (gen.getBlock(loc.x+airOffX[a],loc.y+airOffY[a],loc.z+airOffZ[a]) != block)++adjacentAir;
				}
				
				if (adjacentAir >= 4)gen.setBlock(loc.x,loc.y,loc.z,Blocks.air);
			}
		}
		
		return generatedSomething;
	}
}

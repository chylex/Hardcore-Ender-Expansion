package chylex.hee.world.feature.blobs;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.facing.Facing6;
import chylex.hee.system.collections.weight.IWeightProvider;
import chylex.hee.system.util.MathUtil;

public abstract class AbstractBlobHandler implements IWeightProvider{
	private final int weight;
	
	public AbstractBlobHandler(int weight){
		this.weight = weight;
	}
	
	@Override
	public int getWeight(){
		return weight;
	}
	
	protected static final boolean isBlobInsideWorld(StructureWorldBlob world, double x, double y, double z, double rad){
		int ix = MathUtil.floor(x), iy = MathUtil.floor(y), iz = MathUtil.floor(z), irad = MathUtil.ceil(rad)-1;
		if (!world.isInside(ix, iy, iz))return false;
		
		for(Facing6 facing:Facing6.list){
			if (!world.isInside(ix+facing.getX()*irad, iy+facing.getY()*irad, iz+facing.getZ()*irad))return false;
		}
		
		return true;
	}
	
	protected static final void generateBlob(StructureWorldBlob world, double x, double y, double z, double rad){
		generateBlob(world, x, y, z, rad, Blocks.end_stone);
	}
	
	protected static final void generateBlob(StructureWorldBlob world, double x, double y, double z, double rad, Block block){
		double radSq = MathUtil.square(rad+0.5D);
		int dist = MathUtil.ceil(radSq);
		Pos center = Pos.at(x, y, z);
		
		Pos.forEachBlock(center.offset(-dist, -dist, -dist), center.offset(dist, dist, dist), pos -> {
			if (MathUtil.distanceSquared(pos.x-x, pos.y-y, pos.z-z) <= radSq){
				world.setBlock(pos, block);
			}
		});
	}
	
	@FunctionalInterface
	protected static interface IRandGenerator{
		double generate(Random rand);
	}
}
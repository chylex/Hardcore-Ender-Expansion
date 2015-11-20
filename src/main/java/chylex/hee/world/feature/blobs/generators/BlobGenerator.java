package chylex.hee.world.feature.blobs.generators;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.collections.weight.IWeightProvider;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.feature.blobs.StructureWorldBlob;

public abstract class BlobGenerator implements IWeightProvider{
	private final int weight;
	
	public BlobGenerator(int weight){
		this.weight = weight;
	}
	
	@Override
	public final int getWeight(){
		return weight;
	}
	
	public abstract void generate(StructureWorldBlob world, Random rand);
	
	protected static final void generateBlob(StructureWorldBlob world, double x, double y, double z, float rad){
		double radSq = MathUtil.square(rad+0.5D);
		int dist = MathUtil.ceil(radSq);
		Pos center = Pos.at(x,y,z);
		
		Pos.forEachBlock(center.offset(-dist,-dist,-dist),center.offset(dist,dist,dist),pos -> {
			if (MathUtil.distanceSquared(pos.x-x,pos.y-y,pos.z-z) <= radSq){
				world.setBlock(pos.x,pos.y,pos.z,Blocks.end_stone);
			}
		});
	}
}

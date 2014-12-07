package chylex.hee.world.feature.blobs.populators;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.feature.blobs.BlobPopulator;
import chylex.hee.world.feature.util.DecoratorFeatureGenerator;

public class BlobPopulatorLake extends BlobPopulator{
	private Block liquid;
	private double minRadius, maxRadius;
	
	public BlobPopulatorLake(int weight){
		super(weight);
	}
	
	public BlobPopulatorLake block(Block liquid){
		this.liquid = liquid;
		return this;
	}
	
	public BlobPopulatorLake rad(double minRadius, double maxRadius){
		this.minRadius = minRadius;
		this.maxRadius = maxRadius;
		return this;
	}

	@Override
	public void generate(DecoratorFeatureGenerator gen, Random rand){
		double rad = minRadius+rand.nextDouble()*(maxRadius-minRadius);
		int irad = MathUtil.ceil(rad);
		
		for(int attempt = 0, x, y, z; attempt < 30; attempt++){ // TODO fix
			x = rand.nextInt(32)-16;
			y = rand.nextInt(32)-16;
			z = rand.nextInt(32)-16;
			
			while(gen.getBlock(x,y,z) != Blocks.end_stone && y > -16)--y;
			
			if (gen.getBlock(x,y,z) == Blocks.end_stone && gen.getBlock(x,y+1,z) == Blocks.air){
				for(int xx = x-irad; xx <= x+irad; xx++){
					for(int zz = z-irad; zz <= z+irad; zz++){
						if (gen.getBlock(xx,y,zz) == Blocks.end_stone && gen.getBlock(xx,y+1,zz) == Blocks.air &&
							gen.getBlock(xx-1,y,zz) != Blocks.air && gen.getBlock(xx+1,y,zz) != Blocks.air &&
							gen.getBlock(xx,y,zz-1) != Blocks.air && gen.getBlock(xx,y,zz+1) != Blocks.air &&
							MathUtil.distance(xx-x,zz-z) <= rad){
							gen.setBlock(xx,y,zz,liquid);
						}
					}
				}
				
				//break;
			}
		}
	}
}

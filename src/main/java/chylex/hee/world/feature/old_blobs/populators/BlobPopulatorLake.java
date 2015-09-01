package chylex.hee.world.feature.old_blobs.populators;
import net.minecraft.block.Block;
import chylex.hee.world.feature.old_blobs.BlobPopulator;

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

	/*@Override
	public void generate(DecoratorFeatureGenerator gen, Random rand){
		for(int attempt = 0, placed = 0, x, y, z; attempt < 90 && placed < 4; attempt++){
			x = rand.nextInt(32)-16;
			z = rand.nextInt(32)-16;
			if ((y = gen.getTopBlockY(x,z)) == Integer.MIN_VALUE)continue;
			
			if (gen.getBlock(x,y,z) == Blocks.end_stone && gen.getBlock(x,y+1,z) == Blocks.air){
				double rad = minRadius+rand.nextDouble()*(maxRadius-minRadius);
				int irad = MathUtil.ceil(rad);
				
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
				
				++placed;
			}
		}
	}*/
}

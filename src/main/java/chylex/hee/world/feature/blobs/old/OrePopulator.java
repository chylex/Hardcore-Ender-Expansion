package chylex.hee.world.feature.blobs.old;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import chylex.hee.world.feature.WorldGenBlobOld;

public class OrePopulator extends Populator{
	private Block oreBlock;
	private int blockAmount,iterations;
	
	public OrePopulator(Block oreBlock, int blockAmount, int iterations){
		this.oreBlock = oreBlock;
		this.blockAmount = blockAmount;
		this.iterations = iterations;
	}
	
	@Override
	public void populate(World world, Random rand, int x, int y, int z){
		boolean succeeded = false;
		
		for(int a = 0; a < iterations; a++){
			x = rand.nextInt(maxPos[0]-minPos[0])+minPos[0];
			y = rand.nextInt(maxPos[1]-minPos[1])+minPos[1];
			z = rand.nextInt(maxPos[2]-minPos[2])+minPos[2];
			
			// from WorldGenMinable, changed some values
			float f = rand.nextFloat()*(float)Math.PI;
			double d0 = ((x+3)+MathHelper.sin(f)*blockAmount*0.125F);
			double d1 = ((x+3)-MathHelper.sin(f)*blockAmount*0.125F);
			double d2 = ((z+3)+MathHelper.cos(f)*blockAmount*0.125F);
			double d3 = ((z+3)-MathHelper.cos(f)*blockAmount*0.125F);
			double d4 = (y+rand.nextInt(3)-2);
			double d5 = (y+rand.nextInt(3)-2);

			for(int l = 0; l <= blockAmount; ++l){
				double centerX = d0+(d1-d0)*l/blockAmount;
				double centerY = d4+(d5-d4)*l/blockAmount;
				double centerZ = d2+(d3-d2)*l/blockAmount;
				double maxDist = rand.nextDouble()*blockAmount*0.0625D;
				double area = (MathHelper.sin(l*(float)Math.PI/blockAmount)+1F)*maxDist+1D;
				
				int minX = MathHelper.floor_double(centerX-(area*0.5D));
				int minY = MathHelper.floor_double(centerY-(area*0.5D));
				int minZ = MathHelper.floor_double(centerZ-(area*0.5D));
				int maxX = MathHelper.floor_double(centerX+(area*0.5D));
				int maxY = MathHelper.floor_double(centerY+(area*0.5D));
				int maxZ = MathHelper.floor_double(centerZ+(area*0.5D));

				for(int xx = minX; xx <= maxX; ++xx){
					double d12 = (xx+0.5D-centerX)/(area*0.5D);

					if (d12*d12 < 1D){
						for(int yy = minY; yy <= maxY; ++yy){
							double d13 = (yy+0.5D-centerY)/(area*0.5D);

							if (d12*d12+d13*d13 < 1D){
								for(int zz = minZ; zz <= maxZ; ++zz){
									double d14 = (zz+0.5D-centerZ)/(area*0.5D);

									if (d12*d12+d13*d13+d14*d14 >= 1D)continue;
									
									if (getBlock(world,xx,yy,zz) == WorldGenBlobOld.filler){
										world.setBlock(xx,yy,zz,oreBlock,0,2);
										succeeded = true;
									}
								}
							}
						}
					}
				}
			}
			
			if (succeeded)break;
		}
	}
}

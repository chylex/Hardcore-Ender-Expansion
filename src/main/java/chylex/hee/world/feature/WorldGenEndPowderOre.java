package chylex.hee.world.feature;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import chylex.hee.block.BlockList;
import chylex.hee.system.util.BlockPosM;

public class WorldGenEndPowderOre extends WorldGenBase{
	@Override
	public boolean generate(World world, Random rand, BlockPosM pos){
		if (pos.getBlock(world) != Blocks.end_stone)return false;
		
		int blockAmount = rand.nextInt(5)+4;
		
		float randomAngle = rand.nextFloat()*(float)Math.PI;
		double genX1 = (pos.x+MathHelper.sin(randomAngle)*blockAmount);
		double genX2 = (pos.x-MathHelper.sin(randomAngle)*blockAmount);
		double genZ1 = (pos.z+MathHelper.cos(randomAngle)*blockAmount);
		double genZ2 = (pos.z-MathHelper.cos(randomAngle)*blockAmount);
		double genY1 = (pos.y+rand.nextInt(8)-4);
		double genY2 = (pos.y+rand.nextInt(8)-4);

		for(int a = 0, placed = 0; a <= blockAmount; ++a){
			double centerX = genX1+(genX2-genX1)*a/blockAmount;
			double centerY = genY1+(genY2-genY1)*a/blockAmount;
			double centerZ = genZ1+(genZ2-genZ1)*a/blockAmount;
			double maxDist = (rand.nextDouble()*blockAmount/2D)-1D;
			double area = (MathHelper.sin(a*(float)Math.PI/blockAmount)+1F)*maxDist+1D;
			
			int minX = MathHelper.floor_double(centerX-area/2D);
			int minY = MathHelper.floor_double(centerY-area/2D);
			int minZ = MathHelper.floor_double(centerZ-area/2D);
			int maxX = MathHelper.floor_double(centerX+area/2D);
			int maxY = MathHelper.floor_double(centerY+area/2D);
			int maxZ = MathHelper.floor_double(centerZ+area/2D);

			for(int xx = minX; xx <= maxX; ++xx){
				double d12 = (xx+0.5D-centerX)/(area/2D);

				if (d12*d12 < 1D){
					for(int yy = minY; yy <= maxY; ++yy){
						double d13 = (yy+0.5D-centerY)/(area/2D);

						if (d12*d12+d13*d13 < 1D){
							for(int zz = minZ; zz <= maxZ; ++zz){
								double d14 = (zz+0.5D-centerZ)/(area/2D);

								if (d12*d12+d13*d13+d14*d14 < 1D && pos.moveTo(xx,yy,zz).getBlock(world) == Blocks.end_stone && rand.nextInt(6) == 0){
									pos.setBlock(world,BlockList.end_powder_ore,2);
									if (++placed > blockAmount-rand.nextInt(3)+rand.nextInt(4))return true;
								}
							}
						}
					}
				}
			}
		}
		
		return true;
	}
}

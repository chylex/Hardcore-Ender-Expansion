package chylex.hee.world.structure.island.biome.feature.island;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.block.BlockList;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;

public class StructureGooLake extends AbstractIslandStructure{
	@Override
	protected boolean generate(Random rand){
		int x = getRandomXZ(rand,16),
			z = getRandomXZ(rand,16),
			y = world.getHighestY(x,z),
			yOff = 0,
			minY = y+3;
		
		double rad = 2D+rand.nextDouble()*3D+rand.nextDouble()*3D,//(biome.hasRareVariation(RareVariationIsland.LARGE_LAKES)?8D:3D),
			   hrad = rad*0.5D;
		boolean canGenerate = true;
		
		for(int testX = (int)Math.floor(x-rad); testX <= x+rad; testX++){
			for(int testZ = (int)Math.floor(z-rad); testZ <= z+rad; testZ++){
				boolean found = false;
				
				for(int testY = y+2; testY >= y-2; testY--){
					if (world.isAir(testX,testY,testZ) && world.getBlock(testX,testY-1,testZ) == surface()){
						found = true;
						if (testY < minY)minY = testY;
						break;
					}
				}
				
				if (!found){
					testX += 999;
					canGenerate = false;
					break;
				}
			}
		}
		
		if (!canGenerate)return false;
		
		--minY;
		
		for(int a = 0; a < 3+rad+rand.nextInt(5); a++,yOff = 0){
			double cx = x+Math.min(hrad,Math.abs(rand.nextGaussian()*0.7D*a))*(rand.nextInt(2)*2D-1D),
				   cz = z+Math.min(hrad,Math.abs(rand.nextGaussian()*0.7D*a))*(rand.nextInt(2)*2D-1D),
				   r = hrad*(0.25D+rand.nextDouble()*0.75D);
			
			for(int iteration = 0; iteration < Math.min(1,rand.nextInt(3)); iteration++){
				if (iteration == 1){
					r *= 0.75D;
					yOff = -1;
				}
				
				for(int px = (int)Math.floor(cx-r); px <= cx+r; px++){
					for(int pz = (int)Math.floor(cz-r); pz <= cz+r; pz++){
						if (MathUtil.distance(px-cx,pz-cz) < r-0.3D+rand.nextDouble()*0.7D){
							for(int py = minY+yOff; py < y+2; py++){
								if (py <= minY)world.setBlock(px,py,pz,BlockList.ender_goo);
								else world.setBlock(px,py,pz,Blocks.air);
							}
						}
					}
				}
			}
		}
		
		return true;
	}
}

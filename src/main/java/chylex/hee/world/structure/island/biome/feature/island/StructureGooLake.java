package chylex.hee.world.structure.island.biome.feature.island;
import java.util.Random;
import net.minecraft.init.Blocks;
import chylex.hee.block.BlockList;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.island.biome.IslandBiomeEnchantedIsland;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;

public class StructureGooLake extends AbstractIslandStructure{
	@Override
	protected boolean generate(Random rand){
		int x = getRandomXZ(rand,16),
			z = getRandomXZ(rand,16),
			y = world.getHighestY(x,z)+1,
			yOff = 0,
			minY = y+3;
		
		if (y <= 1)return false;
		
		boolean isSwamp = biomeData.hasDeviation(IslandBiomeEnchantedIsland.GOO_SWAMP);
		
		double rad = (isSwamp ? 4D*(rand.nextDouble()+0.8D) : 2D)+rand.nextDouble()*3D+rand.nextDouble()*3D,
			   hrad = rad*0.5D;
		boolean canGenerate = true;
		
		for(int testX = MathUtil.floor(x-rad); testX <= x+rad; testX++){
			for(int testZ = MathUtil.floor(z-rad); testZ <= z+rad; testZ++){
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
		
		for(int a = 0; a < 3+rad+rand.nextInt(isSwamp ? 10 : 5); a++,yOff = 0){
			double cx = x+Math.min(hrad,Math.abs(rand.nextGaussian()*0.7D*a))*(rand.nextInt(2)*2D-1D),
				   cz = z+Math.min(hrad,Math.abs(rand.nextGaussian()*0.7D*a))*(rand.nextInt(2)*2D-1D),
				   r = hrad*(0.25D+rand.nextDouble()*0.75D);
			
			for(int iteration = 0; iteration < Math.min(1,rand.nextInt(3)); iteration++){
				if (iteration == 1){
					r *= 0.75D;
					yOff = -1;
				}
				
				for(int px = MathUtil.floor(cx-r); px <= cx+r; px++){
					for(int pz = MathUtil.floor(cz-r); pz <= cz+r; pz++){
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

package chylex.hee.world.structure.island.biome.feature.mountains;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import chylex.hee.block.BlockList;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.island.biome.IslandBiomeBurningMountains;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;

public class StructureCinderPatch extends AbstractIslandStructure{
	@Override
	protected boolean generate(Random rand){
		int x = getRandomXZ(rand,24), z = getRandomXZ(rand,24), y = 5+rand.nextInt(60);
		if (world.getBlock(x,y,z) != Blocks.end_stone)return false;
		
		double rad = 3.5D+rand.nextDouble()*rand.nextDouble()*7D*(biomeData.hasDeviation(IslandBiomeBurningMountains.EXCESSIVE_CINDER) ? 1.5D : 1D);
		generateBlob(rand,x,y,z,rad);
		
		for(int attempt = 0, attemptAmount = rand.nextInt(4)*rand.nextInt(4); attempt < attemptAmount; attempt++){
			generateBlob(rand,x+(rand.nextDouble()-0.5D)*1.2D*rad,y+(rand.nextDouble()-0.5D)*1.2D*rad,z+(rand.nextDouble()-0.5D)*1.2D*rad,3D+rand.nextDouble()*(rad-2D));
		}
		
		return true;
	}
	
	private void generateBlob(Random rand, double x, double y, double z, double rad){
		int xx, yy, zz;

		for(xx = MathUtil.floor(x-rad)-1; xx <= x+rad+1; xx++){
			for(yy = MathUtil.floor(y-rad)-1; yy <= y+rad+1; yy++){
				if (yy <= 0)continue;
				
				for(zz = MathUtil.floor(z-rad)-1; zz <= z+rad+1; zz++){
					if (MathUtil.distance(xx-x,yy-y,zz-z) <= rad-rand.nextDouble()){
						Block block = world.getBlock(xx,yy,zz);
						if (block == Blocks.end_stone || block == BlockList.end_terrain)world.setBlock(xx,yy,zz,BlockList.cinder);
					}
				}
			}
		}
	}
}

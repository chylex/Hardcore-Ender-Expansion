package chylex.hee.world.feature;
import java.util.Random;
import chylex.hee.block.BlockList;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenEnergyCluster extends WorldGenerator{
	@Override
	public boolean generate(World world, Random rand, int x, int y, int z){
		for(int attempt = 0, xx, yy, zz; attempt < 30; attempt++){
			xx = x+4+rand.nextInt(8);
			yy = 10+rand.nextInt(108);
			zz = z+4+rand.nextInt(8);
			
			if (world.isAirBlock(xx,yy,zz)){
				boolean foundBlock = false;
				
				for(int check = 0; check < 10; check++){
					if (!world.isAirBlock(xx+rand.nextInt(9)-4,yy+rand.nextInt(9)-4,zz+rand.nextInt(9)-4)){
						foundBlock = true;
						break;
					}
				}
				
				if (foundBlock || rand.nextInt(88) == 0){
					world.setBlock(xx,yy,zz,BlockList.energy_cluster);
					return true;
				}
			}
		}
		
		return false;
	}
}

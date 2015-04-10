package chylex.hee.world.feature;
import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import chylex.hee.block.BlockList;
import chylex.hee.system.util.BlockPosM;

public class WorldGenEnergyCluster extends WorldGenerator{
	@Override
	public boolean generate(World world, Random rand, int x, int y, int z){
		BlockPosM tmpPos = BlockPosM.tmp();
		
		for(int attempt = 0; attempt < 50; attempt++){
			tmpPos.set(x+4+rand.nextInt(8),10+rand.nextInt(108),z+4+rand.nextInt(8));
			
			if (tmpPos.isAir(world)){
				boolean foundBlock = false;
				BlockPosM testPos = new BlockPosM();
				
				for(int check = 0; check < 10; check++){
					if (!testPos.set(tmpPos).move(rand.nextInt(9)-4,rand.nextInt(9)-4,rand.nextInt(9)-4).isAir(world)){
						foundBlock = true;
						break;
					}
				}
				
				if (foundBlock || rand.nextInt(88) == 0){
					tmpPos.setBlock(world,BlockList.energy_cluster);
					return true;
				}
			}
		}
		
		return false;
	}
}

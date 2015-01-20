package chylex.hee.world.feature;
import java.util.Random;
import net.minecraft.world.World;
import chylex.hee.block.BlockList;
import chylex.hee.system.util.BlockPosM;

public class WorldGenEnergyCluster extends WorldGenBase{
	@Override
	public boolean generate(World world, Random rand, BlockPosM pos){
		BlockPosM tmpPos = pos.copy();
		
		for(int attempt = 0, xx, yy, zz; attempt < 50; attempt++){
			if (tmpPos.moveTo(pos.x+4+rand.nextInt(8),10+rand.nextInt(108),pos.z+4+rand.nextInt(8)).isAir(world)){
				BlockPosM checkPos = tmpPos.copy();
				boolean foundBlock = false;
				
				for(int check = 0; check < 10; check++){
					if (!checkPos.moveTo(tmpPos.x+rand.nextInt(9)-4,tmpPos.y+rand.nextInt(9)-4,tmpPos.z+rand.nextInt(9)-4).isAir(world)){
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

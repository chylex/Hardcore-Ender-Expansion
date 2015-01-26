package chylex.hee.world.structure.island.biome.feature.forest;
import java.util.Random;
import chylex.hee.system.util.Direction;
import chylex.hee.block.BlockList;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;

public class StructureBush extends AbstractIslandStructure{
	@Override
	protected boolean generate(Random rand){
		int x = getRandomXZ(rand,1), z = getRandomXZ(rand,1), y = 0;
		
		for(int yAttempt = 0; yAttempt < 20; yAttempt++){
			y = 10+rand.nextInt(50);
			
			if (world.getBlock(x,y,z) == surface())break;
			else if (yAttempt == 19)return false;
		}
		
		world.setBlock(x,y+1,z,BlockList.spooky_log);
		world.setBlock(x,y+2,z,BlockList.spooky_leaves);
		
		for(int dir = 0; dir < 4; dir++){
			if (world.isAir(x+Direction.offsetX[dir],y+1,z+Direction.offsetZ[dir]) && !world.isAir(x+Direction.offsetX[dir],y,z+Direction.offsetZ[dir])){
				world.setBlock(x+Direction.offsetX[dir],y+1,z+Direction.offsetZ[dir],BlockList.spooky_leaves);
			}
		}
		
		return true;
	}
}
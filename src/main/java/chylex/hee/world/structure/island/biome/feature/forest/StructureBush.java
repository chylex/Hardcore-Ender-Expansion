package chylex.hee.world.structure.island.biome.feature.forest;
import java.util.Random;
import net.minecraft.util.Direction;
import chylex.hee.block.BlockList;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;

public class StructureBush extends AbstractIslandStructure{
	@Override
	protected boolean generate(Random rand){
		int xx = getRandomXZ(rand,1), zz = getRandomXZ(rand,1), yy = 0;
		
		for(int yAttempt = 0; yAttempt < 20; yAttempt++){
			yy = 10+rand.nextInt(50);
			
			if (world.getBlock(xx,yy,zz) == surface())break;
			else if (yAttempt == 19)return false;
		}
		
		world.setBlock(xx,yy+1,zz,BlockList.spooky_log);
		world.setBlock(xx,yy+2,zz,BlockList.spooky_leaves);
		
		for(int dir = 0; dir < 4; dir++){
			if (world.isAir(xx+Direction.offsetX[dir],yy+1,zz+Direction.offsetZ[dir]) && !world.isAir(xx+Direction.offsetX[dir],yy,zz+Direction.offsetZ[dir])){
				world.setBlock(xx+Direction.offsetX[dir],yy+1,zz+Direction.offsetZ[dir],BlockList.spooky_leaves);
			}
		}
		
		return true;
	}
}
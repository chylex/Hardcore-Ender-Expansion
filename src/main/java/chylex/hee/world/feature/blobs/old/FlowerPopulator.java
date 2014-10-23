package chylex.hee.world.feature.blobs.old;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import chylex.hee.world.feature.WorldGenBlobOld;

public class FlowerPopulator extends Populator{
	private Block flower;
	
	public FlowerPopulator(Block flower){
		this.flower = flower;
	}
	
	@Override
	public void populate(World world, Random rand, int x, int y, int z){
		int planeAreaSquared = (int)Math.floor(Math.sqrt(size[0]*size[2]))>>1;
		for(int a = 0; a < planeAreaSquared; a++){
			int xx = rand.nextInt(maxPos[0]-minPos[0])+minPos[0],
				zz = rand.nextInt(maxPos[2]-minPos[2])+minPos[2],yy;
			boolean can = false;
			
			for(yy = maxPos[1]; yy > minPos[1]; yy--){
				if (getBlock(world,xx,yy,zz) == WorldGenBlobOld.filler){
					can = true;
					break;				
				}
			}
			
			if (can)world.setBlock(xx,yy+1,zz,flower);
		}
	}
}

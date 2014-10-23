package chylex.hee.world.feature.blobs;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import chylex.hee.world.feature.WorldGenBlobOld;

public class ObsidianSpikePopulator extends Populator{
	@Override
	public void populate(World world, Random rand, int x, int y, int z){
		float xx,yy,zz;
		int spikes = 0,justInCase = 20;
		for(int a = 0,ix,iy,iz; a < rand.nextInt(6)+rand.nextInt(3)+2; a++){
			xx = (maxPos[0]-minPos[0])/2+minPos[0];
			yy = (maxPos[1]-minPos[1])/2+minPos[1];
			zz = (maxPos[2]-minPos[2])/2+minPos[2];
			
			double a1 = rand.nextDouble()*Math.PI*2D,a2 = rand.nextDouble()*Math.PI*2D;
			double xadd = Math.cos(a1),yadd = Math.sin(a2),zadd = Math.sin(a1);
			
			while(justInCase > 0){
				ix = (int)Math.floor(xx);
				iy = (int)Math.floor(yy);
				iz = (int)Math.floor(zz);

				--justInCase;
				if (!isInRange(ix,iy,iz))continue;
				
				if (getBlock(world,ix,iy,iz) == WorldGenBlobOld.filler){
					world.setBlock(ix,iy,iz,Blocks.obsidian);
				}
				xx += xadd;
				yy += yadd;
				zz += zadd;
			}

			justInCase = 20;
			
			if (++spikes > 7)break;
		}
	}
}

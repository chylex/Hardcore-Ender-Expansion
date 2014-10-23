package chylex.hee.world.feature.blobs.old;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import chylex.hee.world.feature.WorldGenBlobOld;

public class LakePopulator extends Populator{
	private Block liquid;
	
	public LakePopulator(Block liquid){
		this.liquid = liquid;
	}
	
	@Override
	public void populate(World world, Random rand, int x, int y, int z){
		for(int attempt = 0; attempt < 3; attempt++){
			int px = rand.nextInt(maxPos[0]-minPos[0])+minPos[0],
				pz = rand.nextInt(maxPos[2]-minPos[2])+minPos[2],
				py = maxPos[1];
			boolean found = false;
			
			for(; py > minPos[1]; py--){
				if (getBlock(world,px,py,pz) == WorldGenBlobOld.filler){
					found = true;
					break;
				}
			}
			
			if (!found)continue;
			
			for(int xx = minPos[0]; xx <= maxPos[0]; xx++){
				for(int zz = minPos[2]; zz <= maxPos[2]; zz++){
					if (isBlockSuitable(world,xx,py,zz))world.setBlock(xx,py,zz,liquid);
				}
			}
			
			if (rand.nextInt(3) != 0)break;
		}
	}
	
	private boolean isBlockSuitable(World world, int x, int y, int z){
		if (!(isInRange(x-1,y,z-1) && isInRange(x+1,y,z+1)) || getBlock(world,x,y+1,z) != Blocks.air)return false;
		return getBlock(world,x+1,y,z) != Blocks.air && getBlock(world,x-1,y,z) != Blocks.air&&
			   getBlock(world,x,y,z+2) != Blocks.air && getBlock(world,x,y,z-1) != Blocks.air;
	}
}

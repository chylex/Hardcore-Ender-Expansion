package chylex.hee.world.feature.blobs.old;
import java.util.Random;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import chylex.hee.system.util.MathUtil;

public class CavePopulator extends Populator{
	@Override
	public void populate(World world, Random rand, int x, int y, int z){
		for(int a = 0; a < rand.nextInt(3)+1; a++){
			createCave(world,rand,x,y,z);
		}
	}
	
	private void createCave(World world, Random rand, int x, int y, int z){
		float sx = 0,sy = 0,sz = 0;
		int r = rand.nextInt(6);
		
		switch(r){
			case 0:
			case 1: sx = rand.nextInt(maxPos[0]-minPos[0])+minPos[0];
					sy = rand.nextInt(maxPos[1]-minPos[1])+minPos[1];
					sz = r == 0?minPos[2]:maxPos[2];
					break;
			case 2:
			case 3: sx = rand.nextInt(maxPos[0]-minPos[0])+minPos[0];
					sz = rand.nextInt(maxPos[2]-minPos[2])+minPos[2];
					sy = r == 2?minPos[1]:maxPos[1];
					break;
			case 4:
			case 5: sz = rand.nextInt(maxPos[2]-minPos[2])+minPos[2];
					sy = rand.nextInt(maxPos[1]-minPos[1])+minPos[1];
					sx = r == 4?minPos[0]:maxPos[0];
					break;
		}

		Vec3 vec = Vec3.createVectorHelper(x-sx,y-sy,z-sz).normalize();
		
		float rad = 0.25F*(float)Math.sqrt(size[0]+size[1]+size[2]);
		if (rad < 1.1F)return;

		for(int a = 0; a < 100; a++){
			if (!createAirBlob(world,rand,(int)Math.floor(sx),(int)Math.floor(sy),(int)Math.floor(sz),rad))break;
			
			vec.xCoord += 0.05F*(rand.nextDouble()-0.5D);
			vec.yCoord += 0.05F*(rand.nextDouble()-0.5D);
			vec.zCoord += 0.05F*(rand.nextDouble()-0.5D);
			
			sx += vec.xCoord;
			sy += vec.yCoord;
			sz += vec.zCoord;
		}
	}
	
	private boolean createAirBlob(World world, Random rand, int x, int y, int z, float rad){
		rad += rand.nextFloat()*0.2F;
		if (rand.nextInt(60) == 0)rad *= 2F;
		boolean oneInRange = false;
		int ix,iy,iz;
		
		for(float xx = x-rad; xx <= x+rad; xx++){
			for(float yy = y-rad; yy <= y+rad; yy++){
				for(float zz = z-rad; zz <= z+rad; zz++){
					ix = (int)Math.floor(xx);
					iy = (int)Math.floor(yy);
					iz = (int)Math.floor(zz);
					
					if (Math.sqrt(MathUtil.square(xx-x)+MathUtil.square(yy-y)+MathUtil.square(zz-z)) < rad+rand.nextFloat()*0.2F && isInRange(ix,iy,iz) && world.isAirBlock(ix,iy,iz)){
						oneInRange = true;
						world.setBlockToAir(ix,iy,iz);
					}
				}
			}
		}
		
		return oneInRange;
	}
}

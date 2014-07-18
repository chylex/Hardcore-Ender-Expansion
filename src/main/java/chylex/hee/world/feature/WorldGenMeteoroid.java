package chylex.hee.world.feature;
import java.util.Random;
import chylex.hee.block.BlockList;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

public class WorldGenMeteoroid extends WorldGenerator{
	@Override
	public boolean generate(World world, Random rand, int x, int y, int z){
		int xx,yy,zz;
		
		for(xx = x-1; xx <= x+1; xx++){
			for(zz = z-1; zz <= z+1; zz++){
				for(yy = y-1; yy <= y+1; yy++){
					if (!canPlaceAt(world,xx,yy,zz))return false;
				}
			}
		}
		
		xx = x;
		yy = y;
		zz = z;
		
		world.setBlock(xx,yy,zz,BlockList.sphalerite,0,2);
		
		for(int iteration = 0, attempt, extraIterations = rand.nextInt(15); iteration < 8+extraIterations+rand.nextInt(9); iteration++){
			for(attempt = 0; attempt < rand.nextInt(12); attempt++){
				xx += rand.nextInt(3) != 0 ? 0 : rand.nextInt(3)-1;
				yy += rand.nextInt(3) != 0 ? 0 : rand.nextInt(3)-1;
				zz += rand.nextInt(3) != 0 ? 0 : rand.nextInt(3)-1;
				
				if (canPlaceAt(world,xx,yy,zz) && checkBoundaries(x,y,z,xx,yy,zz))world.setBlock(xx,yy,zz,BlockList.sphalerite,0,2);
			}
			
			if (rand.nextBoolean()){
				for(attempt = 0; attempt < 1+rand.nextInt(3); attempt++){
					xx += rand.nextInt(3) != 0 ? 0 : rand.nextInt(3)-1;
					yy += rand.nextInt(3) != 0 ? 0 : rand.nextInt(3)-1;
					zz += rand.nextInt(3) != 0 ? 0 : rand.nextInt(3)-1;
					
					if (canPlaceAt(world,xx,yy,zz) && checkBoundaries(x,y,z,xx,yy,zz))world.setBlock(xx,yy,zz,BlockList.sphalerite,1,2);
				}
			}
			
			xx = x+(2*rand.nextInt(2))-1;
			yy = y+(2*rand.nextInt(2))-1;
			zz = z+(2*rand.nextInt(2))-1;
		}
		
		return true;
	}
	
	private boolean canPlaceAt(World world, int x, int y, int z){
		Block block = world.getBlock(x,y,z);
		return block.getMaterial() == Material.air || block == Blocks.end_stone;
	}
	
	private boolean checkBoundaries(int xCenter, int yCenter, int zCenter, int x, int y, int z){
		return Math.abs(x-xCenter) <= 7 && Math.abs(y-yCenter) <= 7 && Math.abs(z-zCenter) <= 7;
	}
}

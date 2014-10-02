package chylex.hee.world.feature;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import chylex.hee.block.BlockList;
import chylex.hee.system.util.MathUtil;

public class WorldGenMeteoroid extends WorldGenerator{
	@Override
	public boolean generate(World world, Random rand, int x, int y, int z){
		int xx, yy, zz;
		
		for(xx = x-2; xx <= x+2; xx++){
			for(zz = z-2; zz <= z+2; zz++){
				for(yy = y-2; yy <= y+2; yy++){
					if (!canPlaceAt(world,xx,yy,zz))return false;
				}
			}
		}
		
		world.setBlock(x,y,z,BlockList.sphalerite,1,2);
		
		double dx, dy, dz, angH, angHCos, angHSin, angVCos, rad = 3.5D+rand.nextDouble()*3.2D;
		float fillFactor = 0.4F+rand.nextFloat()*0.35F, stardustChance = 0.12F+rand.nextFloat()*rand.nextFloat()*0.15F;
		
		for(int attempt = 0, maxAttempts = 20+(int)(rad+rad*rad*6), block, lineBlocks = (int)Math.ceil(rad/0.7D); attempt < maxAttempts; attempt++){
			dx = x+0.5D;
			dy = y+0.5D;
			dz = z+0.5D;
			angH = rand.nextDouble()*Math.PI*2D;
			angHCos = Math.cos(angH)*0.7D;
			angHSin = Math.sin(angH)*0.7D;
			angVCos = Math.cos(rand.nextDouble()*Math.PI*2D)*0.7D;
			
			for(block = 0; block < lineBlocks; block++){
				if (rand.nextFloat() >= fillFactor)continue;
				
				xx = (int)(dx += angHCos);
				yy = (int)(dy += angVCos);
				zz = (int)(dz += angHSin);
				
				if (canPlaceAt(world,xx,yy,zz) && MathUtil.distance(dx-xx,dy-yy,dz-zz) <= rad){
					world.setBlock(xx,yy,zz,BlockList.sphalerite,rand.nextFloat() < stardustChance ? 1 : 0,2);
				}
			}
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

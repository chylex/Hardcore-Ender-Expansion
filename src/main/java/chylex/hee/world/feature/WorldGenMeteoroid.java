package chylex.hee.world.feature;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import chylex.hee.block.BlockList;
import chylex.hee.system.util.BlockPosM;
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
		
		BlockPosM.tmp(x,y,z).setBlock(world,BlockList.sphalerite,1,2);
		
		double dx, dy, dz, addX, addY, addZ, rad = 3.6D+rand.nextDouble()*2.8D;
		float fillFactor = 0.2F+rand.nextFloat()*0.35F, stardustChance = 0.12F+rand.nextFloat()*rand.nextFloat()*0.15F;
		
		for(int attempt = 0, maxAttempts = 20+(int)(rad+rad*rad*5), block, lineBlocks = MathUtil.ceil(rad/0.9D); attempt < maxAttempts; attempt++){
			dx = x+0.5D;
			dy = y+0.5D;
			dz = z+0.5D;
			addX = (rand.nextDouble()-rand.nextDouble())*0.9D;
			addY = (rand.nextDouble()-rand.nextDouble())*0.9D;
			addZ = (rand.nextDouble()-rand.nextDouble())*0.9D;
			
			for(block = 0; block < lineBlocks; block++){
				if (rand.nextFloat() >= fillFactor)continue;
				
				xx = (int)(dx += (addX *= rand.nextDouble()*0.2D+0.8D));
				yy = (int)(dy += (addY *= rand.nextDouble()*0.2D+0.8D));
				zz = (int)(dz += (addZ *= rand.nextDouble()*0.2D+0.8D));
				
				if (canPlaceAt(world,xx,yy,zz) && MathUtil.distance(xx-x,yy-y,zz-z) <= rad){
					BlockPosM.tmp(xx,yy,zz).setBlock(world,BlockList.sphalerite,rand.nextFloat() < stardustChance ? 1 : 0,2);
				}
			}
		}

		return true;
	}
	
	private boolean canPlaceAt(World world, int x, int y, int z){
		Block block = BlockPosM.tmp(x,y,z).getBlock(world);
		return block.getMaterial() == Material.air || block == Blocks.end_stone;
	}
}

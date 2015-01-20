package chylex.hee.world.feature;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import chylex.hee.block.BlockList;
import chylex.hee.block.BlockSphalerite;
import chylex.hee.system.util.BlockPosM;
import chylex.hee.system.util.MathUtil;

public class WorldGenMeteoroid extends WorldGenBase{
	@Override
	public boolean generate(World world, Random rand, BlockPosM pos){
		BlockPosM tmpPos = pos.copy();
		
		for(tmpPos.x = pos.x-2; tmpPos.x <= pos.x+2; tmpPos.x++){
			for(tmpPos.z = pos.z-2; tmpPos.z <= pos.z+2; tmpPos.z++){
				for(tmpPos.y = pos.y-2; tmpPos.y <= pos.y+2; tmpPos.y++){
					if (!canPlaceAt(world,tmpPos))return false;
				}
			}
		}
		
		pos.setBlock(world,BlockList.sphalerite.setProperty(BlockSphalerite.Variant.STARDUST),2);
		
		double dx, dy, dz, addX, addY, addZ, rad = 3.6D+rand.nextDouble()*2.8D;
		float fillFactor = 0.2F+rand.nextFloat()*0.35F, stardustChance = 0.12F+rand.nextFloat()*rand.nextFloat()*0.15F;
		
		for(int attempt = 0, maxAttempts = 20+(int)(rad+rad*rad*5), block, lineBlocks = MathUtil.ceil(rad/0.9D); attempt < maxAttempts; attempt++){
			dx = pos.x+0.5D;
			dy = pos.y+0.5D;
			dz = pos.z+0.5D;
			addX = (rand.nextDouble()-rand.nextDouble())*0.9D;
			addY = (rand.nextDouble()-rand.nextDouble())*0.9D;
			addZ = (rand.nextDouble()-rand.nextDouble())*0.9D;
			
			for(block = 0; block < lineBlocks; block++){
				if (rand.nextFloat() >= fillFactor)continue;
				
				tmpPos.x = (int)(dx += (addX *= rand.nextDouble()*0.2D+0.8D));
				tmpPos.y = (int)(dy += (addY *= rand.nextDouble()*0.2D+0.8D));
				tmpPos.z = (int)(dz += (addZ *= rand.nextDouble()*0.2D+0.8D));
				
				if (canPlaceAt(world,tmpPos) && MathUtil.distance(tmpPos.x-pos.x,tmpPos.y-pos.y,tmpPos.z-pos.z) <= rad){
					tmpPos.setBlock(world,BlockList.sphalerite.setProperty(rand.nextFloat() < stardustChance ? BlockSphalerite.Variant.STARDUST : BlockSphalerite.Variant.NORMAL),2);
				}
			}
		}

		return true;
	}
	
	private boolean canPlaceAt(World world, BlockPosM pos){
		return pos.getBlockMaterial(world) == Material.air || pos.getBlock(world) == Blocks.end_stone;
	}
}

package chylex.hee.world.feature;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import chylex.hee.block.BlockList;

public class WorldGenEndiumOre extends WorldGenerator{
	@Override
	public boolean generate(World world, Random rand, int x, int y, int z){		
		if (world.getBlock(x,y,z) != Blocks.end_stone)return false;
		
		for(int check = 0, xx = x, yy = y, zz = z; check < 25; check++){
			if (world.getBlock(xx,yy,zz) == Blocks.end_stone && (world.isAirBlock(x-1,y,z) || world.isAirBlock(x+1,y,z) || world.isAirBlock(x,y,z-1) || world.isAirBlock(x,y,z+1) || (check > 15 && rand.nextInt(3) == 0))){
				world.setBlock(xx,yy,zz,BlockList.endium_ore);
				return true;
			}
			
			xx = x+rand.nextInt(8)-4;
			yy = y+rand.nextInt(8)-4;
			zz = z+rand.nextInt(8)-4;
		}
		
		return false;
	}
}

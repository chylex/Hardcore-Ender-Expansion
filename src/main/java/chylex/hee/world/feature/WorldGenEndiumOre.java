package chylex.hee.world.feature;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import chylex.hee.block.BlockList;
import chylex.hee.system.util.BlockPosM;

public class WorldGenEndiumOre extends WorldGenBase{
	@Override
	public boolean generate(World world, Random rand, BlockPosM pos){		
		if (pos.getBlock(world) != Blocks.end_stone)return false;
		
		BlockPosM checkPos = pos.copy();
		BlockPosM airPos = checkPos.copy();
		
		for(int check = 0; check < 25; check++){
			
			if (checkPos.getBlock(world) == Blocks.end_stone && ((check > 15 && rand.nextInt(3) == 0) ||
				airPos.moveTo(checkPos).moveEast().isAir(world) || airPos.moveTo(checkPos).moveWest().isAir(world) ||
				airPos.moveTo(checkPos).moveNorth().isAir(world) || airPos.moveTo(checkPos).moveSouth().isAir(world))){
				checkPos.setBlock(world,BlockList.endium_ore);
				return true;
			}
			
			checkPos.moveTo(pos.x+rand.nextInt(9)-4,pos.y+rand.nextInt(9)-4,pos.z+rand.nextInt(9)-4);
		}
		
		return false;
	}
}

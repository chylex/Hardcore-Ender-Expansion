package chylex.hee.world.feature.old;
/* TODO
public class WorldGenEndiumOre extends WorldGenerator{
	@Override
	public boolean generate(World world, Random rand, int x, int y, int z){		
		if (BlockPosM.tmp(x,y,z).getBlock(world) != Blocks.end_stone)return false;
		
		BlockPosM pos = new BlockPosM(x,y,z), tmpPos = BlockPosM.tmp();
		
		for(int check = 0; check < 25; check++){
			if (pos.getBlock(world) == Blocks.end_stone && (tmpPos.set(x-1,y,z).isAir(world) || tmpPos.set(x+1,y,z).isAir(world) || tmpPos.set(x,y,z-1).isAir(world) || tmpPos.set(x,y,z+1).isAir(world) || (check > 15 && rand.nextInt(3) == 0))){
				pos.setBlock(world,BlockList.endium_ore);
				return true;
			}
			
			pos.set(x+rand.nextInt(9)-4,y+rand.nextInt(9)-4,z+rand.nextInt(9)-4);
		}
		
		return false;
	}
}*/


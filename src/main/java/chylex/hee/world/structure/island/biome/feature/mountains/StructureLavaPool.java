package chylex.hee.world.structure.island.biome.feature.mountains;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import net.minecraft.init.Blocks;
import chylex.hee.system.util.Direction;
import net.minecraft.util.MathHelper;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;
import chylex.hee.world.util.BlockLocation;

public class StructureLavaPool extends AbstractIslandStructure{
	@Override
	protected boolean generate(Random rand){
		int x = getRandomXZ(rand,12), z = getRandomXZ(rand,12), y = 25+rand.nextInt(50);
		
		while(world.isAir(x,--y,z) && y > 10);
		if (world.getBlock(x,y,z) != surface())return false;
		
		Set<BlockLocation> lavaBlocks = new HashSet<BlockLocation>();
		
		for(int disc = 0, discAmount = 5+rand.nextInt(8); disc < discAmount; disc++){
			double len = (0.5D+rand.nextDouble()*0.5D)*disc*0.75D, rad = 1.8D+rand.nextDouble()*3D, radSq = MathUtil.square(rad+0.5D), px, pz;
			float ang = rand.nextFloat()*(float)Math.PI*2F;
			
			px = x+MathHelper.cos(ang)*len;
			pz = z+MathHelper.sin(ang)*len;
			
			for(int ix = (int)(px-rad)-1; ix <= (int)(px+rad)+1; ix++){
				for(int iz = (int)(pz-rad)-1; iz <= (int)(pz+rad)+1; iz++){
					if (MathUtil.square(ix-px)+MathUtil.square(iz-pz) <= radSq){
						lavaBlocks.add(new BlockLocation(ix,y,iz));
					}
				}
			}
		}
		
		for(BlockLocation lava:lavaBlocks){
			if (world.getBlock(lava.x,lava.y,lava.z) != surface() || world.isAir(lava.x,lava.y-1,lava.z) ||
				world.isAir(lava.x-1,lava.y,lava.z) || world.isAir(lava.x+1,lava.y,lava.z) ||
				world.isAir(lava.x,lava.y,lava.z-1) || world.isAir(lava.x,lava.y,lava.z+1))return false;
		}
		
		for(BlockLocation lava:lavaBlocks)world.setBlock(lava.x,lava.y,lava.z,Blocks.lava);
		
		for(int yOff = 1; yOff < 3+rand.nextInt(2); yOff++){
			for(int pass = 0; pass < 3; pass++){
				for(Iterator<BlockLocation> iter = lavaBlocks.iterator(); iter.hasNext();){
					BlockLocation lava = iter.next();
					
					if (world.isAir(lava.x,lava.y-yOff-1,lava.z))iter.remove();
					else if (rand.nextBoolean() || rand.nextBoolean()){
						int surrounding = 0;
						
						for(int dir = 0; dir < 4; dir++){
							surrounding += world.getBlock(lava.x+Direction.offsetX[dir],lava.y,lava.z+Direction.offsetZ[dir]) == Blocks.lava ? 1 : 0;
						}
						
						if (surrounding <= 3)iter.remove();
					}
				}
				
				for(BlockLocation lava:lavaBlocks)world.setBlock(lava.x,lava.y-yOff,lava.z,Blocks.lava);
			}
		}
		
		return true;
	}
}

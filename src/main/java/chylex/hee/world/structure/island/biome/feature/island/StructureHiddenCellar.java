package chylex.hee.world.structure.island.biome.feature.island;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import chylex.hee.block.BlockList;
import chylex.hee.world.structure.island.biome.feature.AbstractIslandStructure;

public class StructureHiddenCellar extends AbstractIslandStructure{
	private static final byte[] horCheckX = new byte[]{ -1, 0, 1, -1, 1, -1, 0, 1 },
								horCheckZ = new byte[]{ -1, -1, -1, 0, 0, 1, 1, 1 };
	
	@Override
	protected boolean generate(Random rand){
		for(int attempt = 0; attempt < 50; attempt++){
			
		}
		
		return false;
	}
	
	/**
	 * Walls count to the width of the room. Height has to be divisible by 2.
	 */
	private boolean genRoom(int x, int z, int halfWidth, int bottomY, int height){
		int halfHeight = height>>1;
		
		if (halfHeight*2 != height)throw new IllegalArgumentException("Hidden Cellar height has to be divisible by 2!");
		
		if (world.getBlock(x,bottomY+halfHeight,z) != Blocks.end_stone)return false;
		
		for(int horCheck = 0; horCheck < 8; horCheck++){
			for(int yCheck = -1; yCheck <= 1; yCheck++){
				Block block = world.getBlock(x+horCheckX[horCheck]*halfWidth,bottomY+halfHeight+yCheck*halfHeight,z+horCheckZ[horCheck]*halfWidth);
				if (block != Blocks.end_stone && block != Blocks.bedrock)return false;
			}
		}
		
		int x1 = x-halfWidth, z1 = z-halfWidth, x2 = x+halfWidth, z2 = z+halfWidth;
		
		for(int xx = x1; xx <= x2; xx++){
			for(int zz = z1; zz <= z2; zz++){
				for(int yy = bottomY; yy <= bottomY+height; yy++){
					boolean edge = xx == x1 || xx == x2 || zz == z1 || zz == z2 || yy == bottomY || yy == bottomY+height;
					if (world.getBlock(xx,yy,zz) != Blocks.bedrock)world.setBlock(xx,yy,zz,edge ? BlockList.purplething : Blocks.air);
				}
			}
		}
		
		// TODO room content
		
		return true;
	}
}

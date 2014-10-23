package chylex.hee.world.feature.util;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.Random;
import chylex.hee.system.logging.Log;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public final class DecoratorFeatureGenerator{
	private final TIntObjectHashMap<GeneratedBlock> blocks = new TIntObjectHashMap<>();
	private int minX, minZ, maxX, maxZ;
	
	public void setBlock(int x, int y, int z, Block block){
		setBlock(x,y,z,block,0);
	}
	
	public void setBlock(int x, int y, int z, Block block, int metadata){
		if (x < -16 || x > 16 || z < -16 || z > 16 || y < 0 || y > 255){
			Log.debug("Placing block at invalid coordinates: $0,$1,$2",x,y,z);
			return;
		}
		
		blocks.put(256*y+32*x+z,new GeneratedBlock(block,metadata,x,y,z));
		
		if (x < minX)minX = x;
		else if (x > maxX)maxX = x;
		
		if (z < minZ)minZ = z;
		else if (z > maxZ)maxZ = z;
	}
	
	public Block getBlock(int x, int y, int z){
		GeneratedBlock block = blocks.get(256*y+32*(x+16)+z+16);
		return block == null ? Blocks.air : block.block;
	}
	
	public int getMetadata(int x, int y, int z){
		GeneratedBlock block = blocks.get(256*y+32*(x+16)+z+16);
		return block == null ? 0 : block.metadata;
	}
	
	/**
	 * Generate in the center of 4 chunk group, in decorator it is chunkX+16, chunkZ+16
	 */
	public void generate(World world, Random rand, int centerX, int centerZ){
		if (blocks.isEmpty())return;
		
		int sizeX = maxX-minX+1, sizeZ = maxZ-minZ+1;
		int randX = Math.min(8,40-sizeX*2), randZ = Math.min(8,40-sizeZ*2);
		
		if (randX > 0)centerX += rand.nextInt(randX*2)-randX;
		if (randZ > 0)centerZ += rand.nextInt(randZ*2)-randZ;
		
		for(GeneratedBlock block:blocks.valueCollection())world.setBlock(centerX+block.x,block.y+128,centerZ+block.z,block.block,block.metadata,3);
	}
	
	private static final class GeneratedBlock{
		final Block block;
		final byte metadata;
		final byte x, y, z;
		
		GeneratedBlock(Block block, int metadata, int x, int y, int z){
			this.block = block;
			this.metadata = (byte)metadata;
			this.x = (byte)x;
			this.y = (byte)(y-128);
			this.z = (byte)z;
		}
	}
}

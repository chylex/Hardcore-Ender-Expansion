package chylex.hee.world.feature.util;
import gnu.trove.map.hash.TIntObjectHashMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import chylex.hee.world.structure.util.pregen.ITileEntityGenerator;
import chylex.hee.world.util.BlockLocation;

public final class DecoratorFeatureGenerator{
	private final TIntObjectHashMap<GeneratedBlock> blocks = new TIntObjectHashMap<>();
	private Map<BlockLocation,ITileEntityGenerator> tileEntities = new HashMap<>();
	private int minX, maxX, minZ, maxZ, bottomY, topY;
	
	public boolean setBlock(int x, int y, int z, Block block){
		return setBlock(x,y,z,block,0);
	}
	
	public boolean setBlock(int x, int y, int z, Block block, int metadata){
		if (x < -16 || x > 16 || z < -16 || z > 16 || y < -128 || y > 127)return false;
		
		if (x < minX)minX = x;
		else if (x > maxX)maxX = x;
		
		if (z < minZ)minZ = z;
		else if (z > maxZ)maxZ = z;
		
		if (y > topY)topY = y;
		else if (y < bottomY)bottomY = y;
		
		blocks.put(1024*(y+128)+32*(x+16)+z+16,new GeneratedBlock(block,metadata,x,y,z));
		return true;
	}
	
	public boolean setTileEntity(int x, int y, int z, ITileEntityGenerator tileGen){
		if (x < -16 || x > 16 || z < -16 || z > 16 || y < -128 || y > 127)return false;
		
		tileEntities.put(new BlockLocation(x,y,z),tileGen);
		return true;
	}
	
	public Block getBlock(int x, int y, int z){
		GeneratedBlock block = blocks.get(1024*(y+128)+32*(x+16)+z+16);
		return block == null ? Blocks.air : block.block;
	}
	
	public int getMetadata(int x, int y, int z){
		GeneratedBlock block = blocks.get(1024*(y+128)+32*(x+16)+z+16);
		return block == null ? 0 : block.metadata;
	}
	
	public int getTopBlockY(int x, int z){
		int y = topY;
		
		while(y >= bottomY){
			if (getBlock(x,y--,z) != Blocks.air)return y;
		}
		
		return -1;
	}
	
	public List<BlockLocation> getUsedLocations(){
		List<BlockLocation> locs = new ArrayList<>();
		for(GeneratedBlock block:blocks.valueCollection())locs.add(new BlockLocation(block.x,block.y,block.z));
		return locs;
	}
	
	/**
	 * Generate in the center of 4 chunk group, in decorator it is chunkX+16, chunkZ+16
	 */
	public void generate(World world, Random rand, int centerX, int centerY, int centerZ){
		if (blocks.isEmpty())return;
		
		int sizeX = maxX-minX+1, sizeZ = maxZ-minZ+1;
		int randX = Math.min(8,40-sizeX*2), randZ = Math.min(8,40-sizeZ*2);
		
		if (randX > 0)centerX += rand.nextInt(randX*2)-randX;
		if (randZ > 0)centerZ += rand.nextInt(randZ*2)-randZ;
		
		for(GeneratedBlock block:blocks.valueCollection())world.setBlock(centerX+block.x,centerY+block.y,centerZ+block.z,block.block,block.metadata,3);
		
		for(Entry<BlockLocation,ITileEntityGenerator> entry:tileEntities.entrySet()){
			BlockLocation loc = entry.getKey();
			entry.getValue().onTileEntityRequested("",world.getTileEntity(loc.x,loc.y,loc.z),rand);
		}
	}
	
	private static final class GeneratedBlock{
		final Block block;
		final byte metadata;
		final byte x, y, z;
		
		GeneratedBlock(Block block, int metadata, int x, int y, int z){
			this.block = block;
			this.metadata = (byte)metadata;
			this.x = (byte)x;
			this.y = (byte)y;
			this.z = (byte)z;
		}
	}
}

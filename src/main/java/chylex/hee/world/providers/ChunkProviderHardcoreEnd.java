package chylex.hee.world.providers;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderEnd;
import chylex.hee.world.biome.BiomeGenHardcoreEnd;

public class ChunkProviderHardcoreEnd extends ChunkProviderEnd{
	private final World world;
	
	public ChunkProviderHardcoreEnd(World world, long seed){
		super(world,seed);
		this.world = world;
	}

	@Override
	public void replaceBiomeBlocks(int x, int z, Block[] blocks, BiomeGenBase[] biomes, byte[] metadata){}
	
	@Override
	public Chunk provideChunk(int x, int z){
		endRNG.setSeed(x*341873128712L+z*132897987541L);
		
		Chunk chunk = new Chunk(world,new Block[32768],new byte[32768],x,z);
		for(int a = 0; a < chunk.getBiomeArray().length; a++)chunk.getBiomeArray()[a] = (byte)BiomeGenBase.sky.biomeID;
		
		chunk.generateSkylightMap();
		return chunk;
	}
	
	@Override
	public void populate(IChunkProvider chunkProvider, int x, int z){
		((BiomeGenHardcoreEnd)BiomeGenBase.sky).decorate(world,endRNG,x*16,z*16);
	}
	
	@Override
	public List getPossibleCreatures(EnumCreatureType type, int x, int y, int z){
		return ((BiomeGenHardcoreEnd)BiomeGenBase.sky).getSpawnableList(type);
	}
	
	@Override
	public ChunkPosition func_147416_a(World world, String identifier, int x, int y, int z){
		return null;
	}
}
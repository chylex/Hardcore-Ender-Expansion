package chylex.hee.world.providers;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderEnd;
import chylex.hee.world.biome.BiomeGenHardcoreEnd;

public class ChunkProviderHardcoreEnd extends ChunkProviderEnd{
	private final World world;
	private final Random randCopy;
	
	public ChunkProviderHardcoreEnd(World world, long seed){
		super(world,seed);
		this.world = world;
		this.randCopy = endRNG;
	}

	@Override
	public void replaceBiomeBlocks(int x, int z, Block[] blocks, BiomeGenBase[] biomes, byte[] metadata){
		for(int index = 0; index < 32768; index++){
			if (blocks[index] == Blocks.stone)blocks[index] = Blocks.end_stone;
		}
	}
	
	@Override
	public void populate(IChunkProvider chunkProvider, int x, int z){
		((BiomeGenHardcoreEnd)BiomeGenBase.sky).decorate(world,randCopy,x*16,z*16);
	}
	
	@Override
	public void recreateStructures(int x, int z){}
	
	@Override
	public ChunkPosition func_147416_a(World world, String identifier, int x, int y, int z){
		return null;
	}
	
	// TODO override noise gen
}
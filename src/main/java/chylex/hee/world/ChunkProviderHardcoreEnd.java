package chylex.hee.world;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.init.Blocks;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderEnd;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.ChunkProviderEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import chylex.hee.world.biome.BiomeGenHardcoreEnd;
import chylex.hee.world.structure.island.MapGenIsland;
import chylex.hee.world.structure.tower.MapGenTower;
import cpw.mods.fml.common.eventhandler.Event.Result;

public class ChunkProviderHardcoreEnd extends ChunkProviderEnd{
	private final World world;
	private final Random randCopy;
	private final MapGenScatteredFeature islandGen, towerGen;
	
	public ChunkProviderHardcoreEnd(World world, long seed){
		super(world,seed);
		this.world = world;
		this.randCopy = endRNG;
		
		islandGen = new MapGenIsland();
		towerGen = new MapGenTower();
	}

	@Override
	public void replaceBiomeBlocks(int x, int z, Block[] blocks, BiomeGenBase[] biomes, byte[] metadata){
		if (!BiomeGenHardcoreEnd.overrideWorldGen){
			ChunkProviderEvent.ReplaceBiomeBlocks event = new ChunkProviderEvent.ReplaceBiomeBlocks(this,x,z,blocks,metadata,biomes,world);
			MinecraftForge.EVENT_BUS.post(event);
			if (event.getResult() == Result.DENY)return;
		}
		
		for(int index = 0; index < 32768; index++){
			if (blocks[index] == Blocks.stone)blocks[index] = Blocks.end_stone;
		}

		if (world.provider.dimensionId == 1){
			islandGen.func_151539_a(this,world,x,z,blocks);
			towerGen.func_151539_a(this,world,x,z,blocks);
		}
	}
	
	@Override
	public void populate(IChunkProvider chunkProvider, int x, int z){
		BlockFalling.fallInstantly = true;
		if (!BiomeGenHardcoreEnd.overrideWorldGen)MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(chunkProvider,world,randCopy,x,z,false));
		
		if (world.provider.dimensionId == 1){
			islandGen.generateStructuresInChunk(world,randCopy,x,z);
			towerGen.generateStructuresInChunk(world,randCopy,x,z);
		}

		((BiomeGenHardcoreEnd)BiomeGenBase.sky).decorate(world,randCopy,x*16,z*16);

		if (!BiomeGenHardcoreEnd.overrideWorldGen)MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(chunkProvider,world,randCopy,x,z,false));
		BlockFalling.fallInstantly = false;
	}
	
	@Override
	public void recreateStructures(int x, int z){
		if (world.provider.dimensionId == 1){
			islandGen.func_151539_a(this,world,x,z,(Block[])null);
			towerGen.func_151539_a(this,world,x,z,(Block[])null);
		}
	}
	
	@Override
	public ChunkPosition func_147416_a(World world, String identifier, int x, int y, int z){
		return null;
	}
}

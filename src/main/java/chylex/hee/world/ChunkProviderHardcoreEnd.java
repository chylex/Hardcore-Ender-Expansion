package chylex.hee.world;
import java.util.Random;
import net.minecraft.block.BlockFalling;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderEnd;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.ChunkProviderEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import chylex.hee.world.biome.BiomeGenHardcoreEnd;
import chylex.hee.world.structure.island.MapGenIsland;
import chylex.hee.world.structure.tower.MapGenTower;

public class ChunkProviderHardcoreEnd extends ChunkProviderEnd{
	private final World world;
	private final Random randCopy;
	private final MapGenScatteredFeature islandGen, towerGen;
	private int chunkX, chunkZ;
	
	public ChunkProviderHardcoreEnd(World world, long seed){
		super(world,seed);
		this.world = world;
		this.randCopy = endRNG;
		
		islandGen = new MapGenIsland();
		towerGen = new MapGenTower();
	}

	@Override
	public void func_180519_a(ChunkPrimer chunk){
		if (!BiomeGenHardcoreEnd.overrideWorldGen){
			ChunkProviderEvent.ReplaceBiomeBlocks event = new ChunkProviderEvent.ReplaceBiomeBlocks(this,chunkX,chunkZ,chunk,world);
			MinecraftForge.EVENT_BUS.post(event);
			if (event.getResult() == Result.DENY)return;
		}
		
		for(int index = 0; index < 32768; index++){
			if (chunk.getBlockState(index).getBlock() == Blocks.stone)chunk.setBlockState(index,Blocks.end_stone.getDefaultState());
		}

		if (world.provider.getDimensionId() == 1){
			islandGen.func_175792_a(this,world,chunkX,chunkZ,chunk);
			towerGen.func_175792_a(this,world,chunkX,chunkZ,chunk);
		}
	}
	
	@Override
	public void populate(IChunkProvider chunkProvider, int x, int z){
		chunkX = x;
		chunkZ = z;
		ChunkCoordIntPair coords = new ChunkCoordIntPair(x,z);
		
		BlockFalling.fallInstantly = true;
		if (!BiomeGenHardcoreEnd.overrideWorldGen)MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(chunkProvider,world,randCopy,x,z,false));
		
		if (world.provider.getDimensionId() == 1){
			islandGen.func_175794_a(world,randCopy,coords);
			towerGen.func_175794_a(world,randCopy,coords);
		}

		((BiomeGenHardcoreEnd)BiomeGenBase.sky).decorate(world,randCopy,new BlockPos(x*16,0,z*16));

		if (!BiomeGenHardcoreEnd.overrideWorldGen)MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(chunkProvider,world,randCopy,x,z,false));
		BlockFalling.fallInstantly = false;
	}
	
	@Override
	public void recreateStructures(Chunk chunk, int x, int z){
		if (world.provider.getDimensionId() == 1){
			islandGen.func_175792_a(this,world,x,z,null);
			towerGen.func_175792_a(this,world,x,z,null);
		}
	}
}

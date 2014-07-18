package chylex.hee.world;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderEnd;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import chylex.hee.world.structure.island.MapGenIsland;
import chylex.hee.world.structure.tower.MapGenTower;

public class ChunkProviderHardcoreEnd extends ChunkProviderEnd{
	private World world;
	private MapGenScatteredFeature islandGen,towerGen;
	
	public ChunkProviderHardcoreEnd(World world, long seed){
		super(world,seed);
		this.world = world;
		
		islandGen = new MapGenIsland();
		towerGen = new MapGenTower();
	}

	@Override
	public void func_147421_b(int x, int z, Block[] blocks, BiomeGenBase[] biomes){
		super.func_147421_b(x,z,blocks,biomes);

		if (world.provider.dimensionId == 1){
			islandGen.func_151539_a(this,world,x,z,blocks);
			towerGen.func_151539_a(this,world,x,z,blocks);
		}
	}
	
	@Override
	public void populate(IChunkProvider chunkProvider, int x, int z){
		BlockFalling.fallInstantly = true;
		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Pre(chunkProvider,world,world.rand,x,z,false));
		
		if (world.provider.dimensionId == 1){
			islandGen.generateStructuresInChunk(world,world.rand,x,z);
			towerGen.generateStructuresInChunk(world,world.rand,x,z);
		}

		int realX = x*16, realZ = z*16;
		world.getBiomeGenForCoords(realX+16,realZ+16).decorate(world,world.rand,realX,realZ);

		MinecraftForge.EVENT_BUS.post(new PopulateChunkEvent.Post(chunkProvider,world,world.rand,x,z,false));
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
		if (identifier.equals("hardcoreenderdragon_Island"))return islandGen.func_151545_a(world,x,y,z);
		else if (identifier.equals("hardcoreenderdragon_Tower"))return towerGen.func_151545_a(world,x,y,z);
		return null;
	}
}

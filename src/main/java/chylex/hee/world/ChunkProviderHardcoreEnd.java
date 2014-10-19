package chylex.hee.world;
import java.util.Random;
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
import chylex.hee.system.ReflectionPublicizer;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.DragonSavefile;
import chylex.hee.world.structure.island.MapGenIsland;
import chylex.hee.world.structure.tower.MapGenTower;

public class ChunkProviderHardcoreEnd extends ChunkProviderEnd{
	private final World world;
	private final Random randCopy;
	private final MapGenScatteredFeature islandGen, towerGen;
	
	public ChunkProviderHardcoreEnd(World world, long seed){
		super(world,seed);
		this.world = world;
		this.randCopy = (Random)ReflectionPublicizer.get(ReflectionPublicizer.chunkProviderEndRandom,this);
		
		islandGen = new MapGenIsland();
		towerGen = new MapGenTower();
	}
	
	@Override
	public void func_147420_a(int x, int z, Block[] blocks, BiomeGenBase[] biomes){
		randCopy.nextInt(1+WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).getDragonDeathAmount());
		super.func_147420_a(x,z,blocks,biomes);
	}

	@Override
	public void replaceBiomeBlocks(int x, int z, Block[] blocks, BiomeGenBase[] biomes, byte[] metadata){
		super.replaceBiomeBlocks(x,z,blocks,biomes,metadata);

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

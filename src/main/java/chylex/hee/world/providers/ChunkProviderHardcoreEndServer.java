package chylex.hee.world.providers;
import java.util.Random;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import chylex.hee.system.collections.EmptyEnumSet;
import chylex.hee.world.end.EndTerritory;

public class ChunkProviderHardcoreEndServer extends ChunkProviderServer{
	public ChunkProviderHardcoreEndServer(WorldServer world){
		super(world,world.theChunkProviderServer.currentChunkLoader,world.theChunkProviderServer.currentChunkProvider);
	}

	@Override
	public void populate(IChunkProvider provider, int x, int z){
		Chunk chunk = provideChunk(x,z);
		
		if (!chunk.isTerrainPopulated){
			chunk.func_150809_p();
			
			if (currentChunkProvider != null){
				currentChunkProvider.populate(provider,x,z);
				chunk.setChunkModified();
			}
		}
	}
	
	public void prepareSpawn(){
		if (!provideChunk(0,0).isTerrainPopulated){
			EndTerritory.THE_HUB.generateTerritory(0,worldObj,new Random(worldObj.getSeed()),EmptyEnumSet.get());
		}
	}
}
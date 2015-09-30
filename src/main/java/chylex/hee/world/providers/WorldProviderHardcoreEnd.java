package chylex.hee.world.providers;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.chunk.IChunkProvider;
import chylex.hee.world.TeleportHandler;

public class WorldProviderHardcoreEnd extends WorldProviderEnd{
	@Override
	public IChunkProvider createChunkGenerator(){
		return new ChunkProviderHardcoreEnd(worldObj,worldObj.getSeed());
	}
	
	@Override
	public boolean canRespawnHere(){
		return true;
	}
	
	@Override
	public ChunkCoordinates getEntrancePortalLocation(){
		return TeleportHandler.endSpawn;
	}
	
	@Override
	public ChunkCoordinates getSpawnPoint(){
		return TeleportHandler.endSpawn;
	}
	
	@Override
	public ChunkCoordinates getRandomizedSpawnPoint(){
		return TeleportHandler.endSpawn;
	}
}

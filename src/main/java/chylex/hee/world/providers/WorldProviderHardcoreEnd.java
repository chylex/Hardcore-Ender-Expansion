package chylex.hee.world.providers;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.chunk.IChunkProvider;

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
		return new ChunkCoordinates(0,70,0); // TODO
	}
}

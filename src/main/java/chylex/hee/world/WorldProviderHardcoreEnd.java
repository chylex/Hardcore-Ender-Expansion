package chylex.hee.world;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.chunk.IChunkProvider;

public class WorldProviderHardcoreEnd extends WorldProviderEnd{
	@Override
	public IChunkProvider createChunkGenerator(){
		return new ChunkProviderHardcoreEnd(worldObj,worldObj.getSeed());
	}
}

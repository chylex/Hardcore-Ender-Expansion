package chylex.hee.game.save.types;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import chylex.hee.game.save.SaveFile;

public class DragonFile extends SaveFile{
	private ChunkCoordIntPair lastDragonChunk = new ChunkCoordIntPair(0,0);
	
	public DragonFile(){
		super("server.nbt");
	}
	
	public void setLastDragonChunk(int x, int z){
		lastDragonChunk = new ChunkCoordIntPair(x,z);
		setModified();
	}
	
	public ChunkCoordIntPair getLastDragonChunk(){
		return lastDragonChunk;
	}

	@Override
	protected void onSave(NBTTagCompound nbt){
		nbt.setIntArray("lastChunk",new int[]{ lastDragonChunk.chunkXPos, lastDragonChunk.chunkZPos });
	}

	@Override
	protected void onLoad(NBTTagCompound nbt){
		int[] lastChunk = nbt.getIntArray("lastChunk");
		if (lastChunk.length == 2)lastDragonChunk = new ChunkCoordIntPair(lastChunk[0],lastChunk[1]);
	}
}

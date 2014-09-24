package chylex.hee.world.structure.util.pregen;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.world.structure.ComponentScatteredFeatureCustom;

public final class LargeStructureWorld{
	private static final LargeStructureChunk emptyFallbackChunk = new LargeStructureChunk.Empty(0,0,128);
	
	private final LargeStructureChunk[][] chunks;
	private LargeStructureChunk lastActiveChunk;
	
	public LargeStructureWorld(ComponentScatteredFeatureCustom structure){
		chunks = new LargeStructureChunk[structure.getSizeX()>>4][structure.getSizeZ()>>4];
		
		int chunkXSize = chunks.length, chunkZSize = chunks[0].length;
		
		for(int z = 0; z < chunkZSize; z++){
			for(int x = 0; x < chunkXSize; x++){
				chunks[x][z] = new LargeStructureChunk(x,z,structure.getSizeY());
			}
		}
	}
	
	public int getChunkAmountX(){
		return chunks.length;
	}
	
	public int getChunkAmountZ(){
		return chunks[0].length;
	}
	
	public LargeStructureChunk getChunkFromChunkCoords(int chunkX, int chunkZ){
		return chunkX < 0 || chunkZ < 0 || chunkX >= chunks.length || chunkZ >= chunks[0].length ? emptyFallbackChunk : chunks[chunkX][chunkZ];
	}
	
	private LargeStructureChunk getChunk(int blockX, int blockZ){
		if (lastActiveChunk != null && (blockX>>4) == lastActiveChunk.x && (blockZ>>4) == lastActiveChunk.z)return lastActiveChunk;
		if (blockX < 0 || blockZ < 0 || blockX >= chunks.length*16 || blockZ >= chunks[0].length*16)return emptyFallbackChunk;
		return lastActiveChunk = chunks[blockX>>4][blockZ>>4];
	}
	
	private int xInChunk(int blockX){
		return lastActiveChunk != null ? blockX-lastActiveChunk.x*16 : 0;
	}

	private int zInChunk(int blockZ){
		return lastActiveChunk != null ? blockZ-lastActiveChunk.z*16 : 0;
	}
	
	public void setBlock(int blockX, int blockY, int blockZ, Block block){
		getChunk(blockX,blockZ).setBlock(xInChunk(blockX),blockY,zInChunk(blockZ),block,0,false);
	}
	
	public void setBlock(int blockX, int blockY, int blockZ, Block block, int metadata){
		getChunk(blockX,blockZ).setBlock(xInChunk(blockX),blockY,zInChunk(blockZ),block,metadata,false);
	}
	
	public void setBlock(int blockX, int blockY, int blockZ, Block block, int metadata, boolean scheduleUpdate){
		getChunk(blockX,blockZ).setBlock(xInChunk(blockX),blockY,zInChunk(blockZ),block,metadata,scheduleUpdate);
	}
	
	public Block getBlock(int blockX, int blockY, int blockZ){
		return getChunk(blockX,blockZ).getBlock(xInChunk(blockX),blockY,zInChunk(blockZ));
	}
	
	public int getMetadata(int blockX, int blockY, int blockZ){
		return getChunk(blockX,blockZ).getMetadata(xInChunk(blockX),blockY,zInChunk(blockZ));
	}
	
	public int getHighestY(int blockX, int blockZ){
		LargeStructureChunk chunk = getChunk(blockX,blockZ);
		
		for(int yy = chunk.getHighestBlockY(); yy > 0; yy--){
			if (!isAir(blockX,yy,blockZ))return yy;
		}
		
		return 0;
	}
	
	public boolean isAir(int blockX, int blockY, int blockZ){
		Block block = getBlock(blockX,blockY,blockZ);
		return block == Blocks.air || block.getMaterial() == Material.air;
	}
	
	public void setTileEntityGenerator(int blockX, int blockY, int blockZ, String key, ITileEntityGenerator tileGen){
		getChunk(blockX,blockZ).addTileEntityGenerator(xInChunk(blockX),blockY,zInChunk(blockZ),key,tileGen);
	}
	
	public NBTTagCompound saveToNBT(){
		NBTTagCompound nbt = new NBTTagCompound();
		
		for(int x = 0; x < chunks.length; x++){
			for(int z = 0; z < chunks[x].length; z++){
				nbt.setTag(x+"-"+z,chunks[x][z].saveToNBT());
			}
		}
		
		return nbt;
	}
	
	public void loadFromNBT(NBTTagCompound nbt){
		for(int x = 0; x < chunks.length; x++){
			for(int z = 0; z < chunks[x].length; z++){
				chunks[x][z].loadFromNBT(nbt.getCompoundTag(x+"-"+z));
			}
		}
	}
}

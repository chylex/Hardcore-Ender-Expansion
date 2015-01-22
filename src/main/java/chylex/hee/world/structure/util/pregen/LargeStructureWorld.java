package chylex.hee.world.structure.util.pregen;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import chylex.hee.world.structure.ComponentScatteredFeatureCustom;

public final class LargeStructureWorld{
	private static final LargeStructureChunk emptyFallbackChunk = new LargeStructureChunk.Empty(0,0,128);
	
	private final LargeStructureChunk[][] chunks;
	private LargeStructureChunk lastActiveChunk;
	
	public LargeStructureWorld(ComponentScatteredFeatureCustom structure){
		if (structure == null){
			chunks = new LargeStructureChunk[0][0];
			return;
		}
		
		chunks = new LargeStructureChunk[structure.getSizeX()>>4][structure.getSizeZ()>>4];
		
		int chunkXSize = structure.getSizeX()>>4, chunkZSize = structure.getSizeZ()>>4;
		
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
		getChunk(blockX,blockZ).setBlock(xInChunk(blockX),blockY,zInChunk(blockZ),block.getDefaultState(),false);
	}
	
	public void setBlock(int blockX, int blockY, int blockZ, Block block, boolean scheduleUpdate){
		getChunk(blockX,blockZ).setBlock(xInChunk(blockX),blockY,zInChunk(blockZ),block.getDefaultState(),scheduleUpdate);
	}
	
	public void setBlock(int blockX, int blockY, int blockZ, IBlockState state){
		getChunk(blockX,blockZ).setBlock(xInChunk(blockX),blockY,zInChunk(blockZ),state,false);
	}
	
	public void setBlock(int blockX, int blockY, int blockZ, IBlockState state, boolean scheduleUpdate){
		getChunk(blockX,blockZ).setBlock(xInChunk(blockX),blockY,zInChunk(blockZ),state,scheduleUpdate);
	}
	
	public Block getBlock(int blockX, int blockY, int blockZ){
		return getChunk(blockX,blockZ).getBlock(xInChunk(blockX),blockY,zInChunk(blockZ)).getBlock();
	}
	
	public IBlockState getBlockState(int blockX, int blockY, int blockZ){
		return getChunk(blockX,blockZ).getBlock(xInChunk(blockX),blockY,zInChunk(blockZ));
	}
	
	public int getHighestY(int blockX, int blockZ){
		LargeStructureChunk chunk = getChunk(blockX,blockZ);
		
		for(int yy = chunk.getHighestBlockY(); yy > 0; yy--){
			if (!isAir(blockX,yy,blockZ))return yy;
		}
		
		return 0;
	}
	
	public boolean isAir(int blockX, int blockY, int blockZ){
		return getBlock(blockX,blockY,blockZ) == Blocks.air;
		/*Block block = getBlock(blockX,blockY,blockZ);
		return block == Blocks.air || block.getMaterial() == Material.air;*/
	}
	
	public void setTileEntityGenerator(int blockX, int blockY, int blockZ, String key, ITileEntityGenerator tileGen){
		getChunk(blockX,blockZ).addTileEntityGenerator(xInChunk(blockX),blockY,zInChunk(blockZ),key,tileGen);
	}
	
	public void addEntity(Entity entity){
		int x = MathHelper.floor_double(entity.posX), z = MathHelper.floor_double(entity.posZ);
		getChunk(x,z).addEntity(entity,xInChunk(x),zInChunk(z));
	}
	
	public <T extends Entity> List<T> getAllEntities(Class<T> exactClassToMatch){
		List<T> list = new ArrayList<>();
		
		for(int x = 0; x < chunks.length; x++){
			for(int z = 0; z < chunks[x].length; z++){
				for(Entity e:chunks[x][z].getAllEntities()){
					if (e.getClass() == exactClassToMatch)list.add((T)e);
				}
			}
		}
		
		return list;
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

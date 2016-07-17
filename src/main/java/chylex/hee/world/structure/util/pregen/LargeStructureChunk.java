package chylex.hee.world.structure.util.pregen;
import gnu.trove.set.hash.TIntHashSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import chylex.hee.system.logging.Log;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.structure.ComponentScatteredFeatureCustom;

public class LargeStructureChunk{
	public final int x, z;
	private final int ySize;
	private int minBlockY, maxBlockY;
	
	private final Block[] storedBlocks;
	private final byte[] storedMetadata;
	private final TIntHashSet scheduledForUpdate = new TIntHashSet();
	private final Map<Integer,String> storedTileEntityClues = new HashMap<>();
	private final Map<String,ITileEntityGenerator> storedTileEntities = new HashMap<>();
	private final Map<ChunkCoordIntPair,Entity> storedEntities = new HashMap<>();
	
	private TIntHashSet alreadyGeneratedXZ = new TIntHashSet(256);
	
	public LargeStructureChunk(int x, int z, int ySize){
		this.x = x;
		this.z = z;
		this.ySize = ySize;
		this.minBlockY = ySize;
		this.maxBlockY = 0;
		
		this.storedBlocks = new Block[256*ySize];
		this.storedMetadata = new byte[256*ySize];
	}
	
	public void setBlock(int xInChunk, int yInChunk, int zInChunk, Block block, int metadata, boolean scheduleUpdate){
		if (xInChunk < 0 || xInChunk >= 16 || yInChunk < 0 || yInChunk >= ySize || zInChunk < 0 || zInChunk >= 16){
			if (Log.isDebugEnabled())Thread.dumpStack();
			Log.debug("Placing block at invalid coordinates: $0,$1,$2",xInChunk,yInChunk,zInChunk);
			return;
		}
		
		if (yInChunk < minBlockY)minBlockY = yInChunk;
		if (yInChunk > maxBlockY)maxBlockY = yInChunk;
		
		int index = yInChunk*256+xInChunk*16+zInChunk;
		storedBlocks[index] = block;
		storedMetadata[index] = (byte)metadata;
		
		if (scheduleUpdate)scheduledForUpdate.add(index);
	}
	
	public Block getBlock(int xInChunk, int yInChunk, int zInChunk){
		if (xInChunk < 0 || xInChunk >= 16 || yInChunk < 0 || yInChunk >= ySize || zInChunk < 0 || zInChunk >= 16){
			if (Log.isDebugEnabled())Thread.dumpStack();
			Log.debug("Getting block at invalid coordinates: $0,$1,$2",xInChunk,yInChunk,zInChunk);
			return Blocks.air;
		}
		
		Block block = storedBlocks[yInChunk*256+xInChunk*16+zInChunk];
		return block == null ? Blocks.air : block;
	}
	
	public int getMetadata(int xInChunk, int yInChunk, int zInChunk){
		if (xInChunk < 0 || xInChunk >= 16 || yInChunk < 0 || yInChunk >= ySize || zInChunk < 0 || zInChunk >= 16){
			if (Log.isDebugEnabled())Thread.dumpStack();
			Log.debug("Getting block metadata at invalid coordinates: $0,$1,$2",xInChunk,yInChunk,zInChunk);
			return 0;
		}
		
		return storedMetadata[yInChunk*256+xInChunk*16+zInChunk];
	}
	
	public void addTileEntityGenerator(int xInChunk, int yInChunk, int zInChunk, String key, ITileEntityGenerator tileGen){
		storedTileEntityClues.put(yInChunk*256+xInChunk*16+zInChunk,key);
		storedTileEntities.put(key,tileGen);
	}
	
	public void addEntity(Entity entity, int xInChunk, int zInChunk){
		storedEntities.put(new ChunkCoordIntPair(xInChunk,zInChunk),entity);
	}
	
	public Collection<Entity> getAllEntities(){
		return storedEntities.values();
	}
	
	public boolean isBlockScheduledForUpdate(int xInChunk, int yInChunk, int zInChunk){
		return scheduledForUpdate.contains(yInChunk*256+xInChunk*16+zInChunk);
	}
	
	public int getHighestBlockY(){
		return maxBlockY;
	}
	
	public void generateInStructure(ComponentScatteredFeatureCustom structure, World world, StructureBoundingBox bb, int addX, int addY, int addZ){
		if (minBlockY == ySize && maxBlockY == 0)return;
		
		boolean hasBlocksToUpdate = !scheduledForUpdate.isEmpty(), hasTileEntities = !storedTileEntities.isEmpty(), continueY = true;
		Block block;
		
		for(int x = 0; x < 16; x++){
			for(int z = 0; z < 16; z++){
				if (!alreadyGeneratedXZ.contains(x*16+z)){
					for(int y = minBlockY; y <= maxBlockY && continueY; y++){
						if (continueY && y == maxBlockY){
							alreadyGeneratedXZ.add(x*16+z);
							
							for(Iterator<Entry<ChunkCoordIntPair,Entity>> iter = storedEntities.entrySet().iterator(); iter.hasNext();){
								Entry<ChunkCoordIntPair,Entity> entry = iter.next();
								
								if (entry.getKey().chunkXPos == x && entry.getKey().chunkZPos == z){
									Entity entity = entry.getValue();
									
									int ix = MathUtil.floor(entity.posX), iy = MathUtil.floor(entity.posY), iz = MathUtil.floor(entity.posZ);
									double fx = entity.posX-ix, fy = entity.posY-iy, fz = entity.posZ-iz;
									ix += addX; iy += addY; iz += addZ;
									
									if (entity.worldObj == null){
										entity.dimension = world.provider.dimensionId;
										entity.setWorld(world);
									}
									
									entity.setPosition(structure.getXWithOffset(ix,iz)+fx,structure.getYWithOffset(iy)+fy+0.01D,structure.getZWithOffset(ix,iz)+fz);
									
									world.spawnEntityInWorld(entity);
									iter.remove();
								}
							}
						}
						
						if ((block = getBlock(x,y,z)) == Blocks.air)continue;
						
						if (hasBlocksToUpdate && isBlockScheduledForUpdate(x,y,z))continueY = structure.placeBlockAndUpdateUnsafe(block,getMetadata(x,y,z),addX+this.x*16+x,addY+y,addZ+this.z*16+z,world,bb);
						else continueY = structure.placeBlockUnsafe(block,getMetadata(x,y,z),addX+this.x*16+x,addY+y,addZ+this.z*16+z,world,bb);
						
						if (continueY && hasTileEntities && storedTileEntityClues.containsKey(y*256+x*16+z)){
							String key = storedTileEntityClues.get(y*256+x*16+z);
							TileEntity tileEntity = structure.getBlockTileEntityUnsafe(addX+this.x*16+x,addY+y,addZ+this.z*16+z,world,bb);
							
							if (tileEntity == null)Log.debug("Tile entity with key $0 not found at $1,$2,$3!",key,x,y,z);
							else storedTileEntities.get(key).onTileEntityRequested(key,tileEntity,world.rand);
						}
					}
				}
				
				continueY = true;
			}
		}
	}
	
	public NBTTagCompound saveToNBT(){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setIntArray("genXZ",alreadyGeneratedXZ.toArray());
		return nbt;
	}
	
	public void loadFromNBT(NBTTagCompound nbt){
		alreadyGeneratedXZ.clear();
		alreadyGeneratedXZ.addAll(nbt.getIntArray("genXZ"));
	}
	
	public static class Empty extends LargeStructureChunk{
		public Empty(int x, int z, int ySize){
			super(x,z,ySize);
		}
		
		@Override
		public void setBlock(int xInChunk, int yInChunk, int zInChunk, Block block, int metadata, boolean scheduleUpdate){}
		
		@Override
		public void addTileEntityGenerator(int xInChunk, int yInChunk, int zInChunk, String key, ITileEntityGenerator tileGen){}
	}
}

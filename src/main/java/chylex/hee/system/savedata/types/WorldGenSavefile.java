package chylex.hee.system.savedata.types;
import gnu.trove.iterator.TByteObjectIterator;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.hash.TByteObjectHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import chylex.hee.system.savedata.WorldSavefile;

public class WorldGenSavefile extends WorldSavefile{
	public enum WorldGenElement{
		DUNGEON_TOWER(1), BIOME_ISLAND(6);
		
		private final byte rad;
		
		private WorldGenElement(int rad){
			this.rad = (byte)rad;
		}
	}
	
	private final TLongObjectHashMap<WorldGenElement> elements = new TLongObjectHashMap<>();
	
	public WorldGenSavefile(){
		super("worldgen.nbt");
	}
	
	public void addElementAt(int chunkX, int chunkZ, WorldGenElement element){
		elements.put(getFromChunkPos(chunkX,chunkZ),element);
		setModified();
	}
	
	public boolean isAreaBlocked(int chunkX, int chunkZ, WorldGenElement element){
		for(TLongObjectIterator<WorldGenElement> iter = elements.iterator(); iter.hasNext();){
			iter.advance();
			
			if (Math.abs(getX(iter.key())-chunkX) <= iter.value().rad+element.rad && Math.abs(getZ(iter.key())-chunkZ) <= iter.value().rad+element.rad){
				return true;
			}
		}
		
		return false;
	}
	
	private final long getFromChunkPos(int x, int z){
		if (x >= -1875000 && z >= -1875000 && x < 1875000 && z < 1875000)return ((x+1875000L)<<24L)|(z+1875000L);
		else return Long.MAX_VALUE;
	}
	
	private final int getX(long pos){
		return (int)((pos>>24L)-1875000L);
	}
	
	private final int getZ(long pos){
		return (int)((pos&16777215L)-1875000L);
	}

	@Override
	protected void onSave(NBTTagCompound nbt){
		TByteObjectHashMap<NBTTagList> saved = new TByteObjectHashMap<>();
		byte ord;
		
		for(TLongObjectIterator<WorldGenElement> iter = elements.iterator(); iter.hasNext();){
			iter.advance();
			ord = (byte)iter.value().ordinal();
			
			if (!saved.containsKey(ord))saved.put(ord,new NBTTagList());
			saved.get(ord).appendTag(new NBTTagDouble(Double.longBitsToDouble(iter.key())));
		}
		
		for(TByteObjectIterator<NBTTagList> iter = saved.iterator(); iter.hasNext();){
			iter.advance();
			nbt.setTag(String.valueOf(iter.key()),iter.value());
		}
	}

	@Override
	protected void onLoad(NBTTagCompound nbt){
		WorldGenElement[] vals = WorldGenElement.values();
		
		for(int a = 0; a < vals.length; a++){
			NBTTagList list = nbt.getTagList(String.valueOf(a),NBT.TAG_DOUBLE);
			
			for(int index = 0; index < list.tagCount(); index++){
				elements.put(Double.doubleToLongBits(list.getDouble(index)),vals[a]);
			}
		}
	}
}

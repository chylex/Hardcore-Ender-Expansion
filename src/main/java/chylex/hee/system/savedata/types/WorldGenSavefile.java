package chylex.hee.system.savedata.types;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.map.hash.TLongObjectHashMap;
import net.minecraft.nbt.NBTTagCompound;
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
	}
	
	public boolean isAreaBlocked(int chunkX, int chunkZ, WorldGenElement element){
		for(TLongIterator iter = elements.keySet().iterator(); iter.hasNext();){
			long pos = iter.next();
			WorldGenElement ele = elements.get(pos);
			
			if (Math.abs(getX(pos)-chunkX) <= ele.rad+element.rad && Math.abs(getZ(pos)-chunkZ) <= ele.rad+element.rad){
				return true;
			}
		}
		
		return false;
	}
	
	private final long getFromChunkPos(int x, int z){
		if (x >= -1875000 && z >= -1875000 && x < 1875000 && z < 1875000)return ((x+1875000)<<24)|(z+1875000);
		else return Long.MIN_VALUE;
	}
	
	private final int getX(long pos){
		return (int)((pos>>24)-1875000);
	}
	
	private final int getZ(long pos){
		return (int)((pos&16777215)-1875000);
	}

	@Override
	protected void onSave(NBTTagCompound nbt){
		
	}

	@Override
	protected void onLoad(NBTTagCompound nbt){
		
	}
}

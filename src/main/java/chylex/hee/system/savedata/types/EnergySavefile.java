package chylex.hee.system.savedata.types;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants.NBT;
import chylex.hee.mechanics.energy.EnergyChunkData;
import chylex.hee.system.savedata.WorldSavefile;

public class EnergySavefile extends WorldSavefile{
	public static byte sectionSize = 3;
	
	private Map<ChunkCoordIntPair,EnergyChunkData> chunkMap = new HashMap<>();
	
	public EnergySavefile(){
		super("energy.nbt");
	}
	
	public EnergyChunkData getFromChunkCoords(int chunkX, int chunkZ, boolean setModified){
		if (setModified)setModified();
		
		if (chunkX < 0)chunkX -= sectionSize-1;
		if (chunkZ < 0)chunkZ -= sectionSize-1;
		
		ChunkCoordIntPair coords = new ChunkCoordIntPair((chunkX+1)/sectionSize,(chunkZ+1)/sectionSize);
		
		EnergyChunkData data = chunkMap.get(coords);
		if (data != null)return data;

		chunkMap.put(coords,data = new EnergyChunkData(chunkX,chunkZ,DimensionManager.getWorld(1).rand));
		return data;
	}
	
	public EnergyChunkData getFromBlockCoords(int blockX, int blockZ, boolean setModified){
		return getFromChunkCoords(blockX>>4,blockZ>>4,setModified);
	}
	
	public void reset(){
		chunkMap.clear();
		setModified();
	}

	@Override
	protected void onSave(NBTTagCompound nbt){
		NBTTagList list = new NBTTagList();
		
		for(Entry<ChunkCoordIntPair,EnergyChunkData> entry:chunkMap.entrySet()){
			NBTTagCompound tag = entry.getValue().saveToNBT();
			tag.setInteger("x",entry.getKey().chunkXPos);
			tag.setInteger("z",entry.getKey().chunkZPos);
			list.appendTag(tag);
		}
		
		nbt.setTag("sections",list);
	}

	@Override
	protected void onLoad(NBTTagCompound nbt){
		NBTTagList list = nbt.getTagList("sections",NBT.TAG_COMPOUND);
		
		for(int a = 0, count = list.tagCount(); a < count; a++){
			NBTTagCompound tag = list.getCompoundTagAt(a);
			chunkMap.put(new ChunkCoordIntPair(tag.getInteger("x"),tag.getInteger("z")),EnergyChunkData.readFromNBT(tag));
		}
	}
}

package chylex.hee.system.savedata.types;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import chylex.hee.mechanics.energy.EnergyChunkData;
import chylex.hee.system.savedata.WorldSavefile;

public class EnergySavefile extends WorldSavefile{
	public static byte sectionSize = 3;
	
	private TIntObjectHashMap<EnergyChunkData> chunkMap = new TIntObjectHashMap<>();
	
	public EnergySavefile(){
		super("energy.nbt");
	}
	
	public EnergyChunkData getFromChunkCoords(int chunkX, int chunkZ){
		if (chunkX < 0)--chunkX;
		if (chunkZ < 0)--chunkZ;
		
		int index = (chunkX/sectionSize)*sectionSize+(chunkZ/sectionSize);
		
		EnergyChunkData data = chunkMap.get(index);
		if (data != null)return data;

		chunkMap.put(0,data = new EnergyChunkData());
		return data;
	}
	
	public EnergyChunkData getFromBlockCoords(int blockX, int blockZ){
		return getFromChunkCoords(blockX>>4,blockZ>>4);
	}

	@Override
	protected void onSave(NBTTagCompound nbt){
		NBTTagList list = new NBTTagList();
		
		for(TIntIterator iter = chunkMap.keySet().iterator(); iter.hasNext();){
			int index = iter.next();
			
			NBTTagCompound tag = chunkMap.get(index).saveToNBT();
			tag.setInteger("i",index);
			list.appendTag(tag);
		}
		
		nbt.setTag("sections",list);
	}

	@Override
	protected void onLoad(NBTTagCompound nbt){
		NBTTagList list = nbt.getTagList("sections",NBT.TAG_COMPOUND);
		
		for(int a = 0, count = list.tagCount(); a < count; a++){
			NBTTagCompound tag = list.getCompoundTagAt(a);
			chunkMap.put(tag.getInteger("i"),EnergyChunkData.readFromNBT(tag));
		}
	}
}

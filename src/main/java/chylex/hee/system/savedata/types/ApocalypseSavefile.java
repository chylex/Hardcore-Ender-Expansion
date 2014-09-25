package chylex.hee.system.savedata.types;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.system.savedata.WorldSavefile;
import chylex.hee.world.structure.island.biome.IslandBiomeBase;

public class ApocalypseSavefile extends WorldSavefile{
	private Map<String,byte[]> rituals = new HashMap<>();
	private TObjectIntHashMap<String> times = new TObjectIntHashMap<>();
	
	public ApocalypseSavefile(String filename){
		super(filename);
	}
	
	public boolean addRitual(String username, byte biomeId){
		byte[] ritualArray = rituals.get(username);
		
		if (ritualArray == null)rituals.put(username,ritualArray = new byte[]{ biomeId });
		else if (!ArrayUtils.contains(ritualArray,biomeId))ArrayUtils.add(ritualArray,biomeId);
		
		if (ritualArray.length >= IslandBiomeBase.biomeList.size()){
			addApocalypseTime(username);
			return true;
		}
		else return false;
	}
	
	public boolean hasFinishedRitual(String username, byte biomeId){
		return ArrayUtils.contains(rituals.get(username),biomeId);
	}
	
	public boolean isMidApocalypse(String username){
		return times.containsKey(username);
	}
	
	public void addApocalypseTime(String username){
		times.adjustOrPutValue(username,1,1);
	}
	
	public int getApocalypseTime(String username){
		int time = times.get(username);
		return time == times.getNoEntryValue() ? 0 : time;
	}
	
	public void resetApocalypseTime(String username){
		times.remove(username);
	}

	@Override
	protected void onSave(NBTTagCompound nbt){
		NBTTagCompound ritualTag = nbt.getCompoundTag("rituals");
		for(Entry<String,byte[]> entry:rituals.entrySet())ritualTag.setByteArray(entry.getKey(),entry.getValue());
		nbt.setTag("rituals",ritualTag);
		
		NBTTagCompound timeTag = nbt.getCompoundTag("times");
		for(String username:times.keySet())timeTag.setShort(username,(short)times.get(username));
		nbt.setTag("times",timeTag);
	}

	@Override
	protected void onLoad(NBTTagCompound nbt){
		NBTTagCompound ritualTag = nbt.getCompoundTag("rituals");
		
		for(Object o:ritualTag.func_150296_c()){
			String key = (String)o;
			rituals.put(key,ritualTag.getByteArray(key));
		}
		
		
		NBTTagCompound timeTag = nbt.getCompoundTag("times");
		
		for(Object o:ritualTag.func_150296_c()){
			String key = (String)o;
			times.put(key,timeTag.getShort(key));
		}
	}
}

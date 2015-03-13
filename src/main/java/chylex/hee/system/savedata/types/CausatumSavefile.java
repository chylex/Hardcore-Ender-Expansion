package chylex.hee.system.savedata.types;
import gnu.trove.iterator.TObjectFloatIterator;
import gnu.trove.map.hash.TObjectFloatHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.mechanics.causatum.CausatumMeters;
import chylex.hee.system.logging.Log;
import chylex.hee.system.savedata.WorldSavefile;

public class CausatumSavefile extends WorldSavefile{
	private Map<UUID,TObjectFloatHashMap<CausatumMeters>> levels = new HashMap<>();
	
	public CausatumSavefile(){
		super("causatum.nbt");
	}
	
	public float getTotalLevel(EntityPlayer player){
		TObjectFloatHashMap<CausatumMeters> map = levels.get(player.getUniqueID());
		if (map == null)return 0F;
		
		float value = 0F;
		for(float val:map.values())value += val;
		return value;
	}
	
	public float getLevel(UUID id, CausatumMeters meter){
		TObjectFloatHashMap<CausatumMeters> map = levels.get(id);
		if (map == null)return 0F;
		
		float value = map.get(meter);
		return value == map.getNoEntryValue() ? 0F : value;
	}
	
	public float increaseLevel(UUID id, CausatumMeters meter, float add){
		TObjectFloatHashMap<CausatumMeters> map = levels.get(id);
		if (map == null)levels.put(id,map = new TObjectFloatHashMap<>());
		
		float value = map.adjustOrPutValue(meter,add,add);
		if (value > meter.limit)map.put(meter,value = meter.limit);
		return value;
	}

	@Override
	protected void onSave(NBTTagCompound nbt){
		for(Entry<UUID,TObjectFloatHashMap<CausatumMeters>> entry:levels.entrySet()){
			NBTTagCompound tag = new NBTTagCompound();
			
			for(TObjectFloatIterator<CausatumMeters> iter = entry.getValue().iterator(); iter.hasNext();){
				iter.advance();
				tag.setFloat(iter.key().toString(),iter.value());
			}
			
			nbt.setTag(entry.getKey().toString(),tag);
		}
	}

	@Override
	protected void onLoad(NBTTagCompound nbt){
		levels.clear();
		
		for(String key:(Set<String>)nbt.func_150296_c()){
			TObjectFloatHashMap<CausatumMeters> map = new TObjectFloatHashMap<>();
			NBTTagCompound tag = nbt.getCompoundTag(key);
			
			for(String name:(Set<String>)tag.func_150296_c()){
				try{
					CausatumMeters meter = CausatumMeters.valueOf(name);
					map.put(meter,tag.getFloat(name));
				}catch(IllegalArgumentException e){
					Log.error("Unknown CausatumMeters entry: "+name);
				}
			}
			
			levels.put(UUID.fromString(key),map);
		}
	}
}

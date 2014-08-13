package chylex.hee.system.savedata.old;
/*import gnu.trove.map.hash.TObjectShortHashMap;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.world.structure.island.biome.IslandBiomeBase;

public class ApocalypseSavefile extends Savefile{
	public static final byte DISABLED = 0,
							 WAITING_FOR_OVERWORLD = 1,
					   		 WAITING_FOR_NOON = 2,
					   		 WAITING_FOR_NIGHT = 3,
					   		 APOCALYPSE_RUNNING = 4,
					   		 APOCALYPSE_BOSS = 5,
					   		 APOCALYPSE_BOSS_DEAD = 6;
	
	private TObjectShortHashMap<String> times = new TObjectShortHashMap<>();
	
	public ApocalypseSavefile(WorldData worldData){
		super(worldData,"endermanpocalypse.nbt");
	}
	
	@Override
	protected void onLoad(){
		times.clear();
		
		NBTTagCompound tag = nbt.getCompoundTag("times");
		for(Object o:tag.func_150296_c()){
			String key = (String)o;
			times.put(key,tag.getShort(key));
		}
	}
	
	/**
	 * Adds a biome to player's ritual list and returns true if all biomes are finished.
	 */
	/*public boolean addRitual(String username, byte biomeId){
		NBTTagCompound rituals = nbt.getCompoundTag("rituals");
		byte[] ritualArray = rituals.getByteArray(username);
		if (!ArrayUtils.contains(ritualArray,biomeId))ArrayUtils.add(ritualArray,biomeId);
		
		rituals.setByteArray(username,ritualArray);
		nbt.setTag("rituals",rituals);
		
		if (ritualArray.length >= IslandBiomeBase.biomeList.size()){
			addApocalypseTime(username);
			save();
			return true;
		}
		else{
			save();
			return false;
		}
	}
	
	public boolean hasFinishedRitual(String username, byte biomeId){
		NBTTagCompound rituals;
		if (nbt.hasKey("rituals") && (rituals = nbt.getCompoundTag("rituals")).hasKey(username))return ArrayUtils.contains(rituals.getByteArray(username),biomeId);
		return false;
	}
	
	public boolean isMidApocalypse(String username){
		return times.containsKey(username);
	}
	
	public void addApocalypseTime(String username){
		short time = times.containsKey(username) ? (short)(times.get(username)+1) : 1;
		times.put(username,time);
		
		if (time == 1 || time%20 == 0){
			NBTTagCompound tag = nbt.getCompoundTag("times");
			tag.setShort(username,time);
			nbt.setTag("times",tag);
			save();
		}
	}
	
	public short getApocalypseTime(String username){
		return times.containsKey(username) ? times.get(username) : -1;
	}
	
	public void resetApocalypseTime(String username){
		if (times.remove(username) != times.getNoEntryValue()){
			nbt.getCompoundTag("times").removeTag(username);
			save();
		}
	}
}*/

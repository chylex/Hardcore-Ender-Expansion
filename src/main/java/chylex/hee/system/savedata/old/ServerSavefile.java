package chylex.hee.system.savedata.old;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants;

public class ServerSavefile extends Savefile{
	private int dragonDeathCache;
	
	public ServerSavefile(WorldData worldData){
		super(worldData,"server.nbt");
	}
	
	@Override
	protected void onLoad(){
		dragonDeathCache = Math.max(0,nbt.getShort("dragonDeaths")); // heavily used
	}
	
	/*
	 * ENDER CRYSTALS
	 */
	
	public String addCrystal(int posX, int posY, int posZ){
		if (!nbt.hasKey("crystals"))nbt.setTag("crystals",new NBTTagCompound());
		
		int[] loc = new int[]{ posX, posY, posZ };
		String key = "l-"+Arrays.hashCode(loc);
		
		nbt.getCompoundTag("crystals").setIntArray(key,loc);
		save();
		
		return key;
	}
	
	public void destroyCrystal(String key){
		nbt.getCompoundTag("crystals").removeTag(key);
		save();
	}
	
	public int countCrystals(){
		return nbt.getCompoundTag("crystals").func_150296_c().size(); // OBFUSCATED get tag list
	}
	
	public void resetCrystals(){
		nbt.removeTag("crystals");
	}
	
	/*
	 * DRAGON DEATH
	 */
	
	public void setDragonDead(boolean isDead){
		nbt.setBoolean("dragonDead",isDead);
		save();
	}
	
	public boolean isDragonDead(){
		return nbt.getBoolean("dragonDead") || !nbt.hasKey("dragonTicked");
	}
	
	public void addDragonDeath(){
		nbt.setShort("dragonDeaths",(short)(nbt.getShort("dragonDeaths")+1));
		nbt.removeTag("dragonTicked");
		save();
	}
	
	public int getDragonDeathAmount(){
		return dragonDeathCache;
	}
	
	public void setDragonExists(){
		nbt.setBoolean("dragonTicked",true);
	}
	
	/*
	 * TEMPLE PLAYERS
	 */
	
	public void setPlayerIsInTemple(String player, boolean isInTemple){
		NBTTagList list = nbt.getTagList("templePlayers",Constants.NBT.TAG_STRING);
		int foundId = -1;
		for(int a = 0; a < list.tagCount(); a++){
			if (player.equals(list.getStringTagAt(a))){
				foundId = a;
				break;
			}
		}
		
		if (isInTemple && foundId == -1)list.appendTag(new NBTTagString(player));
		else if (!isInTemple && foundId >= 0)list.removeTag(foundId);
		
		nbt.setTag("templePlayers",list);
		save();
	}
	
	public List<String> getPlayersInTemple(){
		List<String> players = new ArrayList<>();
		
		NBTTagList list = nbt.getTagList("templePlayers",Constants.NBT.TAG_STRING);
		for(int a = 0; a < list.tagCount(); a++)players.add(list.getStringTagAt(a));
		
		return players;
	}
	
	public void resetPlayersInTemple(){
		nbt.removeTag("templePlayers");
		save();
	}
	
	/*
	 * TEMPLE DESTRUCTION
	 */
	
	public void setPreventTempleDestruction(boolean preventTempleDestruction){
		nbt.setBoolean("noTempleDestruct",preventTempleDestruction);
		save();
	}
	
	public boolean shouldPreventTempleDestruction(){
		return nbt.getBoolean("noTempleDestruct");
	}
	
	/*
	 * DESTROY END
	 */
	
	public void setDestroyEnd(boolean should){
		if (!load())return;
		nbt.setBoolean("destroyEnd",should);
		save();
	}

	public boolean shouldDestroyEnd(){
		return nbt.getBoolean("destroyEnd");
	}
}

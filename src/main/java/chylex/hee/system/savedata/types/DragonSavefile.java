package chylex.hee.system.savedata.types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.util.Constants.NBT;
import chylex.hee.system.savedata.WorldSavefile;
import chylex.hee.system.util.DragonUtil;

public class DragonSavefile extends WorldSavefile{
	private Map<String,ChunkCoordinates> crystals = new HashMap<>();
	private Set<UUID> templePlayers = new HashSet<>();
	private boolean isDragonDead;
	private boolean hasDragonTicked;
	private int dragonDeathCount;
	private boolean preventTempleDestruction;
	private boolean shouldDestroyEnd;
	
	public DragonSavefile(){
		super("server.nbt");
	}
	
	public String addCrystal(int posX, int posY, int posZ){
		String key = "l-"+Arrays.hashCode(new int[]{ posX, posY, posZ });
		crystals.put(key,new ChunkCoordinates(posX,posY,posZ));
		setModified();
		return key;
	}
	
	public void destroyCrystal(String key){
		crystals.remove(key);
		setModified();
	}
	
	public int countCrystals(){
		return crystals.size();
	}
	
	public void resetCrystals(){
		crystals.clear();
		setModified();
	}
	
	public void setDragonDead(boolean isDead){
		this.isDragonDead = isDead;
		setModified();
	}
	
	public boolean isDragonDead(){
		return isDragonDead || !hasDragonTicked;
	}
	
	public void addDragonDeath(){
		++dragonDeathCount;
		hasDragonTicked = false;
		setModified();
	}
	
	public int getDragonDeathAmount(){
		return dragonDeathCount;
	}
	
	public void setDragonExists(){
		hasDragonTicked = true;
		setModified();
	}
	
	public void setPlayerIsInTemple(EntityPlayer player, boolean isInTemple){
		UUID playerID = player.getGameProfile().getId();
		boolean contains = templePlayers.contains(playerID);
		
		if (isInTemple && !contains)templePlayers.add(playerID);
		else if (!isInTemple && contains)templePlayers.remove(player.getGameProfile().getId());
		
		setModified();
	}
	
	public Set<UUID> getPlayersInTemple(){
		return new HashSet<>(templePlayers);
	}
	
	public void resetPlayersInTemple(){
		templePlayers.clear();
		setModified();
	}
	
	public void setPreventTempleDestruction(boolean preventTempleDestruction){
		this.preventTempleDestruction = preventTempleDestruction;
		setModified();
	}
	
	public boolean shouldPreventTempleDestruction(){
		return preventTempleDestruction;
	}
	
	public void setDestroyEnd(boolean should){
		this.shouldDestroyEnd = should;
		setModified();
	}

	public boolean shouldDestroyEnd(){
		return shouldDestroyEnd;
	}

	@Override
	protected void onSave(NBTTagCompound nbt){
		nbt.setShort("dragonDeaths",(short)dragonDeathCount);
		nbt.setBoolean("dragonDead",isDragonDead);
		nbt.setBoolean("dragonTicked",hasDragonTicked);
		nbt.setBoolean("noTempleDestruct",preventTempleDestruction);
		nbt.setBoolean("destroyEnd",shouldDestroyEnd);
		
		NBTTagCompound tagCrystals = new NBTTagCompound();
		for(Entry<String,ChunkCoordinates> entry:crystals.entrySet()){
			ChunkCoordinates coords = entry.getValue();
			tagCrystals.setIntArray(entry.getKey(),new int[]{ coords.posX, coords.posY, coords.posZ });
		}
		nbt.setTag("crystals",tagCrystals);
		
		NBTTagList tagTemplePlayers = new NBTTagList();
		for(UUID uuid:templePlayers)tagTemplePlayers.appendTag(new NBTTagString(uuid.toString()));
		nbt.setTag("templePlayers",tagTemplePlayers);
	}

	@Override
	protected void onLoad(NBTTagCompound nbt){
		dragonDeathCount = nbt.getShort("dragonDeaths");
		isDragonDead = nbt.getBoolean("dragonDead");
		hasDragonTicked = nbt.getBoolean("dragonTicked");
		preventTempleDestruction = nbt.getBoolean("noTempleDestruct");
		shouldDestroyEnd = nbt.getBoolean("destroyEnd");
		
		NBTTagCompound tagCrystals = nbt.getCompoundTag("crystals");
		for(Object o:tagCrystals.func_150296_c()){
			String key = (String)o;
			int[] coords = tagCrystals.getIntArray(key);
			if (coords.length == 3)crystals.put(key,new ChunkCoordinates(coords[0],coords[1],coords[2]));
		}
		
		NBTTagList tagTemplePlayers = nbt.getTagList("templePlayers",NBT.TAG_STRING);
		for(int a = 0; a < tagTemplePlayers.tagCount(); a++){
			templePlayers.add(DragonUtil.convertNameToUUID(tagTemplePlayers.getStringTagAt(a)));
		}
	}
}

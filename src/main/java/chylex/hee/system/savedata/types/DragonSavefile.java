package chylex.hee.system.savedata.types;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.common.util.Constants.NBT;
import chylex.hee.system.savedata.WorldSavefile;

public class DragonSavefile extends WorldSavefile{
	private Map<String,ChunkCoordinates> crystals = new HashMap<>();
	private Set<UUID> templePlayers = new HashSet<>();
	private ChunkCoordIntPair lastDragonChunk = new ChunkCoordIntPair(0,0);
	private boolean isDragonDead;
	private int dragonDeathCount;
	
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
	
	public void setLastDragonChunk(int x, int z){
		lastDragonChunk = new ChunkCoordIntPair(x,z);
		setModified();
	}
	
	public ChunkCoordIntPair getLastDragonChunk(){
		return lastDragonChunk;
	}
	
	public void setDragonDead(boolean isDead){
		isDragonDead = isDead;
		setModified();
	}
	
	public boolean isDragonDead(){
		return isDragonDead;
	}
	
	public void addDragonDeath(){
		++dragonDeathCount;
		setModified();
	}
	
	public int getDragonDeathAmount(){
		return dragonDeathCount;
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

	@Override
	protected void onSave(NBTTagCompound nbt){
		nbt.setShort("dragonDeaths",(short)dragonDeathCount);
		nbt.setBoolean("dragonDead",isDragonDead);
		nbt.setIntArray("lastChunk",new int[]{ lastDragonChunk.chunkXPos, lastDragonChunk.chunkZPos });
		
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
		
		int[] lastChunk = nbt.getIntArray("lastChunk");
		if (lastChunk.length == 2)lastDragonChunk = new ChunkCoordIntPair(lastChunk[0],lastChunk[1]);
		
		NBTTagCompound tagCrystals = nbt.getCompoundTag("crystals");
		
		for(String key:(Set<String>)tagCrystals.func_150296_c()){
			int[] coords = tagCrystals.getIntArray(key);
			if (coords.length == 3)crystals.put(key,new ChunkCoordinates(coords[0],coords[1],coords[2]));
		}
		
		NBTTagList tagTemplePlayers = nbt.getTagList("templePlayers",NBT.TAG_STRING);
		
		for(int a = 0; a < tagTemplePlayers.tagCount(); a++){
			templePlayers.add(UUID.fromString(tagTemplePlayers.getStringTagAt(a)));
		}
	}
}

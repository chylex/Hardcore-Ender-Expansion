package chylex.hee.system.savedata.types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.BlockPos;
import net.minecraftforge.common.util.Constants.NBT;
import chylex.hee.mechanics.misc.TempleEvents;
import chylex.hee.system.savedata.WorldSavefile;
import chylex.hee.system.util.BlockPosM;
import chylex.hee.system.util.DragonUtil;

public class DragonSavefile extends WorldSavefile{
	private List<BlockPosM> crystals = new ArrayList<>();
	private Set<UUID> templePlayers = new HashSet<>();
	private BlockPosM portalEggLocation = new BlockPosM(0,100,0);
	private boolean isDragonDead;
	private boolean hasDragonTicked;
	private int dragonDeathCount;
	private boolean preventTempleDestruction;
	private boolean shouldDestroyEnd;
	
	public DragonSavefile(){
		super("server.nbt");
	}
	
	public long addCrystal(BlockPosM pos){
		crystals.add(pos);
		setModified();
		return pos.toLong();
	}
	
	public void destroyCrystal(long key){
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
	
	public BlockPosM getPortalEggLocation(){
		return portalEggLocation;
	}

	@Override
	protected void onSave(NBTTagCompound nbt){
		nbt.setShort("dragonDeaths",(short)dragonDeathCount);
		nbt.setBoolean("dragonDead",isDragonDead);
		nbt.setBoolean("dragonTicked",hasDragonTicked);
		nbt.setBoolean("noTempleDestruct",preventTempleDestruction);
		nbt.setBoolean("destroyEnd",shouldDestroyEnd);
		nbt.setLong("portalCoordsL",portalEggLocation.toLong());
		
		NBTTagList tagCrystals = new NBTTagList();
		for(BlockPosM pos:crystals)tagCrystals.appendTag(new NBTTagLong(pos.toLong()));
		nbt.setTag("crystalsL",tagCrystals);
		
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
		if ((shouldDestroyEnd = nbt.getBoolean("destroyEnd")) == true)TempleEvents.destroyWorld();
		
		if (nbt.hasKey("portalCoordsL"))portalEggLocation = new BlockPosM(BlockPos.fromLong(nbt.getLong("portalCoordsL")));
		else{
			int[] portalCoords = nbt.getIntArray("portalCoords");
			if (portalCoords.length == 3)portalEggLocation = new BlockPosM(portalCoords[0],portalCoords[1],portalCoords[2]);
		}
		
		NBTTagList tagCrystals = nbt.getTagList("crystalsL",NBT.TAG_LONG);
		for(int a = 0; a < tagCrystals.tagCount(); a++)crystals.add(new BlockPosM(((NBTTagLong)tagCrystals.get(a)).getLong()));
		
		NBTTagList tagTemplePlayers = nbt.getTagList("templePlayers",NBT.TAG_STRING);
		for(int a = 0; a < tagTemplePlayers.tagCount(); a++)templePlayers.add(UUID.toString(tagTemplePlayers.getStringTagAt(a)));
	}
}

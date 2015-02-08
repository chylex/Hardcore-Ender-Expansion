package chylex.hee.entity.boss.dragon.managers;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.system.achievements.AchievementEvents;
import chylex.hee.system.achievements.AchievementManager;

public class DragonAchievementManager{
	private final EntityBossDragon dragon;
	private final Map<UUID,AchievementData> playerData;
	private int battleTimer;
	private byte playerCheck;
	
	public DragonAchievementManager(EntityBossDragon dragon){
		this.dragon = dragon;
		playerData = new HashMap<>();
	}
	
	public void updateManager(){
		if (++playerCheck > 100){
			playerCheck = 0;
			++battleTimer;
			
			for(EntityPlayer player:(List<EntityPlayer>)dragon.worldObj.playerEntities){
				if (player.capabilities.isCreativeMode)continue;
				AchievementData data = getData(player.getUniqueID());
				++data.participationCounter;
			}
		}
	}
	
	private AchievementData getData(UUID id){
		AchievementData data = playerData.get(id);
		if (data == null)playerData.put(id,data = new AchievementData());
		return data;
	}
	
	public void onPlayerDied(EntityPlayer player){
		++getData(player.getUniqueID()).deathAmount;
	}
	
	public void onPlayerKilledEnderman(EntityPlayer player){
		getData(player.getUniqueID()).killedEnderman = true;
	}
	
	public void onBattleFinished(){
		if (battleTimer < 24)return; // quicker than 2 minutes must be fake or testing
		int finalDiff = dragon.rewards.getFinalDifficultyRaw();
		if (finalDiff < 68)return; // no challenge can be done on peaceful/easy
		
		for(Entry<UUID,AchievementData> entry:playerData.entrySet()){
			if ((float)entry.getValue().participationCounter/(float)battleTimer < 0.75F)continue;
			
			EntityPlayer player = dragon.worldObj.func_152378_a(entry.getKey());
			
			if (entry.getValue().deathAmount == 0 && finalDiff >= 98){
				if (player == null)AchievementEvents.addDelayedAchievement(entry.getKey(),AchievementManager.CHALLENGE_HARD0DEATHS);
				else player.addStat(AchievementManager.CHALLENGE_HARD0DEATHS,1);
			}
			
			if (!entry.getValue().killedEnderman){
				if (player == null)AchievementEvents.addDelayedAchievement(entry.getKey(),AchievementManager.CHALLENGE_NOENDERMAN);
				else player.addStat(AchievementManager.CHALLENGE_NOENDERMAN,1);
			}
		}
	}
	
	public NBTTagCompound writeToNBT(){
		NBTTagCompound tag = new NBTTagCompound();
		for(Entry<UUID,AchievementData> entry:playerData.entrySet())tag.setTag(entry.getKey().toString(),entry.getValue().writeToNBT());
		tag.setInteger("___timer",battleTimer);
		return tag;
	}

	public void readFromNBT(NBTTagCompound tag){
		for(String key:(Set<String>)tag.func_150296_c()){ // OBFUSCATED get keys
			NBTBase b = tag.getTag(key);
			
			if (b instanceof NBTTagCompound){
				NBTTagCompound compound = (NBTTagCompound)b;
				getData(UUID.fromString(key)).readFromNBT(compound);
			}
		}
		
		battleTimer = tag.getInteger("___timer");
	}
	
	class AchievementData{
		private short participationCounter;
		private short deathAmount;
		private boolean killedEnderman;
		
		private final NBTTagCompound writeToNBT(){
			NBTTagCompound tag = new NBTTagCompound();
			tag.setShort("participatonCnt",participationCounter);
			tag.setShort("deathAmount",deathAmount);
			tag.setBoolean("killedEnderman",killedEnderman);
			return tag;
		}
	
		private final void readFromNBT(NBTTagCompound tag){
			participationCounter = tag.getShort("participationCnt");
			deathAmount = tag.getShort("deathAmount");
			killedEnderman = tag.getBoolean("killedEnderman");
		}
	}
}

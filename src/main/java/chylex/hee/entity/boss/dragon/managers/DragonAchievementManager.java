package chylex.hee.entity.boss.dragon.managers;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.game.achievements.AchievementEvents;
import chylex.hee.game.achievements.AchievementManager;
import chylex.hee.system.abstractions.nbt.NBTCompound;

public class DragonAchievementManager{
	private final EntityBossDragon dragon;
	private final Map<UUID, AchievementData> playerData;
	private int battleTimer;
	private int playerCheck;
	
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
		if (data == null)playerData.put(id, data = new AchievementData());
		return data;
	}
	
	public void onPlayerDied(EntityPlayer player){
		++getData(player.getUniqueID()).deathAmount;
	}
	
	public void onPlayerKilledEnderman(EntityPlayer player){ // TODO reimplement
		getData(player.getUniqueID()).killedEnderman = true;
	}
	
	public void onBattleFinished(){
		if (battleTimer < 24)return; // quicker than 2 minutes must be fake or testing
		int finalDiff = dragon.rewards.getFinalDifficultyRaw();
		
		for(Entry<UUID, AchievementData> entry:playerData.entrySet()){
			if ((float)entry.getValue().participationCounter/(float)battleTimer < 0.75F)continue;

			// TODO CausatumUtils.increase(entry.getKey(), CausatumMeters.DRAGON_KILL_PARTICIPATION, 300);
			if (finalDiff < 68)continue; // no challenge can be done on peaceful/easy
			
			EntityPlayer player = dragon.worldObj.func_152378_a(entry.getKey());
			
			if (entry.getValue().deathAmount == 0 && finalDiff >= 98){
				if (player == null)AchievementEvents.addDelayedAchievement(entry.getKey(), AchievementManager.CHALLENGE_HARD0DEATHS);
				else player.addStat(AchievementManager.CHALLENGE_HARD0DEATHS, 1);
			}
			
			if (!entry.getValue().killedEnderman){
				if (player == null)AchievementEvents.addDelayedAchievement(entry.getKey(), AchievementManager.CHALLENGE_NOENDERMAN);
				else player.addStat(AchievementManager.CHALLENGE_NOENDERMAN, 1);
			}
		}
	}
	
	public NBTCompound writeToNBT(){
		NBTCompound tag = new NBTCompound();
		for(Entry<UUID, AchievementData> entry:playerData.entrySet())tag.setCompound(entry.getKey().toString(), entry.getValue().writeToNBT());
		tag.setInt("___timer", battleTimer);
		return tag;
	}

	public void readFromNBT(NBTCompound tag){
		tag.forEachCompound((key, value) -> getData(UUID.fromString(key)).readFromNBT(value));
		battleTimer = tag.getInt("___timer");
	}
	
	static final class AchievementData{
		private short participationCounter;
		private short deathAmount;
		private boolean killedEnderman;
		
		private final NBTCompound writeToNBT(){
			NBTCompound tag = new NBTCompound();
			tag.setShort("participationCnt", participationCounter);
			tag.setShort("deathAmount", deathAmount);
			tag.setBool("killedEnderman", killedEnderman);
			return tag;
		}
	
		private final void readFromNBT(NBTCompound tag){
			participationCounter = tag.getShort("participationCnt");
			deathAmount = tag.getShort("deathAmount");
			killedEnderman = tag.getBool("killedEnderman");
		}
	}
}

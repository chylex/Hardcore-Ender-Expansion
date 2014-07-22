package chylex.hee.entity.boss.dragon.managers;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.system.achievements.AchievementManager;

public class DragonAchievementManager{
	private EntityBossDragon dragon;
	private Map<String,AchievementData> playerData;
	private int battleTimer = 0;
	private byte playerCheck = 0;
	
	public DragonAchievementManager(EntityBossDragon dragon){
		this.dragon = dragon;
		playerData = new HashMap<String,AchievementData>();
	}
	
	public void updateManager(){
		if (++playerCheck > 100){
			playerCheck = 0;
			++battleTimer;
			
			for(Object o:dragon.worldObj.playerEntities){
				EntityPlayer player = (EntityPlayer)o;
				if (player.capabilities.isCreativeMode)continue;
				AchievementData data = getData(player.getCommandSenderName());
				++data.participationCounter;
			}
		}
	}
	
	private AchievementData getData(String username){
		AchievementData data = playerData.get(username);
		if (data == null)playerData.put(username,data = new AchievementData());
		return data;
	}
	
	public void onPlayerDied(String username){
		++getData(username).deathAmount;
	}
	
	public void onPlayerKilledEnderman(String username){
		getData(username).killedEnderman = true;
	}
	
	public void onBattleFinished(){
		if (battleTimer < 24)return; // quicker than 2 minutes must be fake or testing
		int finalDiff = dragon.rewards.getFinalDifficultyRaw();
		if (finalDiff < 68)return; // no challenge can be done on peaceful/easy
		
		for(Entry<String,AchievementData> entry:playerData.entrySet()){
			if ((float)entry.getValue().participationCounter/(float)battleTimer < 0.75F)continue;
			
			EntityPlayer player = null;
			for(Object o:dragon.worldObj.playerEntities){
				EntityPlayer tmpPlayer = (EntityPlayer)o;
				if (tmpPlayer.getCommandSenderName().equals(entry.getKey())){
					player = tmpPlayer;
					break;
				}
			}
			
			if (player == null)continue;

			AchievementData data = getData(player.getCommandSenderName());
			if (data.deathAmount == 0 && finalDiff >= 98)player.addStat(AchievementManager.CHALLENGE_HARD0DEATHS,1);
			if (!data.killedEnderman)player.addStat(AchievementManager.CHALLENGE_NOENDERMAN,1);
		}
	}
	
	public NBTTagCompound writeToNBT(){
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("___timer",battleTimer);
		for(Entry<String,AchievementData> entry:playerData.entrySet())tag.setTag(entry.getKey(),entry.getValue().writeToNBT());
		return tag;
	}

	public void readFromNBT(NBTTagCompound tag){
		battleTimer = tag.hasKey("__timer") ? tag.getInteger("___timer") : tag.getInteger("timer"); // TODO review later and remove legacy compatibility
		
		for(Object o:tag.func_150296_c()){ // OBFUSCATED list tags?
			String key = (String)o;
			NBTBase b = tag.getTag(key);
			
			if (b instanceof NBTTagCompound){
				NBTTagCompound compound = (NBTTagCompound)b;
				getData(key).readFromNBT(compound);
			}
		}
	}
	
	class AchievementData{
		private short participationCounter = 0;
		private short deathAmount = 0;
		private boolean killedEnderman = false;
		
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

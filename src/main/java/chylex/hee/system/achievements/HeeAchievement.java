package chylex.hee.system.achievements;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;

public class HeeAchievement extends Achievement{
	public HeeAchievement(String statId, String achievementId, int x, int y, ItemStack is, Achievement parentAchievement){
		super(statId,achievementId,x,y,is,parentAchievement);
	}
}

package chylex.hee.system.achievements;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraftforge.common.AchievementPage;
import chylex.hee.block.BlockList;
import chylex.hee.item.ItemList;
import chylex.hee.item.ItemSpecialEffects;
import chylex.hee.proxy.ModCommonProxy;

public final class AchievementManager{
	private static List<Achievement> achievements = new ArrayList<>();
	private static List<Achievement> challenges = new ArrayList<>();
	public static Map<String,String> challengeStrings = new HashMap<>();
	
	public static Achievement
		TIME_FOR_NEW_ADVENTURES = addAchievement(0, "newAdventures", 0, -1, Blocks.dragon_egg, 0, null),
		LEARNING_THE_POWERS = addAchievement(1, "learningThePowers", 2, -2, ItemList.essence, 0, TIME_FOR_NEW_ADVENTURES),
		REBIRTH = addAchievement(50, "rebirth", 1, -4, ItemList.temple_caller, 0, LEARNING_THE_POWERS),
		TRAVELING_101 = addAchievement(2, "traveling101", 0, 1, ItemList.end_powder, 0, TIME_FOR_NEW_ADVENTURES),
		SUPER_SHINY = addAchievement(51, "superShiny", -3, 1, ItemList.enhanced_ender_pearl, 0, TRAVELING_101),
		DEAD_VISIONARY = addAchievement(4, "deadVisionary", 2, 2, Items.ender_eye, 0, TRAVELING_101),
		THE_NEXT_STEP = addAchievement(9, "theNextStep", 5, 2, ItemList.endium_ingot, 0, DEAD_VISIONARY),
		WHOLE_NEW_CULTURES = addAchievement(6, "wholeNewCultures", 7, 2, BlockList.end_terrain, 0, THE_NEXT_STEP),
		AFRAID_NO_MORE = addAchievement(10, "afraidNoMore", 3, 0, BlockList.void_chest, 0, THE_NEXT_STEP),
		MAGIC_OF_DECOMPOSITION = addAchievement(7, "magicOfDecomposition", 5, 4, ItemList.stardust, 0, THE_NEXT_STEP),
		THE_MORE_YOU_KNOW = addAchievement(8, "theMoreYouKnow", -2, -1, ItemList.special_effects, ItemSpecialEffects.achievementCompendiumIcon, null),
		
		CHALLENGE_HARD0DEATHS = addChallenge(0, "hard0Deaths", "hard"),
		CHALLENGE_BEDEXPLODE = addChallenge(1, "bedExplode", "mediumorhard"),
		CHALLENGE_NOENDERMAN = addChallenge(2, "noEnderman", "mediumorhard");
	
	public static void register(){
		AchievementPage.registerAchievementPage(new AchievementPage("HEE Achievements",achievements.toArray(new Achievement[achievements.size()])));
		AchievementPage.registerAchievementPage(new AchievementPage("HEE Challenges",challenges.toArray(new Achievement[challenges.size()])));
	}
	
	private static Achievement addAchievement(int id, String stringId, int x, int y, Block block, int metadata, Achievement parentAchievement){
		return addAchievement(id,stringId,x,y,new ItemStack(block,1,metadata),parentAchievement);
	}
	
	private static Achievement addAchievement(int id, String stringId, int x, int y, Item item, int damage, Achievement parentAchievement){
		return addAchievement(id,stringId,x,y,new ItemStack(item,1,damage),parentAchievement);
	}

	private static Achievement addAchievement(int id, String stringId, int x, int y, ItemStack is, Achievement parentAchievement){
		Achievement achievement = new HeeAchievement("achievement.hee."+(ModCommonProxy.achievementStartId+40+id),stringId,x,y,is,parentAchievement).registerStat();
		achievements.add(achievement);
		return achievement;
	}
	
	private static Achievement addChallenge(int id, String stringId, String difficultyLevel){
		Achievement challenge = new HeeChallenge("achievement.hee."+(ModCommonProxy.achievementStartId+120+id),stringId,-1+(id%2 == 0?0:1),-2+id,new ItemStack(Items.diamond_sword)).registerStat();
		challenges.add(challenge);
		challengeStrings.put(stringId,difficultyLevel);
		return challenge;
	}
}

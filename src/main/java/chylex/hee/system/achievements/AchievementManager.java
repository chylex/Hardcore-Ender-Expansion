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
import chylex.hee.proxy.ModCommonProxy;

public final class AchievementManager{
	private static List<Achievement> achievements = new ArrayList<>();
	private static List<Achievement> challenges = new ArrayList<>();
	public static Map<String,String> challengeStrings = new HashMap<>();
	
	public static Achievement
		/*DIARY_FIRST_PAGE = addAchievement(0, "diaryFirstPage", -3, -1, ItemList.adventurers_diary, null), 
		DIARY_ALL_PAGES = addAchievement(1, "adventurersDiary", -3, 1, ItemList.special_effects, ItemSpecialEffects.achievementLorePageIcon, DIARY_FIRST_PAGE), 
		DRAGON_ESSENCE = addAchievement(2, "dragonEssence", 2, 0, ItemList.essence, EssenceType.DRAGON.getItemDamage(), null), 
		DRAGON_ESSENCE_ALTAR = addAchievement(10, "dragonEssenceAltar", 3, 2, Item.getItemFromBlock(BlockList.essence_altar), DRAGON_ESSENCE), 
		ENHANCED_BREWERY = addAchievement(3, "enhancedBrewery", 1, 3, ItemList.enhanced_brewing_stand, DRAGON_ESSENCE_ALTAR), 
		REBIRTH = addAchievement(4, "dragonRebirth", 1, -3, Item.getItemFromBlock(Blocks.dragon_egg), DRAGON_ESSENCE), 
		EXPLORER = addAchievement(5, "explorer", 5, -1, Item.getItemFromBlock(Blocks.end_stone), DRAGON_ESSENCE), 
		ENDER_PEARLS = addAchievement(6, "enderPearls", 6, -4, Items.ender_pearl, EXPLORER), 
		SUPER_ENDER_PEARLS = addAchievement(11, "superEnderPearls", 8, -3, ItemList.enhanced_ender_pearl, ENDER_PEARLS), 
		BURNT_TO_CRISP = addAchievement(7, "burntToCrisp", 6, 1, ItemList.igneous_rock, EXPLORER), 
		DEAD_FLOWER = addAchievement(8, "deadFlower", 7, 3, Item.getItemFromBlock(BlockList.death_flower), EXPLORER), 
		KILL_ENDER_EYE = addAchievement(9, "killEye", 4, 4, Items.ender_eye, EXPLORER), 
		KILL_FIRE_FIEND = addAchievement(12, "killFireFiend", 4, 5, ItemList.essence, EssenceType.FIERY.getItemDamage(), EXPLORER), 
		KILL_ENDER_DEMON = addAchievement(13, "killEnderDemon", 4, 6, ItemList.enderman_relic, EXPLORER),*/
		
		TIME_FOR_NEW_ADVENTURES = addAchievement(0, "newAdventures", 0, -1, Blocks.dragon_egg, 0, null), //
		LEARNING_THE_POWERS = addAchievement(1, "learningThePowers", 2, -2, ItemList.essence, 0, TIME_FOR_NEW_ADVENTURES), //
		REBIRTH = addAchievement(50, "rebirth", 1, -4, ItemList.temple_caller, 0, LEARNING_THE_POWERS), //
		TRAVELING_101 = addAchievement(2, "traveling101", 0, 1, ItemList.end_powder, 0, TIME_FOR_NEW_ADVENTURES), //
		SUPER_SHINY = addAchievement(51, "superShiny", -3, 1, ItemList.enhanced_ender_pearl, 0, TRAVELING_101), //
		DEAD_VISIONARY = addAchievement(4, "deadVisionary", 2, 2, Items.ender_eye, 0, TRAVELING_101), //
		//THAT_WAS_CLOSE = addAchievement(52, "thatWasClose", 1, 4, ItemList.spatial_dash_gem, 0, DEAD_VISIONARY),
		WHOLE_NEW_CULTURES = addAchievement(6, "wholeNewCultures", 4, 2, BlockList.end_terrain, 0, DEAD_VISIONARY), //
		MAGIC_OF_DECOMPOSITION = addAchievement(7, "magicOfDecomposition", 2, 0, ItemList.stardust, 0, DEAD_VISIONARY), //
		THE_MORE_YOU_KNOW = addAchievement(8, "theMoreYouKnow", -2, -1, ItemList.ender_compendium, 0, null),
		
		CHALLENGE_HARD0DEATHS = addChallenge(0, "hard0Deaths", "hard"), //
		CHALLENGE_BEDEXPLODE = addChallenge(1, "bedExplode", "mediumorhard"), //
		CHALLENGE_NOENDERMAN = addChallenge(2, "noEnderman", "mediumorhard"); //
	
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

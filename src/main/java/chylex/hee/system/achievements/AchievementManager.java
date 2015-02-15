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
	public static final String achievementScreenName = "HEE Achievements";
	public static final String challengeScreenName = "HEE Challenges";
	
	private static List<Achievement> achievements = new ArrayList<>();
	private static List<Achievement> challenges = new ArrayList<>();
	public static Map<String,String> challengeStrings = new HashMap<>();
	
	public static Achievement
		/*  8 */ ENDER_COMPENDIUM = addAchievement(8, "enderCompendium", -2, -1, ItemList.special_effects, ItemSpecialEffects.achievementCompendiumIcon, null),
		/*  0 */ GO_INTO_THE_END = addAchievement(0, "newAdventures", 0, -1, Blocks.dragon_egg, 0, null),
		
		/*  1 */ DRAGON_ESSENCE = addAchievement(1, "dragonEssence", 2, -2, ItemList.essence, 0, GO_INTO_THE_END),
		/* 11 */ ENHANCED_BREWING_STAND = addAchievement(11, "enhancedBrewingStand", 4, -2, ItemList.enhanced_brewing_stand, 0, DRAGON_ESSENCE),
		/* 24 */ TEMPLE_CALLER = addAchievement(24, "templeCaller", 1, -4, ItemList.temple_caller, 0, DRAGON_ESSENCE),
		
		/*  2 */ END_POWDER = addAchievement(2, "endPowder", 0, 1, ItemList.end_powder, 0, GO_INTO_THE_END).setSpecial(),
		/* 12 */ ENHANCED_ENDER_PEARL = addAchievement(12, "enhancedEnderPearl", -3, 1, Items.ender_pearl, 0, END_POWDER),
		/* 51 */ ENHANCED_ENDER_PEARL_FULL = addAchievement(51, "enhancedEnderPearlFull", -4, 3, ItemList.enhanced_ender_pearl, 0, ENHANCED_ENDER_PEARL),
		/*  4 */ ENDER_EYE_KILL = addAchievement(4, "enderEyeKill", 2, 4, Items.ender_eye, 0, END_POWDER),
		/* 52 */ TP_NEAR_VOID = addAchievement(52, "tpNearVoid", 3, 6, ItemList.spatial_dash_gem, 32766, ENDER_EYE_KILL),
		/* 14 */ TRANSPORT_BEACON = addAchievement(14, "transportBeacon", -1, 3, BlockList.transport_beacon, 0, END_POWDER),
		/*  7 */ STARDUST = addAchievement(7, "stardust", 0, 5, ItemList.stardust, 0, END_POWDER).setSpecial(),
		
		/*  9 */ ENDIUM_INGOT = addAchievement(9, "endiumIngot", 2, 2, ItemList.endium_ingot, 0, END_POWDER).setSpecial(),
		/* 10 */ VOID_CHEST = addAchievement(10, "voidChest", 3, 0, BlockList.void_chest, 0, ENDIUM_INGOT),
		/* 13 */ BIOME_COMPASS = addAchievement(13, "biomeCompass", 4, 2, ItemList.biome_compass, 0, ENDIUM_INGOT),
		
		/* 15 */ BIOME_INFESTED_FOREST = addAchievement(15, "biomeInfestedForest", 6, -1, BlockList.end_terrain, 0, BIOME_COMPASS),
		/* 18 */ CURSE = addAchievement(18, "curse", 8, -2, ItemList.curse, 0, BIOME_INFESTED_FOREST),
		/* 19 */ CHARM_POUCH = addAchievement(19, "charmPouch", 8, -1, ItemList.charm_pouch, 0, BIOME_INFESTED_FOREST),
		/* 16 */ BIOME_BURNING_MOUNTAINS = addAchievement(16, "biomeBurningMountains", 6, 2, BlockList.end_terrain, 1, BIOME_COMPASS),
		/* 20 */ FIERY_ESSSENCE = addAchievement(20, "fieryEssence", 8, 1, ItemList.essence, 1, BIOME_BURNING_MOUNTAINS),
		/* 21 */ SCORCHING_PICKAXE = addAchievement(21, "scorchingPickaxe", 8, 2, ItemList.scorching_pickaxe, 0, BIOME_BURNING_MOUNTAINS),
		/* 17 */ BIOME_ENCHANTED_ISLAND = addAchievement(17, "biomeEnchantedIsland", 6, 5, BlockList.end_terrain, 2, BIOME_COMPASS),
		/* 22 */ TRANSFERENCE_GEM = addAchievement(22, "transferenceGem", 8, 4, ItemList.transference_gem, 0, BIOME_ENCHANTED_ISLAND),
		/* 23 */ ENERGY_WAND = addAchievement(23, "energyWand", 8, 5, ItemList.energy_wand, 1, BIOME_ENCHANTED_ISLAND),
		
		/*  0 */ CHALLENGE_HARD0DEATHS = addChallenge(0, "hard0Deaths", "hard"),
		/*  1 */ CHALLENGE_BEDEXPLODE = addChallenge(1, "bedExplode", "mediumorhard"),
		/*  2 */ CHALLENGE_NOENDERMAN = addChallenge(2, "noEnderman", "mediumorhard");
	
	public static void register(){
		AchievementPage.registerAchievementPage(new AchievementPage(achievementScreenName,achievements.toArray(new Achievement[achievements.size()])));
		AchievementPage.registerAchievementPage(new AchievementPage(challengeScreenName,challenges.toArray(new Achievement[challenges.size()])));
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

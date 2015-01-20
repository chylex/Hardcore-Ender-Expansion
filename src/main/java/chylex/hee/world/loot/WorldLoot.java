package chylex.hee.world.loot;
import static net.minecraftforge.common.ChestGenHooks.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import chylex.hee.item.ItemList;
import chylex.hee.system.logging.Stopwatch;

public class WorldLoot{
	private static final ItemStack lorePage = new ItemStack(ItemList.adventurers_diary);
	
	public static void registerWorldLoot(){
		Stopwatch.time("WorldLoot - register");
		
		// ADVENTURER'S DIARY
		WeightedRandomChestContent item = new WeightedRandomChestContent(lorePage,1,1,5);
		getInfo(STRONGHOLD_LIBRARY).addItem(new WeightedRandomChestContent(lorePage,1,1,8));
		getInfo(STRONGHOLD_CORRIDOR).addItem(item);
		getInfo(MINESHAFT_CORRIDOR).addItem(item);
		getInfo(PYRAMID_JUNGLE_CHEST).addItem(item);
		getInfo(PYRAMID_DESERT_CHEST).addItem(item);
		
		// TEMPLE CALLER
		getInfo(MINESHAFT_CORRIDOR).addItem(new WeightedRandomChestContent(new ItemStack(ItemList.temple_caller),0,1,1));
		
		item = new WeightedRandomChestContent(new ItemStack(ItemList.temple_caller),0,1,1);
		for(String s:new String[]{
			DUNGEON_CHEST, PYRAMID_DESERT_CHEST, PYRAMID_JUNGLE_CHEST, VILLAGE_BLACKSMITH, STRONGHOLD_CORRIDOR, STRONGHOLD_CROSSING
		})getInfo(s).addItem(item);
		
		getInfo(STRONGHOLD_LIBRARY).addItem(new WeightedRandomChestContent(new ItemStack(ItemList.temple_caller),1,1,1));
		
		// KNOWLEDGE FRAGMENTS
		item = new WeightedRandomKnowledgeNote(6);
		for(String s:new String[]{
			DUNGEON_CHEST, PYRAMID_DESERT_CHEST, PYRAMID_JUNGLE_CHEST, VILLAGE_BLACKSMITH
		})getInfo(s).addItem(item);
		
		item = new WeightedRandomKnowledgeNote(7);
		getInfo(MINESHAFT_CORRIDOR).addItem(item);
		
		item = new WeightedRandomKnowledgeNote(9);
		for(String s:new String[]{
			STRONGHOLD_CORRIDOR, STRONGHOLD_CROSSING, STRONGHOLD_LIBRARY
		})getInfo(s).addItem(item);
		
		// MISC
		// TODO add librarian trades once the system is fucking implemented again

		Stopwatch.finish("WorldLoot - register");
	}
	
	private WorldLoot(){}
	
	/*@Override
	public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random rand){
		if (villager.getProfession() == 1){
			if (rand.nextFloat() < 0.65F)recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald,4+rand.nextInt(4)),ItemKnowledgeNote.setRandomNote(new ItemStack(ItemList.knowledge_note),rand,2)));
			else if (rand.nextFloat() < 0.6F)recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald,3+rand.nextInt(3),0),lorePage.copy()));
			else if (rand.nextFloat() < 0.3F)recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald,11+rand.nextInt(5),0),new ItemStack(BlockList.essence_altar)));
		}
	}*/
}

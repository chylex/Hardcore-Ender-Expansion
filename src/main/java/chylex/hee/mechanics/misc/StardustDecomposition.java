package chylex.hee.mechanics.misc;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.system.util.DragonUtil;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;

public final class StardustDecomposition{
	private static IdentityHashMap<Item,short[]> blacklist = new IdentityHashMap<>();

	public static void addToBlacklist(Item item, short[] damages){
		blacklist.put(item,damages.clone());
	}
	
	public static void addFromString(String list){
		if (list.isEmpty())return;
		
		int added = 0;
		String[] split = list.split(",");
		
		for(String entry:split){
			entry = entry.trim();
			if (entry.length() == 0)continue;
			
			String itemName = entry;
			short[] dmgs = new short[]{ -1 };
			
			// PARSE DAMAGE VALUES
			
			if (entry.contains("/")){
				String[] sep = entry.split("/");
				if (sep.length != 2){
					DragonUtil.severe("Invalid entry in Decomposition Blacklist: %0%",entry);
					continue;
				}
				
				itemName = sep[0];
				try{
					if (sep[1].contains("+")){
						String[] dmgVals = sep[1].split("\\+");
						dmgs = new short[dmgVals.length];
						for(int a = 0; a < dmgVals.length; a++)dmgs[a] = Short.parseShort(dmgVals[a]);
					}
					else dmgs = new short[]{ Short.parseShort(sep[1]) };
				}catch(NumberFormatException e){
					DragonUtil.severe("Invalid entry in Decomposition Blacklist, wrong damage values: %0%",entry);
					continue;
				}
			}
			
			// PARSE ENTRY ID AND NAME
			
			String[] itemId = itemName.split(":");
			if (itemId.length > 2){
				DragonUtil.severe("Invalid entry in Decomposition Blacklist, wrong item identifier: %0%",entry);
				continue;
			}
			else if (itemId.length == 1)itemId = new String[]{ "minecraft",itemId[0] };
			
			if (itemId[1].equals("*")){ // BLACKLIST ALL BLOCKS AND ITEMS FROM MOD
				String identifier = itemId[0]+":";
				
				// SEARCH ALL BLOCKS WITH SPECIFIED ID
				
				for(Object o:GameData.getBlockRegistry().getKeys()){
					String key = (String)o;
					
					if (key.startsWith(identifier)){
						Block block = GameData.getBlockRegistry().getRaw(key);
						
						if (block == null){
							if (itemId[0].equals("minecraft") || Loader.isModLoaded(itemId[0]))DragonUtil.severe("Stumbled upon invalid entry in block registry while parsing Decomposition Blacklist, object not found: %0%",key);
							continue;
						}
						
						blacklist.put(Item.getItemFromBlock(block),dmgs);
						++added;
					}
				}
				
				// SEARCH ALL ITEMS WITH SPECIFIED ID
				
				for(Object o:GameData.getItemRegistry().getKeys()){
					String key = (String)o;
					
					if (key.startsWith(identifier)){
						Item item = GameData.getItemRegistry().getRaw(key);
						
						if (item == null){
							if (itemId[0].equals("minecraft") || Loader.isModLoaded(itemId[0]))DragonUtil.severe("Stumbled upon invalid entry in item registry while parsing Decomposition Blacklist, object not found: %0%",key);
							continue;
						}
						
						blacklist.put(item,dmgs);
						++added;
					}
				}
			}
			else{ // BLACKLIST SPECIFIED ENTRY
				Item item = GameRegistry.findItem(itemId[0],itemId[1]);
				
				if (item == null){
					Block block = GameRegistry.findBlock(itemId[0],itemId[1]);
					
					if (block == null){
						DragonUtil.severe("Invalid entry in Decomposition Blacklist, item not found: %0%",entry);
						continue;
					}
					else item = Item.getItemFromBlock(block);
				}
				
				blacklist.put(item,dmgs);
				++added;
			}
		}
		
		if (added > 0)DragonUtil.info("Added %0% items into Decomposition blacklist",added);
	}
	
	public static boolean isBlacklisted(Item item, int damage){
		short[] blacklistedDamages = blacklist.get(item);
		return (blacklistedDamages != null && blacklistedDamages.length > 0 && (blacklistedDamages[0] == -1 || ArrayUtils.contains(blacklistedDamages,(short)damage)));
	}

	public static List<ItemStack> getRandomRecipeIngredientsFor(ItemStack is, Random rand){
		Item item = is.getItem();

		if (isBlacklisted(item,is.getItemDamage()))return null;
		
		List<ItemStack[]> ingredients = new ArrayList<>();

		for(Object o:CraftingManager.getInstance().getRecipeList()){
			IRecipe recipe = (IRecipe)o;
			ItemStack output = recipe.getRecipeOutput();
			if (output == null || output.getItem() != item || output.stackSize != is.stackSize || (!is.isItemStackDamageable() && output.getItemDamage() != is.getItemDamage()))continue;

			if (o instanceof ShapedRecipes){
				ingredients.add(getNonNullValues(((ShapedRecipes)o).recipeItems));
			}
			else if (o instanceof ShapelessRecipes){
				ShapelessRecipes shapeless = (ShapelessRecipes)o;

				ItemStack[] ing = new ItemStack[shapeless.recipeItems.size()];
				for(int a = 0; a < ing.length; a++)ing[a] = (ItemStack)shapeless.recipeItems.get(a);
				ingredients.add(ing);
			}
			else{
				Object[] objs = o instanceof ShapedOreRecipe ? getNonNullValues(((ShapedOreRecipe)o).getInput()) :
								o instanceof ShapelessOreRecipe ? ((ShapelessOreRecipe)o).getInput().toArray() : null;
				if (objs == null)continue;

				ItemStack[] ing = new ItemStack[objs.length];
				boolean failed = false;

				for(int a = 0; a < ing.length; a++){
					if (objs[a] instanceof ItemStack)ing[a] = (ItemStack)objs[a];
					else if (objs[a] instanceof ArrayList){
						ArrayList list = (ArrayList)objs[a];

						if (list.size() == 0){
							failed = true;
							break;
						}
						else ing[a] = (ItemStack)list.get(rand.nextInt(list.size()));
					}
				}

				if (!failed)ingredients.add(ing);
			}
		}

		if (ingredients.isEmpty())return null;

		List<ItemStack> randRecipeIngredients = new ArrayList<>(Arrays.asList(ingredients.get(rand.nextInt(ingredients.size()))));
		
		for(Iterator<ItemStack> iter = randRecipeIngredients.iterator(); iter.hasNext();){
			ItemStack ingredient = iter.next();
			if (ingredient == null)return null;
			if (ingredient.getItem().hasContainerItem(ingredient))iter.remove();
			if (isBlacklisted(ingredient.getItem(),ingredient.getItemDamage()))iter.remove();
		}
		if (randRecipeIngredients.isEmpty())return null;
		
		return randRecipeIngredients;
	}

	private static <T> T[] getNonNullValues(T[] array){
		if (array.length == 0)return array;

		int nonNull = 0, cnt = 0;
		for(T t:array){
			if (t != null)++nonNull;
		}

		T[] newArray = (T[])Array.newInstance(array.getClass().getComponentType(),nonNull);
		for(T t:array){
			if (t != null)newArray[cnt++] = t;
		}

		return newArray;
	}
}

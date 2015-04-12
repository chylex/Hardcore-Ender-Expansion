package chylex.hee.mechanics.orb;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.system.logging.Log;
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.system.util.DragonUtil;
import cpw.mods.fml.common.registry.GameRegistry;

public final class OrbAcquirableItems{
	public static boolean overrideRemoveBrokenRecipes = false;
	public static final WeightedItemList idList = new WeightedItemList();
	private static final String errorMessage = "A defective recipe has crashed the game! Another mod is creating recipes with non-existent blocks or items, which is a serious error that will cause item or even world loss when crafting these recipes. Corrupted recipes have been saved in the log with tag [HEE-ORB], please try to find which mod they belong to and report them to the respective modder. Then enable the 'overrideRemoveBrokenRecipes' option in Hardcore Ender Expansion and the game will start.";
	
	public static void initialize(){
		Stopwatch.time("OrbAcquirableItems");
		
		boolean proceed = true;
		
		for(BiomeGenBase biome:BiomeGenBase.getBiomeGenArray()){
			if (biome == null)continue;
			else if (biome.biomeID >= 128)break;
			
			idList.add(new WeightedItem(biome.topBlock,0,34));
			idList.add(new WeightedItem(biome.fillerBlock,0,34));
		}
		
		Throwable lastThrowable = null;
		
		for(Iterator<Entry<ItemStack,ItemStack>> iter = FurnaceRecipes.smelting().getSmeltingList().entrySet().iterator(); iter.hasNext();){
			Entry<ItemStack,ItemStack> entry = iter.next();
			
			try{
				int weight = 30-(int)(7F*FurnaceRecipes.smelting().func_151398_b(entry.getValue())); // OBFUSCATED get experience amount
				idList.add(new WeightedItem(entry.getKey().getItem(),0,weight));
				idList.add(new WeightedItem(entry.getValue().getItem(),0,weight));
			}catch(Throwable t){
				Log.error("[HEE-ORB] Corrupted furnace recipe: $0 <= $1",toString(entry.getValue()),toString(entry.getKey()));
				if (overrideRemoveBrokenRecipes)iter.remove();
				lastThrowable = t;
				proceed = false;
			}
		}
			
		for(Iterator<IRecipe> iter = CraftingManager.getInstance().getRecipeList().iterator(); iter.hasNext();){
			IRecipe recipe = iter.next();
			Class<?> cls = recipe.getClass();
			
			try{
				if (cls == ShapedRecipes.class){
					ShapedRecipes shaped = (ShapedRecipes)recipe;
					addItemToList(shaped.getRecipeOutput(),21-shaped.recipeWidth*2-shaped.recipeHeight*2);
					for(ItemStack is:shaped.recipeItems)addItemToList(is,23-shaped.recipeWidth*2-shaped.recipeHeight*2);
				}
				else if (cls == ShapelessRecipes.class){
					addItemToList(recipe.getRecipeOutput(),24-recipe.getRecipeSize()*2);
					for(ItemStack item:(List<ItemStack>)((ShapelessRecipes)recipe).recipeItems)addItemToList(item,25-recipe.getRecipeSize()*2);
				}
				else if (cls == ShapedOreRecipe.class){
					ShapedOreRecipe shaped = (ShapedOreRecipe)recipe;					
					int amt = DragonUtil.getNonNullValues(shaped.getInput()).length;
					
					addItemToList(shaped.getRecipeOutput(),20-amt*2);
					
					for(Object obj:shaped.getInput()){
						if (obj instanceof ItemStack)addItemToList((ItemStack)obj,19-amt*2);
						else if (obj instanceof ArrayList){
							ArrayList list = (ArrayList)obj;
							int len = list.size();
							
							for(ItemStack listObj:(ArrayList<ItemStack>)list)addItemToList(listObj,Math.max(2,19-amt*2-((len-1)*3)));
						}
					}
				}
				else if (cls == ShapelessOreRecipe.class){					
					int amt = recipe.getRecipeSize();
					
					addItemToList(recipe.getRecipeOutput(),23-amt*2);
	
					for(Object obj:((ShapelessOreRecipe)recipe).getInput()){
						if (obj instanceof ItemStack)addItemToList((ItemStack)obj,24-amt*2);
						else if (obj instanceof ArrayList){
							ArrayList list = (ArrayList)obj;
							int len = list.size();
							
							for(ItemStack listObj:(ArrayList<ItemStack>)list)addItemToList(listObj,Math.max(2,24-amt*2-((len-1)*3)));
						}
					}
				}
			}catch(Throwable t){
				if (cls == ShapedRecipes.class)Log.error("[HEE-ORB] Corrupted shaped recipe: $0 <= $1",toString(recipe.getRecipeOutput()),toString(((ShapedRecipes)recipe).recipeItems));
				else if (cls == ShapelessRecipes.class)Log.error("[HEE-ORB] Corrupted shapeless recipe: $0 <= $1",toString(recipe.getRecipeOutput()),toString(((ShapelessRecipes)recipe).recipeItems));
				else if (cls == ShapedOreRecipe.class)Log.error("[HEE-ORB] Corrupted shaped ore recipe: $0 <= $1",toString(recipe.getRecipeOutput()),toString(((ShapedOreRecipe)recipe).getInput()));
				else if (cls == ShapelessOreRecipe.class)Log.error("[HEE-ORB] Corrupted shapeless ore recipe: $0 <= $1",toString(recipe.getRecipeOutput()),toString(((ShapelessOreRecipe)recipe).getInput()));
				
				if (overrideRemoveBrokenRecipes)iter.remove();
				lastThrowable = t;
				proceed = false;
			}
		}
		
		if (!proceed && !overrideRemoveBrokenRecipes){
			RuntimeException e = new RuntimeException(errorMessage);
			e.setStackTrace(lastThrowable.getStackTrace());
			throw e;
		}
		
		/*
		 * CLEANUP OF THINGS WE DON'T WANT
		 */
		
		Item fire = Item.getItemFromBlock(Blocks.fire);
		
		for(Iterator<WeightedItem> iter = idList.iterator(); iter.hasNext();){
			Item item = iter.next().getItem();
			
			if (item == fire)iter.remove();
			else if (item instanceof ItemBlock){
				Block block = Block.getBlockFromItem(item);
				if (block instanceof IFluidBlock || block instanceof BlockLiquid)iter.remove();
			}
		}

		Stopwatch.finish("OrbAcquirableItems");
	}
	
	private static String getModID(ItemStack is){
		try{
			return GameRegistry.findUniqueIdentifierFor(is.getItem()).modId;
		}catch(NullPointerException e){
			return "<null>";
		}
	}
	
	private static void addItemToList(ItemStack is, int weight){
		if (is == null || weight <= 0)return;
		if (is.getItem() == null)is.isItemDamaged(); // throws NPE
	
		idList.add(new WeightedItem(is.getItem(),is.getItemDamage(),weight));
	}
	
	private static String toString(ItemStack is){
		if (is == null)return "<critical:stack null>";
		else if (is.getItem() == null)return "<error:null>";
		else return "["+getModID(is)+"]"+(is.stackSize > 9 ? is.toString().substring(3).replace('@','/') : is.toString().substring(2)).replace('@','/');
	}
	
	private static String toString(List list){
		return toString(list.toArray());
	}
	
	private static String toString(Object[] array){
		Object[] newArray = new Object[array.length];
		
		for(int a = 0; a < array.length; a++){
			if (array[a] == null)newArray[a] = "<empty>";
			else if (array[a] instanceof ItemStack)newArray[a] = toString((ItemStack)array[a]);
			else if (array[a] instanceof Collection){
				Collection collection = (Collection)array[a];
				
				if (collection.isEmpty())newArray[a] = "<empty-list>";
				else{
					Object obj = collection.iterator().next();
					if (obj instanceof ItemStack)newArray[a] = toString((ItemStack)obj)+"(ore)";
					else newArray[a] = "<unknown>";
				}
			}
			else newArray[a] = "<unknown-cls "+array[a].getClass().getSimpleName()+">";
		}
		
		return ArrayUtils.toString(newArray).replace(",",", ");
	}
	
	private OrbAcquirableItems(){}
}

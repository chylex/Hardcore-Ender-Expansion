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
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.item.ItemList;
import chylex.hee.system.logging.Log;
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.system.util.DragonUtil;
import cpw.mods.fml.common.registry.GameRegistry;

public final class OrbAcquirableItems{
	public static boolean overrideRemoveBrokenRecipes = false;
	public static final WeightedItemList idList = new WeightedItemList();
	private static final String errorMessage = "Hardcore Ender Expansion has found a defective recipe and cannot proceed! Another mod is likely registering a recipe BEFORE registering blocks and items, which is considered a serious error that may cause you to lose your items or your world when crafting. Please, remove other mods until this message stops appearing to find which mod causes the issue, and then report it to that mod's author. You can enable logDebuggingInfo in config to get complete list of recipes in the game, search for 'Previous entry caused an exception!' and report the broken recipes.";
	
	public static void initialize(){
		Stopwatch.time("OrbAcquirableItems");
		
		boolean proceed = true;
		
		for(BiomeGenBase biome:BiomeGenBase.getBiomeGenArray()){
			if (biome == null)continue;
			else if (biome.biomeID >= 128)break;
			
			idList.add(new WeightedItem(biome.topBlock,0,34));
			idList.add(new WeightedItem(biome.fillerBlock,0,34));
		}
		
		for(Iterator<Entry<ItemStack,ItemStack>> iter = FurnaceRecipes.smelting().getSmeltingList().entrySet().iterator(); iter.hasNext();){
			Entry<ItemStack,ItemStack> entry = iter.next();
			
			try{
				if (Log.isDebugEnabled())Log.debug("Adding a furnace recipe: $0 <= $1",toString(entry.getValue()),toString(entry.getKey()));
				
				int weight = 30-(int)(7F*FurnaceRecipes.smelting().func_151398_b(entry.getValue())); // OBFUSCATED get experience amount
				idList.add(new WeightedItem(entry.getKey().getItem(),0,weight));
				idList.add(new WeightedItem(entry.getValue().getItem(),0,weight));
			}catch(Throwable t){
				if (Log.isDebugEnabled() || overrideRemoveBrokenRecipes)Log.debug("Previous entry caused an exception!");
				if (overrideRemoveBrokenRecipes)iter.remove();
				proceed = false;
			}
		}
			
		for(Iterator<Object> iter = CraftingManager.getInstance().getRecipeList().iterator(); iter.hasNext();){
			Object o = iter.next();
			
			try{
				if (o instanceof ShapedRecipes){
					ShapedRecipes shaped = (ShapedRecipes)o;
					if (Log.isDebugEnabled())Log.debug("Adding a shaped recipe: $0 <= $1",toString(shaped.getRecipeOutput()),toString(shaped.recipeItems));
					
					addItemToList(shaped.getRecipeOutput(),21-shaped.recipeWidth*2-shaped.recipeHeight*2);
					for(ItemStack is:shaped.recipeItems)addItemToList(is,23-shaped.recipeWidth*2-shaped.recipeHeight*2);
				}
				else if (o instanceof ShapelessRecipes){
					ShapelessRecipes shapeless = (ShapelessRecipes)o;
					if (Log.isDebugEnabled())Log.debug("Adding a shapeless recipe: $0 <= $1",toString(shapeless.getRecipeOutput()),toString(shapeless.recipeItems));
					
					addItemToList(shapeless.getRecipeOutput(),24-shapeless.getRecipeSize()*2);
					for(Object item:shapeless.recipeItems)addItemToList((ItemStack)item,25-shapeless.getRecipeSize()*2);
				}
				else if (o instanceof ShapedOreRecipe){
					ShapedOreRecipe shaped = (ShapedOreRecipe)o;
					if (Log.isDebugEnabled())Log.debug("Adding a shaped ore recipe: $0 <= $1",toString(shaped.getRecipeOutput()),toString(shaped.getInput()));
					
					int amt = DragonUtil.getNonNullValues(shaped.getInput()).length;
					
					addItemToList(shaped.getRecipeOutput(),20-amt*2);
					
					for(Object obj:shaped.getInput()){
						if (obj instanceof ItemStack)addItemToList((ItemStack)obj,19-amt*2);
						else if (obj instanceof ArrayList){
							ArrayList list = (ArrayList)obj;
							int len = list.size();
							
							for(Object listObj:list)addItemToList((ItemStack)listObj,Math.max(2,19-amt*2-((len-1)*3)));
						}
					}
				}
				else if (o instanceof ShapelessOreRecipe){
					ShapelessOreRecipe shapeless = (ShapelessOreRecipe)o;
					if (Log.isDebugEnabled())Log.debug("Adding a shapeless ore recipe: $0 <= $1",toString(shapeless.getRecipeOutput()),toString(shapeless.getInput()));
					
					int amt = shapeless.getInput().size();
					
					addItemToList(shapeless.getRecipeOutput(),23-amt*2);
	
					for(Object obj:shapeless.getInput()){
						if (obj instanceof ItemStack)addItemToList((ItemStack)obj,24-amt*2);
						else if (obj instanceof ArrayList){
							ArrayList list = (ArrayList)obj;
							int len = list.size();
							
							for(Object listObj:list)addItemToList((ItemStack)listObj,Math.max(2,24-amt*2-((len-1)*3)));
						}
					}
				}
			}catch(Throwable t){
				if (Log.isDebugEnabled() || overrideRemoveBrokenRecipes)Log.debug("Previous entry caused an exception!");
				if (overrideRemoveBrokenRecipes)iter.remove();
				proceed = false;
			}
		}
		
		if (!proceed && !overrideRemoveBrokenRecipes)throw new RuntimeException(errorMessage);
		
		/*
		 * CLEANUP OF THINGS WE DON'T WANT
		 */
		
		for(Iterator<WeightedItem> iter = idList.iterator(); iter.hasNext();){
			Item item = iter.next().getItem();
			
			if (item == ItemList.enderman_relic || item == Item.getItemFromBlock(Blocks.fire))iter.remove();
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
		if (is.getItem() == null)throw new RuntimeException(errorMessage);
	
		idList.add(new WeightedItem(is.getItem(),is.getItemDamage(),weight));
	}
	
	private static String toString(ItemStack is){
		if (is == null)return "<supernull, wtf>";
		else if (is.getItem() == null)return "<null>";
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
				
				if (collection.isEmpty())newArray[a] = "<collempty>";
				else{
					Object obj = collection.iterator().next();
					if (obj instanceof ItemStack)newArray[a] = toString((ItemStack)obj)+"(ore)";
					else newArray[a] = "<unknown>";
				}
			}
			else newArray[a] = "<unknowncls "+array[a].getClass().getSimpleName()+">";
		}
		
		return ArrayUtils.toString(newArray).replace(",",", ");
	}
	
	private OrbAcquirableItems(){}
}

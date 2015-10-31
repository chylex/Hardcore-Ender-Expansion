package chylex.hee.mechanics.misc;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import chylex.hee.system.collections.CollectionUtil;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.ItemPattern;

public final class StardustDecomposition{
	private static final List<ItemPattern> patterns = new ArrayList<>();
	
	public static void addToBlacklist(ItemPattern pattern){
		patterns.add(pattern);
	}
	
	private static boolean isBlacklisted(final ItemStack is){
		return patterns.stream().anyMatch(pattern -> pattern.matches(is));
	}
	
	/**
	 * Returns list of decomposed ingredients, or null if the source item is blacklisted or the list would be empty.
	 */
	public static List<ItemStack> getRandomRecipeIngredientsFor(ItemStack is, Random rand){
		Item item = is.getItem();

		if (isBlacklisted(is))return null;
		
		List<ItemStack[]> ingredients = new ArrayList<>();

		for(IRecipe recipe:(List<IRecipe>)CraftingManager.getInstance().getRecipeList()){
			ItemStack output = recipe.getRecipeOutput();
			if (output == null || output.getItem() != item || output.stackSize != is.stackSize || (!is.isItemStackDamageable() && output.getItemDamage() != is.getItemDamage()))continue;

			if (recipe instanceof ShapedRecipes){
				ingredients.add(DragonUtil.getNonNullValues(((ShapedRecipes)recipe).recipeItems));
			}
			else if (recipe instanceof ShapelessRecipes){
				ShapelessRecipes shapeless = (ShapelessRecipes)recipe;

				ItemStack[] ing = new ItemStack[shapeless.recipeItems.size()];
				for(int a = 0; a < ing.length; a++)ing[a] = (ItemStack)shapeless.recipeItems.get(a);
				ingredients.add(ing);
			}
			else{
				Object[] objs = recipe instanceof ShapedOreRecipe ? DragonUtil.getNonNullValues(((ShapedOreRecipe)recipe).getInput()) :
								recipe instanceof ShapelessOreRecipe ? ((ShapelessOreRecipe)recipe).getInput().toArray() : null;
				if (objs == null)continue;

				ItemStack[] ing = new ItemStack[objs.length];
				boolean failed = false;

				for(int a = 0; a < ing.length; a++){
					if (objs[a] instanceof ItemStack)ing[a] = (ItemStack)objs[a];
					else if (objs[a] instanceof ArrayList){
						ArrayList list = (ArrayList)objs[a];

						if (list.isEmpty()){
							failed = true;
							break;
						}
						else ing[a] = (ItemStack)list.get(rand.nextInt(list.size()));
					}
				}

				if (!failed)ingredients.add(ing);
			}
		}
		
		while(!ingredients.isEmpty()){
			List<ItemStack> randRecipeIngredients = CollectionUtil.newList(ingredients.remove(rand.nextInt(ingredients.size())));

			for(Iterator<ItemStack> iter = randRecipeIngredients.iterator(); iter.hasNext();){
				ItemStack ingredient = iter.next();
				if (ingredient == null)return null;
				
				if (ingredient.getItem().hasContainerItem(ingredient) || isBlacklisted(ingredient))iter.remove();
				else if (ingredient.getItemDamage() == OreDictionary.WILDCARD_VALUE)ingredient.setItemDamage(0);
			}
			
			if (!randRecipeIngredients.isEmpty())return randRecipeIngredients;
		}
		
		return null;
	}
	
	private StardustDecomposition(){}
}

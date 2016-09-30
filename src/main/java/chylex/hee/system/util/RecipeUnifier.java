package chylex.hee.system.util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import chylex.hee.system.collections.CollectionUtil;

/**
 * Converts the current IRecipe mess into a unified system.
 */
public final class RecipeUnifier{
	private static final Function<? super Object, ? extends ItemStack> mapToItemStack = obj -> {
		if (obj instanceof ItemStack)return (ItemStack)obj;
		else if (obj instanceof ArrayList && !((ArrayList)obj).isEmpty())return (ItemStack)((ArrayList)obj).get(0);
		else return null;
	};
	
	private static final Function<? super Object, ? extends ItemStack[]> mapToOreStack = obj -> {
		if (obj instanceof ItemStack)return new ItemStack[]{ (ItemStack)obj };
		else if (obj instanceof ArrayList)return ((ArrayList<ItemStack>)obj).toArray(new ItemStack[((ArrayList)obj).size()]);
		else return new ItemStack[0];
	};
	
	private boolean align, pad;
	
	/**
	 * Aligns all ingredients next to each other, removing all null elements and resizing the ingredient array.
	 * If combined with {@code pad()}, the array elements are first aligned to the beginning, and then padded.
	 */
	public RecipeUnifier align(){
		this.align = true;
		return this;
	}
	
	/**
	 * Pads the ingredient array with null to have 9 elements.
	 */
	public RecipeUnifier pad(){
		this.pad = true;
		return this;
	}
	
	/**
	 * Converts an IRecipe to a custom recipe object. If the IRecipe class cannot be handled, the ingredient array will be empty.
	 */
	public Recipe unify(IRecipe recipe){
		boolean isShaped = false;
		ItemStack[] ingredients = null;
		List<ItemStack[]> oreIngredients = null;
		
		if (recipe instanceof ShapedRecipes){
			ShapedRecipes shaped = (ShapedRecipes)recipe;
			isShaped = true;
			
			if (shaped.recipeWidth != 3){
				ingredients = new ItemStack[9];
				int origIndex = 0;
				
				for(int y = 0; y < 3; y++){
					for(int x = 0; x < 3; x++){
						if (x >= shaped.recipeWidth || y >= shaped.recipeHeight)ingredients[y*3+x] = null;
						else ingredients[y*3+x] = shaped.recipeItems[origIndex++];
					}
				}
			}
			else ingredients = shaped.recipeItems.clone();
		}
		else if (recipe instanceof ShapelessRecipes){
			ShapelessRecipes shapeless = (ShapelessRecipes)recipe;
			ingredients = ((List<ItemStack>)((ShapelessRecipes)recipe).recipeItems).toArray(new ItemStack[shapeless.recipeItems.size()]);
		}
		else if (recipe instanceof ShapedOreRecipe){
			ingredients = Arrays.stream(((ShapedOreRecipe)recipe).getInput()).map(mapToItemStack).toArray(ItemStack[]::new);
			oreIngredients = Arrays.stream(((ShapedOreRecipe)recipe).getInput()).map(mapToOreStack).collect(Collectors.toList());
			isShaped = true;
		}
		else if (recipe instanceof ShapelessOreRecipe){
			ingredients = ((ShapelessOreRecipe)recipe).getInput().stream().map(mapToItemStack).toArray(ItemStack[]::new);
			oreIngredients = ((ShapelessOreRecipe)recipe).getInput().stream().map(mapToOreStack).collect(Collectors.toList());
		}
		
		if (align && ingredients != null){
			ingredients = Arrays.stream(ingredients).filter(Objects::nonNull).toArray(ItemStack[]::new);
			if (oreIngredients != null)oreIngredients = oreIngredients.stream().filter(Objects::nonNull).collect(Collectors.toList());
		}
		
		if (pad && ingredients != null){
			ingredients = Arrays.copyOf(ingredients, 9);
			while(oreIngredients != null && oreIngredients.size() < 9)oreIngredients.add(new ItemStack[0]);
		}
		
		return new Recipe(recipe.getRecipeOutput(), ingredients == null ? new ItemStack[0] : ingredients, oreIngredients, isShaped);
	}
	
	public static final class Recipe{
		private final ItemStack output;
		private final ItemStack[] ingredients;
		private final List<ItemStack[]> oreIngredients;
		private final boolean isShaped;
		
		private Recipe(ItemStack output, ItemStack[] ingredients, List<ItemStack[]> oreIngredients, boolean isShaped){
			this.output = output;
			this.ingredients = ingredients;
			this.oreIngredients = oreIngredients;
			this.isShaped = isShaped;
		}
		
		/**
		 * Returns the result of the crafting recipe. This should work even for non-standard recipes, but there is a possibility of returning null.
		 */
		public ItemStack getOutput(){
			return output;
		}
		
		/**
		 * Returns whether the recipe is shaped.
		 * If the {@code align()} method was used in the RecipeUnifier, the crafting matrix structure will not be preserved.
		 */
		public boolean isShaped(){
			return isShaped;
		}
		
		/**
		 * Returns the ingredient array. Changes made to it are reflected in the internal object, but not in the recipe itself.
		 * The array size, and whether it contains nulls or not, depend on settings of the RecipeUnifier used to parse the recipe.
		 */
		public ItemStack[] getIngredientArray(){
			return ingredients;
		}
		
		/**
		 * Returns a List from the ingredient array. Changes made to the list are not reflected the original array.
		 */
		public List<ItemStack> getIngredientList(){
			return CollectionUtil.newList(ingredients);
		}
		
		/**
		 * Returns whether the recipe uses Ore Dictionary ingredients.
		 */
		public boolean isOreRecipe(){
			return oreIngredients != null;
		}
		
		/**
		 * If the recipe is an Ore Dictionary recipe, returns the saved list of ItemStack arrays.
		 * If not, it creates a new list from the ingredient array for every call, so use {@code isOreRecipe()} first.
		 * The returned list never contains null, missing/empty ingredients use an empty array instead.
		 */
		public List<ItemStack[]> getOreIngredientList(){
			return oreIngredients != null ? oreIngredients : Arrays.stream(ingredients).map(is -> is == null ? new ItemStack[0] : new ItemStack[]{ is }).collect(Collectors.toList());
		}
	}
}

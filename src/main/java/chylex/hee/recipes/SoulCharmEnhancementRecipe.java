package chylex.hee.recipes;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;
import chylex.hee.block.BlockList;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.enhancements.types.SoulCharmEnhancements;

public class SoulCharmEnhancementRecipe extends ShapedRecipes{
	private static final ItemStack[] createItemArray(SoulCharmEnhancements enhancement){
		ItemStack endPowder = new ItemStack(ItemList.end_powder), modifier = enhancement.itemSelector.getRepresentativeItem();
		
		return new ItemStack[]{
			endPowder, modifier, endPowder,
			modifier, new ItemStack(BlockList.soul_charm), modifier,
			endPowder, modifier, endPowder
		};
	}
	
	private final SoulCharmEnhancements enhancement;
	
	public SoulCharmEnhancementRecipe(SoulCharmEnhancements enhancement){
		super(3,3,createItemArray(enhancement),enhancement.setLevel(new ItemStack(BlockList.soul_charm),(byte)-1));
		this.enhancement = enhancement;
	}
	
	@Override
	public boolean matches(InventoryCrafting craftMatrix, World world){
		int size = craftMatrix.getSizeInventory();
		if (size < 9)return false;
		
		ItemStack is;
		
		for(int a = 0; a < size; a++){
			if ((is = craftMatrix.getStackInSlot(a)) == null)return false;
			
			if (a == 0 || a == 2 || a == 6 || a == 8){
				if (is.getItem() != ItemList.end_powder)return false;
			}
			else if (a == 1 || a == 3 || a == 5 || a == 7){
				if (!enhancement.itemSelector.isValid(is))return false;
			}
			else if (!enhancement.canIncreaseLevel(is))return false;
		}
		
		return true;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting craftMatrix){
		ItemStack charm = craftMatrix.getStackInSlot(4).copy();
		enhancement.increaseLevel(charm);		
		return charm;
	}

	@Override
	public int getRecipeSize(){
		return 11;
	}

	@Override
	public ItemStack getRecipeOutput(){
		return enhancement.setLevel(new ItemStack(BlockList.soul_charm),(byte)0);
	}
}

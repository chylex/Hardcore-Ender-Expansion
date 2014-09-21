package chylex.hee.mechanics.compendium.content.fragments;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import org.lwjgl.opengl.GL11;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.gui.helpers.GuiItemRenderHelper;
import chylex.hee.item.ItemList;
import chylex.hee.item.ItemSpecialEffects;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.system.logging.Log;
import com.google.common.base.Joiner;

public class KnowledgeFragmentCrafting extends KnowledgeFragment{
	public static final ItemStack lockedItem = new ItemStack(ItemList.special_effects,1,ItemSpecialEffects.questionMark);
	
	private ItemStack[] items;
	
	public KnowledgeFragmentCrafting(int globalID){
		super(globalID);
	}
	
	public KnowledgeFragmentCrafting setCustomRecipe(ItemStack output, ItemStack[] ingredients){
		items = new ItemStack[10];
		for(int a = 0; a < 9 && a < ingredients.length; a++)items[a] = ingredients[a];
		items[9] = output;
		return this;
	}
	
	public KnowledgeFragmentCrafting setRecipeFromRegistry(ItemStack outputToFind){
		List<IRecipe> list = CraftingManager.getInstance().getRecipeList();
		
		for(int a = list.size()-1; a >= 0; a--){
			IRecipe recipe = list.get(a);
			
			if (!ItemStack.areItemStacksEqual(outputToFind,recipe.getRecipeOutput()))continue;
			else if (recipe instanceof ShapedRecipes)return setCustomRecipe(recipe.getRecipeOutput(),((ShapedRecipes)recipe).recipeItems);
			else if (recipe instanceof ShapelessRecipes)return setCustomRecipe(recipe.getRecipeOutput(),((List<ItemStack>)((ShapelessRecipes)recipe).recipeItems).toArray(new ItemStack[recipe.getRecipeSize()]));
		}
		
		Log.warn("Could not find ItemStack $0 when registering recipe from registry.",outputToFind.toString());
		return this;
	}

	@Override
	public int getHeight(GuiEnderCompendium gui, boolean isUnlocked){
		return 58;
	}

	@Override
	public void render(GuiEnderCompendium gui, int x, int y, int mouseX, int mouseY, boolean isUnlocked){
		GL11.glColor4f(1F,1F,1F,1F);
		gui.mc.getTextureManager().bindTexture(GuiEnderCompendium.texFragments);
		gui.drawTexturedModalRect(x,y,0,0,88,58);
		
		if (items == null)return;
		
		for(int cycle = 0; cycle < (isUnlocked ? 2 : 1); cycle++){
			for(int a = 0, xx = x, yy = y, cnt = 0; a < 10; a++){
				ItemStack is = isUnlocked ? items[a] : lockedItem;
				
				if (is != null){
					if (cycle == 0)GuiItemRenderHelper.renderItemIntoGUI(gui.mc.getTextureManager(),is,xx+2,yy+2);
					else if (mouseX >= xx+1 && mouseX <= xx+18 && mouseY >= yy+1 && mouseY <= yy+18){
						GuiItemRenderHelper.drawTooltip(gui,gui.mc.fontRenderer,mouseX,mouseY,Joiner.on('\n').join(is.getTooltip(gui.mc.thePlayer,false)));
					} // TODO click to switch object
				}
	
				if (a == 8){
					xx = x+94;
					yy = y+19;
					continue;
				}
	
				xx += 19;
				
				if (++cnt >= 3){
					yy += 19;
					xx -= 19*3;
					cnt = 0;
				}
			}
		}
	}
}

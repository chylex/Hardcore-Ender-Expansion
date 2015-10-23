package chylex.hee.mechanics.compendium_old.content.fragments;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.gui.helpers.GuiItemRenderHelper;
import chylex.hee.init.ItemList;
import chylex.hee.item.ItemSpecialEffects;
import chylex.hee.mechanics.compendium_old.content.KnowledgeFragment;
import chylex.hee.mechanics.compendium_old.content.KnowledgeObject;
import chylex.hee.mechanics.compendium_old.objects.IKnowledgeObjectInstance;
import chylex.hee.mechanics.compendium_old.util.KnowledgeUtils;
import chylex.hee.system.logging.Log;
import com.google.common.base.Joiner;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class KnowledgeFragmentCrafting extends KnowledgeFragment{
	public static final ItemStack lockedItem = new ItemStack(ItemList.special_effects,1,ItemSpecialEffects.questionMark);
	
	private static final Map<KnowledgeFragmentCrafting,Pair<ItemStack,ItemStack[]>> tmpCraftingFragments = new HashMap<>();
	
	public static void verifyRecipes(){
		for(Entry<KnowledgeFragmentCrafting,Pair<ItemStack,ItemStack[]>> entry:tmpCraftingFragments.entrySet()){
			KnowledgeFragmentCrafting fragment = entry.getKey();
			IRecipe prevRecipe = fragment.referenceRecipe;
			fragment.setRecipeFromRegistry(entry.getValue().getLeft(),entry.getValue().getRight());
			
			if (fragment.referenceRecipe == null)fragment.status = 2;
			else if (fragment.referenceRecipe != prevRecipe)fragment.status = 1;
		}
		
		tmpCraftingFragments.clear();
	}
	
	private IRecipe referenceRecipe;
	private ItemStack[] items;
	private byte status; // 0 = ok, 1 = changed, 2 = removed
	
	public KnowledgeFragmentCrafting(int globalID){
		super(globalID);
	}
	
	private KnowledgeFragmentCrafting setRecipe(IRecipe recipe, ItemStack[] ingredients){
		this.referenceRecipe = recipe;
		items = new ItemStack[10];
		for(int a = 0; a < 9 && a < ingredients.length; a++)items[a] = ingredients[a];
		items[9] = recipe.getRecipeOutput();
		return this;
	}
	
	public KnowledgeFragmentCrafting setRecipeFromRegistry(ItemStack outputToFind){
		return setRecipeFromRegistry(outputToFind,null);
	}
	
	public KnowledgeFragmentCrafting setRecipeFromRegistry(ItemStack outputToFind, ItemStack[] ingredients){
		this.referenceRecipe = null;
		this.items = null;
		
		List<IRecipe> list = CraftingManager.getInstance().getRecipeList();
		tmpCraftingFragments.put(this,Pair.of(outputToFind,ingredients));
		
		for(int a = list.size()-1; a >= 0; a--){
			IRecipe recipe = list.get(a);
			
			if (!ItemStack.areItemStacksEqual(outputToFind,recipe.getRecipeOutput()))continue;
			else if (recipe instanceof ShapedRecipes){
				ItemStack[] recipeIngredients = ((ShapedRecipes)recipe).recipeItems;
				if (checkIngredients(recipeIngredients,ingredients))return setRecipe(recipe,recipeIngredients);
			}
			else if (recipe instanceof ShapelessRecipes){
				ItemStack[] recipeIngredients = ((List<ItemStack>)((ShapelessRecipes)recipe).recipeItems).toArray(new ItemStack[recipe.getRecipeSize()]);
				if (checkIngredients(recipeIngredients,ingredients))return setRecipe(recipe,recipeIngredients);
			}
		}
		
		Log.warn("Could not find ItemStack $0 when registering recipe from registry.",outputToFind.toString());
		return this;
	}
	
	private boolean checkIngredients(ItemStack[] recipe, ItemStack[] target){
		if (target == null)return true;
		if (recipe.length != target.length)return false;
		
		int matching = 0;
		
		for(int recipeIndex = 0; recipeIndex < recipe.length; recipeIndex++){
			for(int targetIndex = 0; targetIndex < target.length; targetIndex++){
				ItemStack recipeIS = recipe[recipeIndex], targetIS = target[targetIndex];
				
				if (recipeIS.getItem() == targetIS.getItem() && recipeIS.getItemDamage() == targetIS.getItemDamage()){
					++matching;
					break;
				}
			}
		}
		
		return matching == recipe.length;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getHeight(GuiEnderCompendium gui, boolean isUnlocked){
		return 58;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean onClick(GuiEnderCompendium gui, int x, int y, int mouseX, int mouseY, int buttonId, boolean isUnlocked){
		if (items == null || !isUnlocked || buttonId != 0)return false;
		
		for(int a = 0, xx = x, yy = y, cnt = 0; a < 10; a++){
			if (items[a] != null && mouseX >= xx+1 && mouseX <= xx+18 && mouseY >= yy+1 && mouseY <= yy+18){
				KnowledgeObject<? extends IKnowledgeObjectInstance<?>> obj = KnowledgeUtils.tryGetFromItemStack(items[a]);
				if (obj == null)return false;
				
				gui.showObject(obj);
				gui.moveToCurrentObject(true);
				return true;
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
		
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onRender(GuiEnderCompendium gui, int x, int y, int mouseX, int mouseY, boolean isUnlocked){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1F,1F,1F,1F);
		gui.mc.getTextureManager().bindTexture(GuiEnderCompendium.texFragments);
		gui.drawTexturedModalRect(x,y,0,0,88,58);
		
		if (status > 0){
			boolean unicode = gui.mc.fontRenderer.getUnicodeFlag();
			gui.mc.fontRenderer.drawString("?",x+107,y+8,255<<24);
			gui.mc.fontRenderer.setUnicodeFlag(unicode);
			
			if (mouseX >= x+106 && mouseY >= y+7 && mouseX <= x+112 && mouseY <= y+16){
				GuiItemRenderHelper.setupTooltip(mouseX,mouseY,I18n.format(status == 1 ? "compendium.crafting.changed" : "compendium.crafting.removed"));
			}
		}
		
		if (items == null)return;
		
		for(int a = 0, xx = x, yy = y, cnt = 0; a < 10; a++){
			ItemStack is = isUnlocked ? items[a] : lockedItem;
			
			if (is != null){
				GuiItemRenderHelper.renderItemIntoGUI(gui.mc.getTextureManager(),is,xx+2,yy+2);
				
				if (isUnlocked && mouseX >= xx+1 && mouseX <= xx+18 && mouseY >= yy+1 && mouseY <= yy+18){
					GuiItemRenderHelper.setupTooltip(mouseX,mouseY,Joiner.on('\n').join(KnowledgeUtils.getCompendiumTooltip(is,gui.mc.thePlayer)));
				}
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

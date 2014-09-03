package chylex.hee.mechanics.knowledge.fragment;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.gui.helpers.GuiItemRenderHelper;
import chylex.hee.item.ItemList;
import chylex.hee.item.ItemSpecialEffects;
import chylex.hee.system.logging.Log;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CraftingKnowledgeFragment extends KnowledgeFragment{
	@SideOnly(Side.CLIENT)
	private static ResourceLocation texCraftingTable;
	
	@SideOnly(Side.CLIENT)
	public static void initClient(){
		texCraftingTable = new ResourceLocation("hardcoreenderexpansion:textures/gui/fragment_crafting.png");
	}
	
	private static final ItemStack lockedItem = new ItemStack(ItemList.special_effects,1,ItemSpecialEffects.questionMark);
	
	private ItemStack[] items;
	
	public CraftingKnowledgeFragment(int id){
		super(id);
	}

	public CraftingKnowledgeFragment setShapelessRecipe(ShapelessRecipes recipe){
		items = new ItemStack[10];
		for(int a = 0; a < 9 && a < recipe.recipeItems.size(); a++)items[a] = (ItemStack)recipe.recipeItems.get(a);
		items[9] = recipe.getRecipeOutput();
		return this;
	}
	
	public CraftingKnowledgeFragment setShapedRecipe(ShapedRecipes recipe){
		items = new ItemStack[10];
		for(int a = 0; a < 9 && a < recipe.recipeItems.length; a++)items[a] = recipe.recipeItems[a];
		items[9] = recipe.getRecipeOutput();
		return this;
	}
	
	public CraftingKnowledgeFragment setCustomRecipe(ItemStack output, ItemStack...ingredients){
		items = new ItemStack[10];
		for(int a = 0; a < 9 && a < ingredients.length; a++)items[a] = ingredients[a];
		items[9] = output;
		return this;
	}
	
	public CraftingKnowledgeFragment setRecipeFromRegistry(ItemStack outputToFind){
		List<?> list = CraftingManager.getInstance().getRecipeList();
		
		for(int a = list.size()-1; a >= 0; a--){
			Object o = list.get(a);
			
			if (o instanceof ShapedRecipes){
				ShapedRecipes recipe = (ShapedRecipes)o;
				if (ItemStack.areItemStacksEqual(outputToFind,recipe.getRecipeOutput()))return setShapedRecipe(recipe);
			}
			else if (o instanceof ShapelessRecipes){
				ShapelessRecipes recipe = (ShapelessRecipes)o;
				if (ItemStack.areItemStacksEqual(outputToFind,recipe.getRecipeOutput()))return setShapelessRecipe(recipe);
			}
		}
		
		Log.warn("Could not find ItemStack $0 when registering recipe from registry.",outputToFind.toString());
		return this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getHeight(Minecraft mc, boolean isUnlocked){
		return 58;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(int x, int y, Minecraft mc, boolean isUnlocked){
		GL11.glColor4f(1F,1F,1F,1F);
		mc.getTextureManager().bindTexture(texCraftingTable);
		
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x,y+58,0,0,0.453125F);
		tessellator.addVertexWithUV(x+88,y+58,0,0.6875F,0.453125F);
		tessellator.addVertexWithUV(x+88,y,0,0.6875F,0);
		tessellator.addVertexWithUV(x,y,0,0,0);
		tessellator.draw();
		
		if (items == null)return;
		
		for(int cycle = 0; cycle < 2; cycle++){
			for(int a = 0,xx = x,yy = y,cnt = 0; a < 10; a++){
				ItemStack is = isUnlocked?items[a]:lockedItem;
				if (is != null){
					if (cycle == 0){
						GL11.glEnable(GL12.GL_RESCALE_NORMAL);
						RenderHelper.enableGUIStandardItemLighting();
						GuiItemRenderHelper.renderItemIntoGUI(mc.getTextureManager(),is,xx+2,yy+2);
						RenderHelper.disableStandardItemLighting();
						GL11.glDisable(GL12.GL_RESCALE_NORMAL);
					}
					else if (isUnlocked && GuiEnderCompendium.mouseX > xx+1 && GuiEnderCompendium.mouseX < xx+18 && GuiEnderCompendium.mouseY >= yy+1 && GuiEnderCompendium.mouseY <= yy+18){
						StringBuilder build = new StringBuilder();
						List<String> tooltip = is.getTooltip(mc.thePlayer,false);
						for(String line:tooltip)build.append(line).append("\n");
						GuiItemRenderHelper.drawTooltip((GuiEnderCompendium)mc.currentScreen,mc.fontRenderer,GuiEnderCompendium.mouseX,GuiEnderCompendium.mouseY,build.toString());
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
}

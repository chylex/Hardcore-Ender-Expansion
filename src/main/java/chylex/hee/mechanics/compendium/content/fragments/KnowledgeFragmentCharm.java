package chylex.hee.mechanics.compendium.content.fragments;
import java.util.Map.Entry;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.gui.helpers.GuiItemRenderHelper;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.charms.CharmRecipe;
import chylex.hee.mechanics.charms.RuneType;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.objects.IKnowledgeObjectInstance;
import chylex.hee.mechanics.compendium.util.KnowledgeUtils;
import com.google.common.base.Joiner;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class KnowledgeFragmentCharm extends KnowledgeFragment{
	private ItemStack[] runes = new ItemStack[5];
	private ItemStack charm;
	
	public KnowledgeFragmentCharm(int globalID){
		super(globalID);
	}
	
	public KnowledgeFragmentCharm setRecipe(CharmRecipe recipe){
		this.charm = new ItemStack(ItemList.charm,1,recipe.id);
		
		int a = 0;
		for(Entry<RuneType,Byte> entry:recipe.getRunes().entrySet()){
			runes[a++] = new ItemStack(ItemList.rune,entry.getValue(),entry.getKey().damage);
		}
		
		return this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getHeight(GuiEnderCompendium gui, boolean isUnlocked){
		return 20;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean onClick(GuiEnderCompendium gui, int x, int y, int mouseX, int mouseY, int buttonId, boolean isUnlocked){
		if (!isUnlocked)return false;
		
		for(int a = 0, xx = x, yy = y; a < 6; a++){
			ItemStack is = a == 5 ? charm : runes[a];
			
			if (is != null && mouseX >= xx+1 && mouseX <= xx+18 && mouseY >= yy+1 && mouseY <= yy+18){
				KnowledgeObject<? extends IKnowledgeObjectInstance<?>> obj = KnowledgeUtils.tryGetFromItemStack(is);
				if (obj == null)return false;
				
				gui.showObject(obj);
				gui.moveToCurrentObject(true);
				return true;
			}

			if (a == 4)xx = x+98;
			else xx += 19;
		}
		
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onRender(GuiEnderCompendium gui, int x, int y, int mouseX, int mouseY, boolean isUnlocked){
		GL11.glColor4f(1F,1F,1F,1F);
		gui.mc.getTextureManager().bindTexture(GuiEnderCompendium.texFragments);
		gui.drawTexturedModalRect(x,y,89,0,96,20);
		
		for(int a = 0, xx = x, yy = y; a < 6; a++){
			ItemStack is = isUnlocked ? (a == 5 ? charm : runes[a]) : KnowledgeFragmentCrafting.lockedItem;
			
			if (is != null){
				GuiItemRenderHelper.renderItemIntoGUI(gui.mc.getTextureManager(),is,xx+2,yy+2);
				
				if (isUnlocked && mouseX >= xx+1 && mouseX <= xx+18 && mouseY >= yy+1 && mouseY <= yy+18){
					GuiItemRenderHelper.setupTooltip(mouseX,mouseY,Joiner.on('\n').join(KnowledgeUtils.getCompendiumTooltip(is,gui.mc.thePlayer)));
				}
			}

			if (a == 4)xx = x+98;
			else xx += 19;
		}
	}
}

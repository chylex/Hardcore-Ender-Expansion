package chylex.hee.mechanics.compendium.content.fragments;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.gui.helpers.GuiItemRenderHelper;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.util.KnowledgeUtils;
import chylex.hee.system.abstractions.GL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class FragmentItemConversion extends KnowledgeFragment<FragmentItemConversion>{
	private ItemStack itemFrom, itemTo;
	
	public FragmentItemConversion(int globalID){
		super(globalID);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getHeight(GuiEnderCompendium gui, boolean isUnlocked){
		return 20;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean onClick(GuiEnderCompendium gui, int x, int y, int mouseX, int mouseY, int buttonId, boolean isUnlocked){
		if (isUnlocked && buttonId == 0 && mouseY >= y && mouseY <= y+17){
			KnowledgeObject<?> obj = null;
			
			if (mouseX >= x && mouseX <= x+17)obj = KnowledgeObject.fromObject(itemFrom);
			else if (mouseX >= x+44 && mouseX <= x+61)obj = KnowledgeObject.fromObject(itemTo);
			
			if (obj != null){
				gui.showObject(obj);
				gui.moveToCurrentObject(true);
				return true;
			}
		}
		
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onRender(GuiEnderCompendium gui, int x, int y, int mouseX, int mouseY, boolean isUnlocked){
		GL.color(1F,1F,1F,1F);
		gui.mc.getTextureManager().bindTexture(GuiEnderCompendium.texFragments);
		gui.drawTexturedModalRect(x+20,y,0,59,22,20);
		
		GuiItemRenderHelper.renderItemIntoGUI(gui.mc.getTextureManager(),isUnlocked ? itemFrom : FragmentCrafting.lockedItem,x+1,y+1);
		GuiItemRenderHelper.renderItemIntoGUI(gui.mc.getTextureManager(),isUnlocked ? itemTo : FragmentCrafting.lockedItem,x+45,y+1);
		
		RenderHelper.disableStandardItemLighting();
		
		if (isUnlocked && mouseY >= y && mouseY <= y+17){
			if (mouseX >= x && mouseX <= x+17){
				GuiItemRenderHelper.setupTooltip(mouseX,mouseY,KnowledgeUtils.getCompendiumTooltipClient(itemFrom));
			}
			else if (mouseX >= x+44 && mouseX <= x+61){
				GuiItemRenderHelper.setupTooltip(mouseX,mouseY,KnowledgeUtils.getCompendiumTooltipClient(itemTo));
			}
		}
	}
}

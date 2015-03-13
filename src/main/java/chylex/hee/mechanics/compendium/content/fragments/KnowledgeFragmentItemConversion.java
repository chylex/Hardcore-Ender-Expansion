package chylex.hee.mechanics.compendium.content.fragments;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.gui.helpers.GuiItemRenderHelper;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.objects.IKnowledgeObjectInstance;
import chylex.hee.mechanics.compendium.util.KnowledgeUtils;
import com.google.common.base.Joiner;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class KnowledgeFragmentItemConversion extends KnowledgeFragment{
	private ItemStack itemFrom, itemTo;
	
	public KnowledgeFragmentItemConversion(int globalID){
		super(globalID);
	}
	
	public KnowledgeFragmentItemConversion setItems(ItemStack itemFrom, ItemStack itemTo){
		this.itemFrom = itemFrom;
		this.itemTo = itemTo;
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
		if (isUnlocked && buttonId == 0 && mouseY >= y && mouseY <= y+17){
			KnowledgeObject<? extends IKnowledgeObjectInstance<?>> obj = null;
			
			if (mouseX >= x && mouseX <= x+17)obj = KnowledgeUtils.tryGetFromItemStack(itemFrom);
			else if (mouseX >= x+44 && mouseX <= x+61)obj = KnowledgeUtils.tryGetFromItemStack(itemTo);
			
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
		GL11.glColor4f(1F,1F,1F,1F);
		gui.mc.getTextureManager().bindTexture(GuiEnderCompendium.texFragments);
		gui.drawTexturedModalRect(x+20,y,0,59,22,20);
		
		GuiItemRenderHelper.renderItemIntoGUI(gui.mc.getTextureManager(),isUnlocked ? itemFrom : KnowledgeFragmentCrafting.lockedItem,x+1,y+1);
		GuiItemRenderHelper.renderItemIntoGUI(gui.mc.getTextureManager(),isUnlocked ? itemTo : KnowledgeFragmentCrafting.lockedItem,x+45,y+1);
		
		RenderHelper.disableStandardItemLighting();
		
		if (isUnlocked && mouseY >= y && mouseY <= y+17){
			if (mouseX >= x && mouseX <= x+17){
				GuiItemRenderHelper.setupTooltip(mouseX,mouseY,Joiner.on('\n').join(KnowledgeUtils.getCompendiumTooltip(itemFrom,gui.mc.thePlayer)));
			}
			else if (mouseX >= x+44 && mouseX <= x+61){
				GuiItemRenderHelper.setupTooltip(mouseX,mouseY,Joiner.on('\n').join(KnowledgeUtils.getCompendiumTooltip(itemTo,gui.mc.thePlayer)));
			}
		}
	}
}

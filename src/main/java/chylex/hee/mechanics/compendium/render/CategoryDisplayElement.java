package chylex.hee.mechanics.compendium.render;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.mechanics.compendium.content.KnowledgeCategory;

public class CategoryDisplayElement{
	public final KnowledgeCategory category;
	private final int y;
	
	public CategoryDisplayElement(KnowledgeCategory category, int y){
		this.category = category;
		this.y = y;
	}

	public void render(GuiScreen gui){
		GL11.glColor4f(1F,1F,1F,1F);
		
		RenderHelper.disableStandardItemLighting();
		gui.mc.getTextureManager().bindTexture(GuiEnderCompendium.texBack);
		gui.drawTexturedModalRect(GuiEnderCompendium.guiObjLeft,y,216,5,40,40);
		
		GL11.glPushMatrix();
		GL11.glScalef(2F,2F,1F);
		RenderHelper.enableGUIStandardItemLighting();
		GuiEnderCompendium.renderItem.renderItemIntoGUI(gui.mc.fontRenderer,gui.mc.getTextureManager(),category.getItemStack(),(GuiEnderCompendium.guiObjLeft+4)>>1,(y+4)>>1);
		GL11.glPopMatrix();
	}
	
	public boolean isMouseOver(int mouseX, int mouseY, int offsetY){
		int x = GuiEnderCompendium.guiObjLeft, y = this.y+offsetY;
		return mouseX >= x+2 && mouseY >= y && mouseX <= x+42 && mouseY <= y+40;
	}
}
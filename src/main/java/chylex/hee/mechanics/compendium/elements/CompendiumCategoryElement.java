package chylex.hee.mechanics.compendium.elements;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.mechanics.compendium.content.KnowledgeCategory;

public final class CompendiumCategoryElement{
	private static final int startX = 23, startY = 28;
	
	public final KnowledgeCategory category;
	private final int y;
	
	public CompendiumCategoryElement(KnowledgeCategory category, int y){
		this.category = category;
		this.y = y;
	}
	
	public void render(GuiScreen gui, boolean selected){
		GL11.glColor4f(1F,1F,1F,1F);
		RenderHelper.disableStandardItemLighting();
		gui.mc.getTextureManager().bindTexture(GuiEnderCompendium.texBack);
		gui.drawTexturedModalRect(startX,startY+y,selected ? 113 : 115,selected ? 0 : 23,23,22);
		
		GL11.glPushMatrix();
		RenderHelper.enableGUIStandardItemLighting();
		GuiEnderCompendium.renderItem.renderItemIntoGUI(gui.mc.fontRenderer,gui.mc.getTextureManager(),category.getDisplayItem(),startX+(selected ? 3 : 2),startY+y+3);
		GL11.glPopMatrix();
	}
	
	public boolean isMouseOver(int mouseX, int mouseY, boolean selected){
		return mouseX >= startX && mouseY >= startY+y && mouseX <= startX+(selected ? 22 : 20) && mouseY < startY+y+22;
	}
}

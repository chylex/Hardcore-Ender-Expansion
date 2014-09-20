package chylex.hee.mechanics.compendium.render;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.objects.IKnowledgeObjectInstance;
import chylex.hee.mechanics.compendium.player.PlayerCompendiumData;

public class ObjectDisplayElement{
	public final KnowledgeObject<IKnowledgeObjectInstance<?>> object;
	
	public ObjectDisplayElement(KnowledgeObject<IKnowledgeObjectInstance<?>> object){
		this.object = object;
	}
	
	public void render(GuiScreen gui, PlayerCompendiumData compendiumData, float offsetX, float offsetY, int screenCenter){
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(1F,1F,1F,1F);
		
		boolean isCenter = object.getY() == -1;
		int x = (int)(isCenter ? screenCenter-8 : object.getX()+offsetX);
		int y = (int)(isCenter ? 63 : 63+object.getY()+offsetY);
		
		RenderHelper.disableStandardItemLighting();
		gui.mc.getTextureManager().bindTexture(GuiEnderCompendium.texBack);
		gui.drawTexturedModalRect(x,y,113,compendiumData.hasDiscoveredObject(object) ? 0 : 23,22,22);
		RenderHelper.enableGUIStandardItemLighting();
		GuiEnderCompendium.renderItem.renderItemIntoGUI(gui.mc.fontRenderer,gui.mc.getTextureManager(),object.getItemStack(),x+3,y+3);
	}
	
	public boolean isMouseOver(int mouseX, int mouseY, int offsetX, int offsetY, int screenCenter){
		boolean isCenter = object.getY() == -1;
		int x = isCenter ? screenCenter-8 : object.getX()+offsetX;
		int y = isCenter ? 63 : 63+object.getY()+offsetY;
		return mouseX >= x-2 && mouseY >= y-1 && mouseX <= x+18 && mouseY <= y+18;
	}
}
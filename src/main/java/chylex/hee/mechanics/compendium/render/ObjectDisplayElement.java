package chylex.hee.mechanics.compendium.render;
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
	
	public void render(GuiScreen gui, PlayerCompendiumData compendiumData, float offsetX, float offsetY){
		RenderHelper.disableStandardItemLighting();
		gui.mc.getTextureManager().bindTexture(GuiEnderCompendium.texBack);
		gui.drawTexturedModalRect((int)(object.getX()+offsetX+29),(int)(object.getY()+offsetY+30),113,compendiumData.hasDiscoveredObject(object) ? 0 : 23,22,22);
		RenderHelper.enableGUIStandardItemLighting();
		GuiEnderCompendium.renderItem.renderItemIntoGUI(gui.mc.fontRenderer,gui.mc.getTextureManager(),object.getItemStack(),(int)(object.getX()+offsetX+32),(int)(object.getY()+offsetY+33));
	}
	
	public boolean isMouseOver(int mouseX, int mouseY, int offsetX, int offsetY){
		int x = object.getX()+offsetX+32, y = object.getY()+offsetY+32;
		return mouseX >= x-2 && mouseY >= y-1 && mouseX <= x+18 && mouseY <= y+18;
	}
}
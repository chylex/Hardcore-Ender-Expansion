package chylex.hee.mechanics.compendium.render;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.gui.helpers.AnimatedFloat;
import chylex.hee.gui.helpers.AnimatedFloat.Easing;
import chylex.hee.mechanics.compendium.content.type.KnowledgeCategory;
import chylex.hee.system.util.MathUtil;

public class CategoryDisplayElement{
	public final KnowledgeCategory category;
	private final AnimatedFloat alpha = new AnimatedFloat(Easing.LINEAR);
	private float prevAlpha;
	private byte alphaStartDelay;
	
	public CategoryDisplayElement(KnowledgeCategory category, int alphaStartDelay){
		this.category = category;
		this.alphaStartDelay = (byte)alphaStartDelay;
	}
	
	public void update(){
		if (alphaStartDelay > 0 && --alphaStartDelay == 0)alpha.startAnimation(0F,1F,0.4F);
		prevAlpha = alpha.value();
		alpha.update(0.05F);
	}

	public void render(GuiScreen gui, float offsetX, float offsetY, float partialTickTime){
		GL11.glColor4f(1F,1F,1F,prevAlpha+(alpha.value()-prevAlpha)*partialTickTime);
		
		RenderHelper.disableStandardItemLighting();
		gui.mc.getTextureManager().bindTexture(GuiEnderCompendium.texBack);
		gui.drawTexturedModalRect((int)(category.getX()+offsetX+2),(int)(category.getY()+offsetY),216,5,40,40);
		
		GL11.glPushMatrix();
		GL11.glScalef(0.5F,0.5F,1F);
		gui.drawTexturedModalRect((int)(2*(category.getX()+offsetX+8)),(int)(2*(category.getY()+offsetY+4)),(category.id%4)*56,194-62*(category.id>>2),56,62);
		GL11.glPopMatrix();
		
		GL11.glColor4f(1F,1F,1F,1F);
	}
	
	public boolean isMouseOver(int mouseX, int mouseY, int offsetX, int offsetY){
		int x = category.getX()+offsetX, y = category.getY()+offsetY;
		return mouseX >= x+2 && mouseY >= y && mouseX <= x+42 && mouseY <= y+40 && MathUtil.floatEquals(alpha.value(),1F);
	}
}
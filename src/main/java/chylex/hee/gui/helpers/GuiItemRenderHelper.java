package chylex.hee.gui.helpers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiItemRenderHelper{
	private static final RenderBlocks renderBlocks = new RenderBlocks();
	
	private static int tooltipX, tooltipY;
	private static String tooltipString;
	
	public static void setupTooltip(int x, int y, String tooltip){
		tooltipX = x;
		tooltipY = y;
		tooltipString = tooltip;
	}
	
	public static void drawTooltip(ITooltipRenderer gui, FontRenderer fontRendererObj){
		if (tooltipString == null)return;
		String[] strings = tooltipString.split("\n");
		
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		int maxWidth = 0, xx = tooltipX+12, yy = tooltipY-12, height = strings.length > 1 ? 10+(strings.length-1)*10 : 8;
		
		for(String s:strings)maxWidth = Math.max(maxWidth,fontRendererObj.getStringWidth(s));

		GuiScreen guiScreen = (GuiScreen)gui;
		if (xx+maxWidth > guiScreen.width)xx -= 28+maxWidth;
		if (yy+height+6 > guiScreen.height)yy -= guiScreen.height-height-6;

		Minecraft.getMinecraft().getRenderItem().zLevel = 300F;
		gui.setZLevel(300F);
		
		int grad1 = -267386864,grad2 = 1347420415,grad3 = (grad2&16711422)>>1|grad2&-16777216;
		
		gui.callDrawGradientRect(xx-3,yy-4,xx+maxWidth+3,yy-3,grad1,grad1);
		gui.callDrawGradientRect(xx-3,yy+height+3,xx+maxWidth+3,yy+height+4,grad1,grad1);
		gui.callDrawGradientRect(xx-3,yy-3,xx+maxWidth+3,yy+height+3,grad1,grad1);
		gui.callDrawGradientRect(xx-4,yy-3,xx-3,yy+height+3,grad1,grad1);
		gui.callDrawGradientRect(xx+maxWidth+3,yy-3,xx+maxWidth+4,yy+height+3,grad1,grad1);
		
		gui.callDrawGradientRect(xx-3,yy-2,xx-2,yy+height+2,grad2,grad3);
		gui.callDrawGradientRect(xx+maxWidth+2,yy-2,xx+maxWidth+3,yy+height+2,grad2,grad3);
		gui.callDrawGradientRect(xx-3,yy-3,xx+maxWidth+3,yy-2,grad2,grad2);
		gui.callDrawGradientRect(xx-3,yy+height+2,xx+maxWidth+3,yy+height+3,grad3,grad3);

		for(int a = 0; a < strings.length; ++a){
			fontRendererObj.drawStringWithShadow(strings[a],xx,yy,-1);
			yy += a == 0 ? 12 : 10;
		}

		Minecraft.getMinecraft().getRenderItem().zLevel = 0F;
		gui.setZLevel(0F);
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		
		tooltipString = null;
	}
	
	public static interface ITooltipRenderer{
		void setZLevel(float newZLevel);
		void callDrawGradientRect(int x1, int y1, int x2, int y2, int color1, int color2);
	}
}

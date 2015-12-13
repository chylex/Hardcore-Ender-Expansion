package chylex.hee.gui.helpers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class GuiHelper{
	public static final int keyArrowLeft = 203,
							keyArrowRight = 205,
							keyArrowUp = 200,
							keyArrowDown = 208,
							keyPageUp = 201,
							keyPageDown = 209,
							keyHome = 199,
							keyEnd = 207,
							keyEscape = 1,
							keyF1 = 59;
	
	public static final ResourceLocation texUtils = new ResourceLocation("hardcoreenderexpansion:textures/gui/utilities.png");
	
	public static void renderLine(int x1, int y1, int x2, int y2, int color){
		if (x1 == x2 || y1 == y2){
			Gui.drawRect(x1-1,y1-1,x2+1,y2+1,color);
		}
		else{
			Vec vec = Vec.xz(x2-x1,y2-y1);
			int len = MathUtil.ceil(vec.length());
			vec = vec.normalized();
			
			float x = x1+0.5F, y = y1+0.5F;
			
			for(int a = 0; a < len; a++){
				Gui.drawRect((int)x-1,(int)y-1,(int)x+1,(int)y+1,color);
				x += vec.x;
				y += vec.z;
			}
		}
	}
	
	public static void renderUnicodeString(String str, int x, int y, int maxWidth, int color){
		Minecraft mc = Minecraft.getMinecraft();
		FontRenderer fontRenderer = mc.fontRenderer;
		
		boolean origFont = fontRenderer.getUnicodeFlag();
		fontRenderer.setUnicodeFlag(true);
		
		if ((mc.gameSettings.guiScale&1) == 1){
			float dist = 0.08F;
			
			for(int cycle = 0; cycle < 2; cycle++){
				GL11.glTranslatef(-dist,0F,0F);
				fontRenderer.drawSplitString(str,x,y,maxWidth,color);
				GL11.glTranslatef(dist,-dist,0F);
				fontRenderer.drawSplitString(str,x,y,maxWidth,color);
				GL11.glTranslatef(dist,0F,0F);
				fontRenderer.drawSplitString(str,x,y,maxWidth,color);
				GL11.glTranslatef(-dist,dist,0F);
				
				dist = -dist;
			}
		}
		else fontRenderer.drawSplitString(str,x,y,maxWidth,color);
		
		fontRenderer.setUnicodeFlag(origFont);
	}

	public static void renderGradient(int x, int y, int w, int h, int color1, int color2, float zLevel){
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		OpenGlHelper.glBlendFunc(770,771,1,0);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA_F((color1>>16&255)/255F,(color1>>8&255)/255F,(color1&255)/255F,(color1>>24&255)/255F);
		tessellator.addVertex(w,y,zLevel);
		tessellator.addVertex(x,y,zLevel);
		tessellator.setColorRGBA_F((color2>>16&255)/255F,(color2>>8&255)/255F,(color2&255)/255F,(color2>>24&255)/255F);
		tessellator.addVertex(x,h,zLevel);
		tessellator.addVertex(w,h,zLevel);
		tessellator.draw();
		GL11.glShadeModel(GL11.GL_FLAT);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	private GuiHelper(){}
}

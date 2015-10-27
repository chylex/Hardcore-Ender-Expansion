package chylex.hee.gui.helpers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
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
							keyEscape = 1;
	
	public static void renderLine(int x1, int y1, int x2, int y2, int color){
		if (x1 == x2 || y1 == y2){
			Gui.drawRect(x1-1,y1-1,x2+1,y2+1,color);
		}
		else{ // TODO um... improve?
			Vec vec = Vec.xz(x2-x1,y2-y1);
			int len = MathUtil.ceil(vec.length());
			vec = vec.normalized();
			
			float x = x1, y = y1;
			
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
	
	private GuiHelper(){}
}

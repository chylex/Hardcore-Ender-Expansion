package chylex.hee.gui;
import net.minecraft.client.Minecraft;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.client.config.GuiUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonSmall extends GuiButtonExt{
	private final boolean hasShadow;
	
	public GuiButtonSmall(int id, int x, int y, int width, int height, String displayString, boolean hasShadow){
		super(id,x,y,width,height,displayString);
		this.hasShadow = hasShadow;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY){
		if (visible){
			field_146123_n = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition+width && mouseY<yPosition+height;
			GuiUtils.drawContinuousTexturedBox(buttonTextures,xPosition,yPosition,0,46+getHoverState(field_146123_n)*20,width,height,200,20,2,2,2,2,zLevel);
			mouseDragged(mc,mouseX,mouseY);
			
			int color = packedFGColour != 0 ? packedFGColour : !enabled ? 10526880 : field_146123_n ? 16777120 : 14737632;
			mc.fontRenderer.drawString(displayString,xPosition+width/2-mc.fontRenderer.getStringWidth(displayString)/2,yPosition+(height-7)/2,color,hasShadow);
		}
	}
}

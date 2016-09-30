package chylex.hee.gui;
import net.minecraft.client.Minecraft;
import chylex.hee.gui.helpers.GuiHelper;
import chylex.hee.system.abstractions.GL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonArrow extends GuiButtonSmall{
	public GuiButtonArrow(int id, int x, int y){
		super(id, x, y, 11, 11, "", false);
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY){
		super.drawButton(mc, mouseX, mouseY);
		
		if (visible){
			if (!field_146123_n)GL.color(1F, 1F, 1F, 1F);
			mc.getTextureManager().bindTexture(GuiHelper.texUtils);
			drawTexturedModalRect(xPosition, yPosition, 0, 0, 11, 11);
		}
	}
}

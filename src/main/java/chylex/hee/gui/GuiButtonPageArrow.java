package chylex.hee.gui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import chylex.hee.mechanics.compendium.handlers.CompendiumPageHandler;
import chylex.hee.system.abstractions.GL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonPageArrow extends GuiButton{
	private final boolean isRightArrow;
	public boolean forcedHover;

	public GuiButtonPageArrow(int id, int x, int y, boolean isRightArrow){
		super(id,x,y,23,13,"");
		this.isRightArrow = isRightArrow;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY){
		if (visible){
			GL.color(1F,1F,1F,1F);
			mc.getTextureManager().bindTexture(CompendiumPageHandler.texPage);
			drawTexturedModalRect(xPosition,yPosition,(mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition+width && mouseY < yPosition+height) || forcedHover ? 23 : 0,231+(!isRightArrow ? 13 : 0),23,13);
		}
	}
}

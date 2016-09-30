package chylex.hee.gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import chylex.hee.system.abstractions.GL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCharmPouch extends GuiContainer{
	private static final ResourceLocation guiResource = new ResourceLocation("hardcoreenderexpansion:textures/gui/charm_pouch.png");
	
	public GuiCharmPouch(EntityPlayer player){
		super(new ContainerCharmPouch(player));
		ySize = 181;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		String s = I18n.format("container.charmPouch");
		fontRendererObj.drawString(s, (xSize>>2)-(fontRendererObj.getStringWidth(s)>>1)+1, 6, 0x404040);
		
		s = I18n.format("container.runeCrafting");
		fontRendererObj.drawString(s, 3*(xSize>>2)-(fontRendererObj.getStringWidth(s)>>1)-1, 6, 0x404040);
		
		fontRendererObj.drawString(I18n.format("container.inventory"), 8, ySize-96+2, 0x404040);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int mouseX, int mouseY){
		GL.color(1F, 1F, 1F, 1F);
		mc.getTextureManager().bindTexture(guiResource);
		
		int guiX = (width-xSize)>>1, guiY = (height-ySize)>>1;
		drawTexturedModalRect(guiX, guiY, 0, 0, xSize, ySize);
	}
}

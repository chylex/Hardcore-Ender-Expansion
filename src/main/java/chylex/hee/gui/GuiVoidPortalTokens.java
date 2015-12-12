package chylex.hee.gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiVoidPortalTokens extends GuiContainer{
	private static final ResourceLocation texture = new ResourceLocation("hardcoreenderexpansion:textures/gui/void_portal_tokens.png");

	private final EntityPlayer player;
	
	public GuiVoidPortalTokens(EntityPlayer player){
		super(new ContainerVoidPortalTokens(player));
		this.player = player;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		fontRendererObj.drawString(I18n.format("container.voidPortal"),8,6,(24<<16)|(24<<8)|24);
		fontRendererObj.drawString(I18n.format(player.inventory.getInventoryName()),8,ySize-85,(64<<16)|(64<<8)|64);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int mouseX, int mouseY){
		GL11.glColor4f(1F,1F,1F,1F);
		mc.getTextureManager().bindTexture(texture);
		drawTexturedModalRect((width-xSize)/2,(height-ySize)/2,0,0,xSize,174);
	}
}

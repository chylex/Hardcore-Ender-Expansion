package chylex.hee.gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.tileentity.TileEntityEnergyExtractionTable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEnergyExtractionTable extends GuiContainer{
	private static final ResourceLocation guiResource = new ResourceLocation("hardcoreenderexpansion:textures/gui/energy_extraction_table.png");
	
	private TileEntityEnergyExtractionTable energyExtractionTable;
	
	public GuiEnergyExtractionTable(InventoryPlayer inv, TileEntityEnergyExtractionTable tile){
		super(new ContainerEnergyExtractionTable(inv,tile));
		this.energyExtractionTable = tile;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		String s = energyExtractionTable.hasCustomInventoryName() ? energyExtractionTable.getInventoryName() : I18n.format(energyExtractionTable.getInventoryName());
		fontRendererObj.drawString(s,xSize / 2 - fontRendererObj.getStringWidth(s) / 2,6,0x404040);
		fontRendererObj.drawString(I18n.format("container.inventory"),8,ySize - 96 + 2,0x404040);
		
		int stardustReq = energyExtractionTable.getRequiredStardust();

		itemRender.zLevel = zLevel = 300F;
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		fontRendererObj.drawStringWithShadow(
			(energyExtractionTable.getHoldingStardust() < stardustReq ? EnumChatFormatting.YELLOW : EnumChatFormatting.WHITE)+String.valueOf(stardustReq),
		40,ySize-113,0x404040);
		
		itemRender.zLevel = zLevel = 0F;
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		int guiX = (width-xSize)>>1, guiY = (height-ySize)>>1;
		if (mouseX >= guiX+95 && mouseY >= guiY+26 && mouseX <= guiX+95+17 && mouseY <= guiY+26+50)drawTooltip(mouseX-guiX,mouseY-guiY,DragonUtil.formatTwoPlaces.format(energyExtractionTable.getContainedEnergy()*0.01F)+" energy contained");
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int mouseX, int mouseY){
		GL11.glColor4f(1F,1F,1F,1F);
		mc.getTextureManager().bindTexture(guiResource);
		
		int guiX = (width-xSize)>>1, guiY = (height-ySize)>>1; 
		drawTexturedModalRect(guiX,guiY,0,0,xSize,ySize);
		
		int bar = energyExtractionTable.getScaledProgressTime(24);
		if (bar > -1)drawTexturedModalRect(guiX+63,guiY+34,176,0,bar+1,16);
		
		int energy = energyExtractionTable.getScaledContainedEnergy(49);
		if (energy > -1)drawTexturedModalRect(guiX+96,guiY+27+49-energy,176,17+9-energy%10,16,energy);
	}
	
	public void drawTooltip(int x, int y, String tooltip){
		if (tooltip == null)return;
		String[] strings = tooltip.split("\n");
		
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		int maxWidth = 0,xx = x+12, yy = y-12, height = strings.length > 1 ? 10+(strings.length-1)*10 : 8;
		
		for(String s:strings)maxWidth = Math.max(maxWidth,fontRendererObj.getStringWidth(s));

		if (xx+maxWidth > width)xx -= 28+maxWidth;
		if (yy+height+6 > this.height)yy -= this.height-height-6;
		
		itemRender.zLevel = zLevel = 300F;
		
		int grad1 = -267386864,grad2 = 1347420415,grad3 = (grad2&16711422)>>1|grad2&-16777216;
		
		drawGradientRect(xx-3,yy-4,xx+maxWidth+3,yy-3,grad1,grad1);
		drawGradientRect(xx-3,yy+height+3,xx+maxWidth+3,yy+height+4,grad1,grad1);
		drawGradientRect(xx-3,yy-3,xx+maxWidth+3,yy+height+3,grad1,grad1);
		drawGradientRect(xx-4,yy-3,xx-3,yy+height+3,grad1,grad1);
		drawGradientRect(xx+maxWidth+3,yy-3,xx+maxWidth+4,yy+height+3,grad1,grad1);
		
		drawGradientRect(xx-3,yy-2,xx-2,yy+height+2,grad2,grad3);
		drawGradientRect(xx+maxWidth+2,yy-2,xx+maxWidth+3,yy+height+2,grad2,grad3);
		drawGradientRect(xx-3,yy-3,xx+maxWidth+3,yy-2,grad2,grad2);
		drawGradientRect(xx-3,yy+height+2,xx+maxWidth+3,yy+height+3,grad3,grad3);

		for(int a = 0; a < strings.length; ++a){
			fontRendererObj.drawStringWithShadow(strings[a],xx,yy,-1);
			yy += a == 0?12:10;
		}
		
		itemRender.zLevel = zLevel = 0F;
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	}
}

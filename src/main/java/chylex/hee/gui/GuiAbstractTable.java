package chylex.hee.gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import chylex.hee.tileentity.TileEntityAbstractTable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class GuiAbstractTable extends GuiContainer{
	private TileEntityAbstractTable table;
	
	public GuiAbstractTable(Container container, TileEntityAbstractTable table){
		super(container);
		this.table = table;
	}
	
	protected abstract int getEnergyBarX();
	protected abstract int getEnergyBarY();
	protected abstract int getEnergyIconX();
	protected abstract int getEnergyIconY();
	protected abstract int getStardustTextX();
	protected abstract int getStardustTextY();
	protected abstract ResourceLocation getBackgroundTexture();
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		String s = table.hasCustomInventoryName() ? table.getInventoryName() : I18n.format(table.getInventoryName());
		fontRendererObj.drawString(s,xSize/2-fontRendererObj.getStringWidth(s)/2,6,0x404040);
		fontRendererObj.drawString(I18n.format("container.inventory"),8,ySize-94,0x404040);
		
		int stardustReq = table.getRequiredStardust();
		
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		itemRender.zLevel = zLevel = 300F;
		fontRendererObj.drawStringWithShadow((table.getHoldingStardust() < stardustReq ? EnumChatFormatting.YELLOW : EnumChatFormatting.WHITE)+String.valueOf(stardustReq),getStardustTextX(),getStardustTextY(),0x404040);
		itemRender.zLevel = zLevel = 0F;
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int mouseX, int mouseY){
		GL11.glColor4f(1F,1F,1F,1F);
		mc.getTextureManager().bindTexture(getBackgroundTexture());
		
		int guiX = (width-xSize)>>1, guiY = (height-ySize)>>1; 
		drawTexturedModalRect(guiX,guiY,0,0,xSize,ySize);
		
		int bar = table.getScaledProgressTime(24);
		if (bar > -1)drawTexturedModalRect(guiX+getEnergyBarX(),guiY+getEnergyBarY(),176,0,bar+1,16);
		
		if (table.hasInsufficientEnergy())drawTexturedModalRect(guiX+getEnergyIconX(),guiY+getEnergyIconY(),176,18,10,11);
	}
}

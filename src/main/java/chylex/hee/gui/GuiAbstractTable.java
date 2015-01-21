package chylex.hee.gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import chylex.hee.tileentity.TileEntityAbstractTable;

@SideOnly(Side.CLIENT)
public abstract class GuiAbstractTable extends GuiContainer{
	private TileEntityAbstractTable table;
	private short progressBarX = -1, progressBarY = -1;
	private short energyIconX = -1, energyIconY = -1;
	private short energyStorageX = -1, energyStorageY = -1;
	private short stardustTextX = -1, stardustTextY = -1;
	
	public GuiAbstractTable(Container container, TileEntityAbstractTable table){
		super(container);
		this.table = table;
	}
	
	protected final void setupProgressBar(int x, int y){
		this.progressBarX = (short)x;
		this.progressBarY = (short)y;
	}
	
	protected final void setupEnergyIcon(int x, int y){
		this.energyIconX = (short)x;
		this.energyIconY = (short)y;
	}
	
	protected final void setupEnergyStorage(int x, int y){
		this.energyStorageX = (short)x;
		this.energyStorageY = (short)y;
	}
	
	protected final void setupStardustText(int x, int y){
		this.stardustTextX = (short)x;
		this.stardustTextY = (short)y;
	}
	
	protected abstract ResourceLocation getBackgroundTexture();
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		String name = table.getName();
		fontRendererObj.drawString(name,(xSize>>1)-(fontRendererObj.getStringWidth(name)>>1),6,0x404040);
		fontRendererObj.drawString(I18n.format("container.inventory"),8,ySize-94,0x404040);
		
		if (stardustTextX != -1){
			int stardustReq = table.getRequiredStardust();
			
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			itemRender.zLevel = zLevel = 300F;
			fontRendererObj.drawStringWithShadow((table.getHoldingStardust() < stardustReq ? EnumChatFormatting.YELLOW : EnumChatFormatting.WHITE)+String.valueOf(stardustReq),stardustTextX,stardustTextY,0x404040);
			itemRender.zLevel = zLevel = 0F;
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int mouseX, int mouseY){
		GL11.glColor4f(1F,1F,1F,1F);
		mc.getTextureManager().bindTexture(getBackgroundTexture());
		
		int guiX = (width-xSize)>>1, guiY = (height-ySize)>>1; 
		drawTexturedModalRect(guiX,guiY,0,0,xSize,ySize);
		
		if (progressBarX != -1){
			int bar = table.getScaledTimeClient(24);
			if (bar > -1)drawTexturedModalRect(guiX+progressBarX,guiY+progressBarY,176,0,bar+1,16);
		}
		
		if (energyIconX != -1 && table.hasInsufficientEnergy())drawTexturedModalRect(guiX+energyIconX,guiY+energyIconY,176,18,10,11);
		
		if (energyStorageX != -1){
			int energy = table.getScaledStoredEnergyClient(49)-1;
			if (energy > -1)drawTexturedModalRect(guiX+energyStorageX,guiY+energyStorageY+49-energy,176,39-energy%10,16,energy);
		}
	}
}

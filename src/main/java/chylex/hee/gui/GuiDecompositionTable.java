package chylex.hee.gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import chylex.hee.tileentity.TileEntityDecompositionTable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDecompositionTable extends GuiContainer{
	private static final ResourceLocation guiResource = new ResourceLocation("hardcoreenderexpansion:textures/gui/decomposition_table.png");
	
	private TileEntityDecompositionTable decompositionTable;
	
	public GuiDecompositionTable(InventoryPlayer inv, TileEntityDecompositionTable tile){
		super(new ContainerDecompositionTable(inv,tile));
		this.decompositionTable = tile;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		String s = decompositionTable.hasCustomInventoryName() ? decompositionTable.getInventoryName() : I18n.format(decompositionTable.getInventoryName());
		fontRendererObj.drawString(s,xSize / 2 - fontRendererObj.getStringWidth(s) / 2,6,0x404040);
		fontRendererObj.drawString(I18n.format("container.inventory"),8,ySize - 96 + 2,0x404040);
		
		int stardustReq = decompositionTable.getRequiredStardust();
		
		itemRender.zLevel = zLevel = 300F;
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		fontRendererObj.drawStringWithShadow(
			(decompositionTable.getHoldingStardust() < stardustReq ? EnumChatFormatting.YELLOW : EnumChatFormatting.WHITE)+String.valueOf(stardustReq),
		34,ySize-113,0x404040);
		
		itemRender.zLevel = zLevel = 0F;
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int mouseX, int mouseY){
		GL11.glColor4f(1F,1F,1F,1F);
		mc.getTextureManager().bindTexture(guiResource);
		
		int guiX = (width-xSize)>>1, guiY = (height-ySize)>>1; 
		drawTexturedModalRect(guiX,guiY,0,0,xSize,ySize);
		
		int bar = decompositionTable.getScaledProgressTime(24);
		if (bar > -1)drawTexturedModalRect(guiX+57,guiY+34,176,0,bar+1,16);
	}
}

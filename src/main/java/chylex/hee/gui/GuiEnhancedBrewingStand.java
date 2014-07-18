package chylex.hee.gui;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import chylex.hee.tileentity.TileEntityEnhancedBrewingStand;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEnhancedBrewingStand extends GuiBrewingStand{
	private static ResourceLocation guiResource = new ResourceLocation("hardcoreenderexpansion:textures/gui/enhanced_brewing_stand.png");
	
	private TileEntityEnhancedBrewingStand brewingStand;
	 
	public GuiEnhancedBrewingStand(InventoryPlayer inv, TileEntityEnhancedBrewingStand tile){
		super(inv,tile);
		inventorySlots = new ContainerEnhancedBrewingStand(inv,tile);
		ySize = 191;
		brewingStand = tile;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int x, int y){
		GL11.glColor4f(1F,1F,1F,1F);
		mc.getTextureManager().bindTexture(guiResource);
		int guiX = 1+(width-xSize)/2;
		int guiY = (height-ySize)/2;
		drawTexturedModalRect(guiX,guiY,0,0,xSize,ySize);
		int brewTime = brewingStand.getBrewTime();

		if (brewTime > 0){
			int texPos = (int)(28F*(1F-(float)brewTime/brewingStand.getStartBrewTime()));
			if (texPos > 0)drawTexturedModalRect(guiX+98,guiY+16,176,0,9,texPos);

			switch(brewTime/2%7){
				case 0: texPos = 29; break;
				case 1: texPos = 24; break;
				case 2: texPos = 20; break;
				case 3: texPos = 16; break;
				case 4: texPos = 11; break;
				case 5: texPos = 6; break;
				case 6: texPos = 0;
			}

			if (texPos > 0)drawTexturedModalRect(guiX+66,guiY+14+29-texPos,185,29-texPos,12,texPos);
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y){
		super.drawGuiContainerForegroundLayer(x,y);
		
		int powderReq = brewingStand.getRequiredPowder();
		
		fontRendererObj.drawStringWithShadow(
			(brewingStand.getHoldingPowder() < powderReq ? EnumChatFormatting.YELLOW : EnumChatFormatting.WHITE)+String.valueOf(powderReq),
		81,ySize-114,4210752);
	}
}

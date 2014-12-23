package chylex.hee.gui;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import chylex.hee.gui.helpers.GuiItemRenderHelper;
import chylex.hee.gui.helpers.GuiItemRenderHelper.ITooltipRenderer;
import chylex.hee.mechanics.misc.PlayerTransportBeacons.LocationXZ;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTransportBeacon extends GuiScreen implements ITooltipRenderer{
	private static final ResourceLocation guiResource = new ResourceLocation("hardcoreenderexpansion:textures/gui/transport_beacon.png");
	private static final int size = 176;
	
	private final int centerX, centerZ;
	private int selectedX, selectedZ;
	private byte status;
	private Set<LocationXZ> locs = new HashSet<>();
	
	private GuiButton buttonTravel;
	
	public GuiTransportBeacon(int centerX, int centerZ){
		this.centerX = centerX;
		this.centerZ = centerZ;
	}
	
	public void loadOffsets(Set<LocationXZ> offsets){
		locs.clear();
		for(LocationXZ offset:offsets)locs.add(new LocationXZ(centerX+offset.x,centerZ+offset.z));
	}
	
	public void updateStatus(boolean hasEnergy, boolean hasNotBeenTampered){
		status = 0;
		if (hasEnergy)status |= 0b1;
		if (hasNotBeenTampered)status |= 0b10;
		buttonTravel.enabled = (status&0b11) != 0;
	}
	
	@Override
	public void initGui(){
		buttonList.add(buttonTravel = new GuiButton(1,(width>>1)-82,(height>>1)+61,80,20,I18n.format("container.button.travel")));
		buttonList.add(new GuiButton(2,(width>>1)+2,(height>>1)+61,80,20,I18n.format("gui.cancel")));
		
		buttonTravel.enabled = false;
	}
	
	@Override
	protected void actionPerformed(GuiButton button){
		if (button.id == 1){
			
		}
		else if (button.id == 2)mc.displayGuiScreen(null);
	}
	
	@Override
	public void drawScreen(int x, int y, float renderPartialTicks){
		drawDefaultBackground();
		
		GL11.glColor4f(1F,1F,1F,1F);
		mc.getTextureManager().bindTexture(guiResource);
		
		int guiX = (width-size)>>1, guiY = (height-size)>>1;
		drawTexturedModalRect(guiX,guiY,0,0,size,size);
		
		boolean changedColor = false;
		
		for(LocationXZ loc:locs){
			if (loc.x == centerX && loc.z == centerZ){
				GL11.glColor4f(0F,0.9F,0F,1F);
				changedColor = true;
			}
			else if (loc.x == selectedX && loc.z == selectedZ){
				GL11.glColor4f(0.9F,0.6F,0F,1F);
				changedColor = true;
			}
			
			drawTexturedModalRect((width>>1)-1+((loc.x-centerX)>>3),(height>>1)-9+((loc.z-centerZ)>>3),size,0,2,2);
			if (changedColor)GL11.glColor4f(1F,1F,1F,1F);
		}
		
		super.drawScreen(x,y,renderPartialTicks);
		
		if (buttonTravel.getHoverState(true) == 2){
			if ((status&0b10) == 0){
				GuiItemRenderHelper.setupTooltip(x,y,"Tampering detected");
				GuiItemRenderHelper.drawTooltip(this,fontRendererObj);
			}
			else if ((status&0b1) == 0){
				GuiItemRenderHelper.setupTooltip(x,y,"Out of Energy");
				GuiItemRenderHelper.drawTooltip(this,fontRendererObj);
			}
		}
		
		String s = I18n.format("container.transportBeacon");
		fontRendererObj.drawString(s,(width>>1)-(fontRendererObj.getStringWidth(s)>>1),(height>>1)-82,0x404040);
	}
	
	@Override
	public boolean doesGuiPauseGame(){
		return false;
	}

	@Override
	public void setZLevel(float newZLevel){
		zLevel = newZLevel;
	}

	@Override
	public void callDrawGradientRect(int x1, int y1, int x2, int y2, int color1, int color2){
		drawGradientRect(x1,y1,x2,y2,color1,color2);
	}
}

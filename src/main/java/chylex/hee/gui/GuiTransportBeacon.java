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
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.server.S04TransportBeaconTravel;
import chylex.hee.proxy.ModCommonProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiTransportBeacon extends GuiScreen implements ITooltipRenderer{
	private static final ResourceLocation guiResource = new ResourceLocation("hardcoreenderexpansion:textures/gui/transport_beacon.png");
	private static final int size = 176;
	
	public final int centerX, centerY, centerZ;
	private int selectedX, selectedZ;
	private byte status;
	private Set<LocationXZ> locs = new HashSet<>();
	
	private GuiButton buttonTravel;
	
	public GuiTransportBeacon(int centerX, int centerY, int centerZ){
		this.centerX = selectedX = centerX;
		this.centerY = centerY;
		this.centerZ = selectedZ = centerZ;
	}
	
	public void loadOffsets(Set<LocationXZ> offsets){
		locs.clear();
		for(LocationXZ offset:offsets)locs.add(new LocationXZ(centerX+offset.x,centerZ+offset.z));
	}
	
	public void setStatus(boolean hasEnergy, boolean noTampering){
		status = 0;
		if (hasEnergy)status |= 0b1;
		if (noTampering)status |= 0b10;
		updateTravelButton();
	}
	
	public void updateStatusEvent(int eventId, boolean value){
		if (eventId == 1){
			status &= ~0b1;
			if (value)status |= 0b1;
		}
		else if (eventId == 0){
			status &= ~0b10;
			if (value)status |= 0b10;
		}
		
		updateTravelButton();
	}
	
	@Override
	public void initGui(){
		buttonList.add(buttonTravel = new GuiButton(1,(width>>1)-82,(height>>1)+61,80,20,I18n.format("container.button.travel")));
		buttonList.add(new GuiButton(2,(width>>1)+2,(height>>1)+61,80,20,I18n.format("gui.cancel")));
		buttonTravel.enabled = false;
	}
	
	private void updateTravelButton(){
		buttonTravel.enabled = (status&0b11) == 0b11 && !(centerX == selectedX && centerZ == selectedZ);
	}
	
	@Override
	protected void actionPerformed(GuiButton button){
		if (button.id == 1)PacketPipeline.sendToServer(new S04TransportBeaconTravel(centerX,centerY,centerZ,selectedX,selectedZ));
		mc.displayGuiScreen(null);
	}
	
	private boolean checkMouseOver(LocationXZ loc, int x, int y){
		int posX = (width>>1)-1+((loc.x-centerX)>>3), posY = (height>>1)-9+((loc.z-centerZ)>>3);
		return Math.abs(x-posX) <= 1 && Math.abs(y-posY) <= 1;
	}
	
	@Override
	protected void mouseClicked(int x, int y, int button){
		if (button == 0){
			for(LocationXZ loc:locs){
				if (checkMouseOver(loc,x,y)){
					selectedX = loc.x;
					selectedZ = loc.z;
					updateTravelButton();
					return;
				}
			}
		}
		
		super.mouseClicked(x,y,button);
	}
	
	@Override
	public void drawScreen(int x, int y, float renderPartialTicks){
		drawDefaultBackground();
		
		GL11.glColor4f(1F,1F,1F,1F);
		mc.getTextureManager().bindTexture(guiResource);
		
		int guiX = (width-size)>>1, guiY = (height-size)>>1;
		drawTexturedModalRect(guiX,guiY,0,0,size,size);
		
		boolean changedColor = false;
		int hoverX = Integer.MIN_VALUE, hoverZ = Integer.MIN_VALUE;
		
		for(LocationXZ loc:locs){
			if (loc.x == centerX && loc.z == centerZ){
				GL11.glColor4f(0.5F,0.3F,1F,1F);
				changedColor = true;
			}
			else if (loc.x == selectedX && loc.z == selectedZ){
				GL11.glColor4f(0.9F,0.6F,0F,1F);
				changedColor = true;
			}
			
			if (hoverX == Integer.MIN_VALUE && hoverZ == Integer.MIN_VALUE && checkMouseOver(loc,x,y)){
				hoverX = loc.x;
				hoverZ = loc.z;
			}
			
			drawTexturedModalRect((width>>1)-1+((loc.x-centerX)>>3),(height>>1)-9+((loc.z-centerZ)>>3),size,0,2,2);
			if (changedColor)GL11.glColor4f(1F,1F,1F,1F);
		}
		
		super.drawScreen(x,y,renderPartialTicks);
		
		String s = I18n.format(ModCommonProxy.hardcoreEnderbacon ? "container.transportBeacon.bacon" : "container.transportBeacon");
		fontRendererObj.drawString(s,(width>>1)-(fontRendererObj.getStringWidth(s)>>1),(height>>1)-82,0x404040);
		
		String locs = null;
		
		if (hoverX != Integer.MIN_VALUE && hoverZ != Integer.MIN_VALUE)locs = hoverX+", "+hoverZ;
		else if (selectedX != centerX && selectedZ != centerZ)locs = selectedX+", "+selectedZ;
		else locs = centerX+", "+centerZ;
		
		fontRendererObj.drawString(locs,(width>>1)+(size>>1)-fontRendererObj.getStringWidth(locs)-6,(height>>1)+52,0x404040);
		
		if (!buttonTravel.enabled && x >= buttonTravel.xPosition && y >= buttonTravel.yPosition && x < buttonTravel.xPosition+buttonTravel.getButtonWidth() && y < buttonTravel.yPosition+buttonTravel.func_154310_c()){
			if ((status&0b10) == 0){
				GuiItemRenderHelper.setupTooltip(x,y,I18n.format(mc.theWorld.provider.dimensionId == 1 ? "container.transportBeacon.error.tamper" : "container.transportBeacon.error.dimension"));
				GuiItemRenderHelper.drawTooltip(this,fontRendererObj);
			}
			else if (selectedX == centerX && selectedZ == centerZ){
				GuiItemRenderHelper.setupTooltip(x,y,I18n.format("container.transportBeacon.error.nodestination"));
				GuiItemRenderHelper.drawTooltip(this,fontRendererObj);
			}
			else if ((status&0b1) == 0){
				GuiItemRenderHelper.setupTooltip(x,y,I18n.format("container.transportBeacon.error.noenergy"));
				GuiItemRenderHelper.drawTooltip(this,fontRendererObj);
			}
		}
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

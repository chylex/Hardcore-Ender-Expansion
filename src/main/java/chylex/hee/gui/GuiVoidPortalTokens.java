package chylex.hee.gui;
import java.util.List;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import chylex.hee.gui.helpers.IContainerEventHandler;
import chylex.hee.init.ItemList;
import chylex.hee.system.abstractions.GL;
import chylex.hee.system.abstractions.Pos;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiVoidPortalTokens extends GuiContainer{
	private static final ResourceLocation texture = new ResourceLocation("hardcoreenderexpansion:textures/gui/void_portal_tokens.png");

	private final EntityPlayer player;
	private boolean isHoveringToken;
	
	public GuiVoidPortalTokens(EntityPlayer player, Pos voidPortalPos){
		super(new ContainerVoidPortalTokens(player,voidPortalPos));
		this.player = player;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		fontRendererObj.drawString(I18n.format("container.voidPortal"),8,6,(24<<16)|(24<<8)|24);
		fontRendererObj.drawString(I18n.format(player.inventory.getInventoryName()),8,ySize-85,(64<<16)|(64<<8)|64);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int mouseX, int mouseY){
		GL.color(1F,1F,1F,1F);
		mc.getTextureManager().bindTexture(texture);
		drawTexturedModalRect((width-xSize)/2,(height-ySize)/2,0,0,xSize,174);
	}
	
	@Override
	protected void handleMouseClick(Slot slot, int slotNumber, int buttonId, int sourceType){
		if (buttonId == 1 && slot != null && slot.getHasStack() && slot.getStack().getItem() == ItemList.portal_token && slotNumber < 27){
			IContainerEventHandler.sendEvent(slotNumber);
			return;
		}
		
		super.handleMouseClick(slot,slotNumber,buttonId,sourceType);
	}
	
	@Override
	protected void renderToolTip(ItemStack is, int mouseX, int mouseY){
		isHoveringToken = is.getItem() == ItemList.portal_token && mouseY-guiTop < 72;
		super.renderToolTip(is,mouseX,mouseY);
		isHoveringToken = false;
	}
	
	@Override
	protected void drawHoveringText(List textLines, int mouseX, int mouseY, FontRenderer fontRenderer){
		if (isHoveringToken)textLines.add(EnumChatFormatting.GREEN+"Right-click to activate");
		super.drawHoveringText(textLines,mouseX,mouseY,fontRenderer);
	}
}

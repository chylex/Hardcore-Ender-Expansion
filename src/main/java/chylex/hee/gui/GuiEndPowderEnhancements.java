package chylex.hee.gui;
import java.util.Iterator;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import chylex.hee.gui.GuiItemRenderHelper.ITooltipRenderer;
import chylex.hee.mechanics.enhancements.EnhancementHandler;
import chylex.hee.mechanics.enhancements.SlotList;
import chylex.hee.mechanics.enhancements.SlotList.SlotType;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.server.S01GuiEnhancementsClick;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEndPowderEnhancements extends GuiContainer implements ITooltipRenderer{
	private static ResourceLocation guiResource = new ResourceLocation("hardcoreenderexpansion:textures/gui/end_powder_enhancements.png");
	
	private final ContainerEndPowderEnhancements container;
	private int selectedEnhancementSlot = -1;
	
	public GuiEndPowderEnhancements(InventoryPlayer inv){
		super(new ContainerEndPowderEnhancements(inv));
		container = (ContainerEndPowderEnhancements)inventorySlots;
		ySize += 4;
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button){
		if (container.getSlot(0).getStack() != null){
			for(int a = 0; a < container.enhancementSlotX.length; a++){
				int x = container.enhancementSlotX[a];
				
				if (checkRect(mouseX-guiLeft,mouseY-guiTop,x,37,17,17) && !container.clientEnhancementBlocked[a]){
					selectedEnhancementSlot = a;
					container.onEnhancementSlotChangeClient(a);
					PacketPipeline.sendToServer(new S01GuiEnhancementsClick(a));
					return;
				}
			}
		}
		
		super.mouseClicked(mouseX,mouseY,button);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		String s = I18n.format("container.endPowderEnhancements");
		fontRendererObj.drawString(s,xSize/2-fontRendererObj.getStringWidth(s)/2,6,4210752);
		fontRendererObj.drawString(I18n.format("container.inventory"),8,ySize-96+2,4210752);
		
		ItemStack mainIS = container.getSlot(0).getStack();
		if (mainIS == null)selectedEnhancementSlot = -1;
		
		String selectedTooltip = null;

		for(int a = 0, x, y; a < container.enhancementSlotX.length; a++){
			x = container.enhancementSlotX[a];
			y = 37;
			
			ItemStack toRender = container.clientEnhancementItems[a];
			
			if (toRender != null){
				if (container.clientEnhancementBlocked[a])drawRectangle(x,y,x+16,y+16,96<<24);
				else if (a == selectedEnhancementSlot)drawRectangle(x,y,x+16,y+16,-2130706433);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				
				GL11.glEnable(GL11.GL_LIGHTING);
				zLevel = itemRender.zLevel = 100F;
				itemRender.renderItemAndEffectIntoGUI(fontRendererObj,mc.getTextureManager(),toRender,x,y);
				itemRender.renderItemOverlayIntoGUI(fontRendererObj,mc.getTextureManager(),toRender,x,y,null);
				itemRender.zLevel = zLevel = 0F;
			}

			if (checkRect(mouseX-guiLeft,mouseY-guiTop,x,y,17,17)){
				if (!container.clientEnhancementBlocked[a])drawRectangle(x,y,x+16,y+16,-2130706433);
				selectedTooltip = container.clientEnhancementTooltips[a];
			}
		}
		
		if (selectedTooltip != null)GuiItemRenderHelper.drawTooltip(this,fontRendererObj,mouseX-guiLeft,mouseY-guiTop,selectedTooltip);
		
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int mouseX, int mouseY){
		GL11.glColor4f(1F,1F,1F,1F);
		mc.getTextureManager().bindTexture(guiResource);
		
		int guiX = (width-xSize)>>1, guiY = (height-ySize)>>1;
		drawTexturedModalRect(guiX,guiY,0,0,xSize,ySize);
		
		if (container.containerInv.getStackInSlot(0) != null){
			int enhAmount = EnhancementHandler.getEnhancementsForItem(container.containerInv.getStackInSlot(0).getItem()).size();
			
			for(int a = 0, w = 18*enhAmount, x; a < enhAmount; a++){
				x = (int)Math.floor(88-w*0.5F+18*a);
				drawTexturedModalRect(guiX+x,guiY+36,176,0,18,18);
				container.enhancementSlotX[a] = x+1;
			}
			
			if (selectedEnhancementSlot != -1){
				SlotList slots = EnhancementHandler.getEnhancementSlotsForItem(container.containerInv.getStackInSlot(0).getItem());
				int index = 0, indexPowder = 0, indexIngredient = 0, w = 18*(slots.amountPowder+slots.amountIngredient), x;
				
				for(Iterator<SlotType> iter = slots.iterator(); iter.hasNext();){
					SlotType type = iter.next();
					if (type != SlotType.POWDER && type != SlotType.INGREDIENT)throw new RuntimeException("Invalid slot type "+type);
	
					x = (int)Math.floor(88-w*0.5F+18*(index++));
					drawTexturedModalRect(guiX+x,guiY+56,176,type == SlotType.POWDER ? 18 : 0,18,18);
					(type == SlotType.POWDER ? container.powderSlots : container.ingredientSlots)[type == SlotType.POWDER ? indexPowder++ : indexIngredient++].xDisplayPosition = x+1;
				}
			}
		}
		else{
			for(int a = 0; a < container.enhancementSlotX.length; a++)container.enhancementSlotX[a] = -3200;
			for(int a = 0; a < container.powderSlots.length; a++)container.powderSlots[a].xDisplayPosition = -3200;
			for(int a = 0; a < container.ingredientSlots.length; a++)container.ingredientSlots[a].xDisplayPosition = -3200;
		}
	}
	
	private void drawRectangle(int x1, int y1, int x2, int y2, int color){
		GL11.glColorMask(true,true,true,false);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		drawRect(x1,y1,x2,y2,color);
		GL11.glColorMask(true,true,true,true);
		GL11.glEnable(GL11.GL_LIGHTING);
	}
	
	private boolean checkRect(int mouseX, int mouseY, int x, int y, int w, int h){
		return mouseX >= x && mouseX <= x+w && mouseY >= y && mouseY <= y+h;
	}
	
	private boolean isMouseOverSlot(Slot slot, int mouseX, int mouseY){
		return checkRect(mouseX-guiLeft,mouseY-guiTop,slot.xDisplayPosition-1,slot.yDisplayPosition-1,17,17);
	}

	@Override
	public int getWidth(){
		return width;
	}

	@Override
	public int getHeight(){
		return height;
	}

	@Override
	public void setZLevel(float newZLevel){
		this.zLevel = newZLevel;
	}

	@Override
	public void callDrawGradientRect(int x1, int y1, int x2, int y2, int color1, int color2){
		drawGradientRect(x1,y1,x2,y2,color1,color2);
	}
}

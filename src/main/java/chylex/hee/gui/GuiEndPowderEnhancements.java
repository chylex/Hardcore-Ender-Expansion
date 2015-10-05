package chylex.hee.gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import chylex.hee.gui.helpers.GuiItemRenderHelper.ITooltipRenderer;
import chylex.hee.mechanics.enhancements.EnhancementRegistry;
import chylex.hee.mechanics.enhancements.IEnhanceableTile;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEndPowderEnhancements extends GuiContainer implements ITooltipRenderer{
	private static final ResourceLocation guiResource = new ResourceLocation("hardcoreenderexpansion:textures/gui/end_powder_enhancements.png");
	private static final int enhListY = 35;
	
	private final ContainerEndPowderEnhancements container;
	private Enum selectedEnhancement;
	
	private GuiEndPowderEnhancements(Container container){
		super(container);
		this.container = (ContainerEndPowderEnhancements)inventorySlots;
		ySize = 194;
	}
	
	public GuiEndPowderEnhancements(InventoryPlayer inv){
		this(new ContainerEndPowderEnhancements(inv,null));
	}
	
	public GuiEndPowderEnhancements(InventoryPlayer inv, IEnhanceableTile tile){
		this(new ContainerEndPowderEnhancements(inv,tile));
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button){
		if (selectedEnhancement != null){
			if (checkRect(mouseX,mouseY,guiLeft+xSize/2-64,guiTop+enhListY-2,13,13)){
				selectedEnhancement = null;
				return;
			}
		}
		else if (container.getSlot(0).getHasStack()){
			Enum[] enhancements = EnhancementRegistry.listEnhancements(container.getSlot(0).getStack().getItem());
			
			for(int enh = 0; enh < enhancements.length; enh++){
				if (checkRect(mouseX,mouseY,guiLeft+xSize/2-50,guiTop+enhListY+1+enh*9,100,9)){
					selectedEnhancement = enhancements[enh];
					return;
				}
			}
			/* TODO for(int a = 0; a < container.enhancementSlotX.length; a++){
				int x = container.enhancementSlotX[a];
				
				if (checkRect(mouseX-guiLeft,mouseY-guiTop,x,37,17,17) && !container.clientEnhancementBlocked[a]){
					selectedEnhancementSlot = a;
					container.onEnhancementSlotChangeClient(a);
					PacketPipeline.sendToServer(new S01GuiEnhancementsClick(a));
					return;
				}
			}*/
		}
		
		super.mouseClicked(mouseX,mouseY,button);
	}
	
	@Override
	public void updateScreen(){
		super.updateScreen();
		if (!container.getSlot(0).getHasStack())selectedEnhancement = null;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		if (selectedEnhancement != null){
			fontRendererObj.drawString(EnhancementRegistry.getEnhancementName(selectedEnhancement),41,enhListY+1,0x404040);
			fontRendererObj.drawString("<",28,enhListY+1,checkRect(mouseX,mouseY,guiLeft+xSize/2-64,guiTop+enhListY-2,13,13) ? 0xEEEEEE : 0x404040);
		}
		else if (container.getSlot(0).getHasStack()){
			Enum[] enhancements = EnhancementRegistry.listEnhancements(container.getSlot(0).getStack().getItem());
			
			for(int enh = 0; enh < enhancements.length; enh++){
				fontRendererObj.drawString(EnhancementRegistry.getEnhancementName(enhancements[enh]),41,enhListY+1+enh*9,checkRect(mouseX,mouseY,guiLeft+40,guiTop+1+enhListY+enh*9,100,8) ? 0xEEEEEE : 0x404040);
			}
		}
		
		/* TODO String s = I18n.format(ModCommonProxy.hardcoreEnderbacon ? "container.endPowderEnhancements.bacon" : "container.endPowderEnhancements");
		fontRendererObj.drawString(s,xSize/2-fontRendererObj.getStringWidth(s)/2,6,0x404040);
		fontRendererObj.drawString(I18n.format("container.inventory"),8,ySize-96+2,0x404040);
		
		ItemStack mainIS = container.getSlot(0).getStack();
		if (mainIS == null)selectedEnhancementSlot = -1;

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
				GuiItemRenderHelper.setupTooltip(mouseX-guiLeft,mouseY-guiTop,container.clientEnhancementTooltips[a]);
			}
		}
		
		GuiItemRenderHelper.drawTooltip(this,fontRendererObj);*/
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int mouseX, int mouseY){
		GL11.glColor4f(1F,1F,1F,1F);
		mc.getTextureManager().bindTexture(guiResource);
		
		int guiX = (width-xSize)/2, guiY = (height-ySize)/2, centerX = guiX+xSize/2;
		drawTexturedModalRect(guiX,guiY,0,0,xSize,ySize);
		
		if (selectedEnhancement != null){
			drawTexturedModalRect(centerX-4,guiY+27,101,195,8,4);
			drawTexturedModalRect(centerX-50,guiY+enhListY-2,0,195,100,2);
			drawTexturedModalRect(centerX-50,guiY+enhListY,0,198,100,9);
			drawTexturedModalRect(centerX-50,guiY+enhListY+9,0,208,100,2);
			drawTexturedModalRect(centerX-64,guiY+enhListY-2,110,195,13,13);
		}
		else if (container.getSlot(0).getHasStack()){
			Enum[] enhancements = EnhancementRegistry.listEnhancements(container.getSlot(0).getStack().getItem());
			
			drawTexturedModalRect(centerX-4,guiY+27,101,195,8,4);
			drawTexturedModalRect(centerX-50,guiY+enhListY-2,0,195,100,2);
			drawTexturedModalRect(centerX-50,guiY+enhListY+enhancements.length*9,0,208,100,2);
			
			for(int enh = 0; enh < enhancements.length; enh++){
				drawTexturedModalRect(centerX-50,guiY+enhListY+enh*9,0,198,100,9);
			}
		}
		/* TODO
		if (container.isEnhancingTile())drawTexturedModalRect(guiX+79,guiY+16,176,0,18,18);
		else drawTexturedModalRect(guiX+52,guiY+16,176,0,72,18);
		
		if (container.containerInv.getStackInSlot(0) == null){
			for(int a = 0; a < container.enhancementSlotX.length; a++)container.enhancementSlotX[a] = -3200;
			for(int a = 0; a < container.powderSlots.length; a++)container.powderSlots[a].xDisplayPosition = -3200;
			for(int a = 0; a < container.ingredientSlots.length; a++)container.ingredientSlots[a].xDisplayPosition = -3200;
		}
		else{
			int enhAmount = EnhancementHandler.getEnhancementsForItem(container.containerInv.getStackInSlot(0).getItem()).size();
			
			for(int a = 0, w = 18*enhAmount, x; a < enhAmount; a++){
				x = MathUtil.floor(88-w*0.5F+18*a);
				drawTexturedModalRect(guiX+x,guiY+36,176,0,18,18);
				container.enhancementSlotX[a] = x+1;
			}
			
			if (selectedEnhancementSlot != -1){
				SlotList slots = EnhancementHandler.getEnhancementSlotsForItem(container.containerInv.getStackInSlot(0).getItem());
				int index = 0, indexPowder = 0, indexIngredient = 0, w = 18*(slots.amountPowder+slots.amountIngredient), x;
				
				for(Iterator<SlotType> iter = slots.iterator(); iter.hasNext();){
					SlotType type = iter.next();
					if (type != SlotType.POWDER && type != SlotType.INGREDIENT)throw new IllegalArgumentException("Invalid slot type "+type);
					
					x = MathUtil.floor(88-w*0.5F+18*(index++));
					drawTexturedModalRect(guiX+x,guiY+56,176,type == SlotType.POWDER ? 18 : 0,18,18);
					(type == SlotType.POWDER ? container.powderSlots : container.ingredientSlots)[type == SlotType.POWDER ? indexPowder++ : indexIngredient++].xDisplayPosition = x+1;
				}
			}
		}*/
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

	@Override
	public void setZLevel(float newZLevel){
		this.zLevel = newZLevel;
	}

	@Override
	public void callDrawGradientRect(int x1, int y1, int x2, int y2, int color1, int color2){
		drawGradientRect(x1,y1,x2,y2,color1,color2);
	}
}

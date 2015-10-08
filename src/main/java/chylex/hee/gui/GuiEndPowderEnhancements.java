package chylex.hee.gui;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import chylex.hee.gui.helpers.GuiItemRenderHelper;
import chylex.hee.gui.helpers.GuiItemRenderHelper.ITooltipRenderer;
import chylex.hee.mechanics.enhancements.EnhancementData;
import chylex.hee.mechanics.enhancements.EnhancementData.EnhancementInfo;
import chylex.hee.mechanics.enhancements.EnhancementIngredient;
import chylex.hee.mechanics.enhancements.EnhancementList;
import chylex.hee.mechanics.enhancements.EnhancementRegistry;
import chylex.hee.mechanics.enhancements.IEnhanceableTile;
import com.google.common.base.Joiner;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiEndPowderEnhancements extends GuiContainer implements ITooltipRenderer{
	private static final ResourceLocation guiResource = new ResourceLocation("hardcoreenderexpansion:textures/gui/end_powder_enhancements.png");
	private static final int enhListY = 35;
	
	private final ContainerEndPowderEnhancements container;
	private EnhancementData<?>.EnhancementInfo selectedEnhancement;
	private GuiButtonExt enhanceButton, backButton;
	
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
	public void initGui(){
		super.initGui();
		buttonList.add(enhanceButton = new GuiButtonSmall(1,guiLeft+xSize/2-30,guiTop+enhListY+45,60,13,"Enhance",true));
		buttonList.add(backButton = new GuiButtonSmall(2,guiLeft+xSize/2-64,guiTop+enhListY-2,13,13,"<",false));
		enhanceButton.visible = false;
		backButton.visible = false;
	}
	
	@Override
	protected void actionPerformed(GuiButton button){
		if (button == backButton){
			selectedEnhancement = null;
		}
		else if (button == enhanceButton){
			// TODO
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button){
		if (container.getSlot(0).getHasStack()){
			int enhY = guiTop+enhListY+1;
			
			for(EnhancementInfo info:container.listEnhancementInfo()){
				if (checkRect(mouseX,mouseY,guiLeft+xSize/2-50,enhY,100,9)){
					selectedEnhancement = info;
					enhanceButton.visible = true;
					backButton.visible = true;
					return;
				}
				
				enhY += 9;
			}
		}
		
		super.mouseClicked(mouseX,mouseY,button);
	}
	
	@Override
	public void updateScreen(){
		super.updateScreen();
		
		if (!container.getSlot(0).getHasStack()){
			selectedEnhancement = null;
			enhanceButton.visible = false;
		}
		
		if (selectedEnhancement != null){
			enhanceButton.enabled = container.getMissingUpgradeIngredients(selectedEnhancement).isEmpty();
		}
		else{
			enhanceButton.visible = false;
			backButton.visible = false;
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		fontRendererObj.drawString(I18n.format("container.inventory"),8,ySize-96+2,0x404040);
		
		if (selectedEnhancement != null){
			fontRendererObj.drawStringWithShadow(selectedEnhancement.getName(),41,enhListY+1,0xEEEEEE);
		}
		else if (container.getSlot(0).getHasStack()){
			int offY = enhListY+1;
			
			for(EnhancementInfo info:EnhancementRegistry.listEnhancementInfo(container.getSlot(0).getStack().getItem())){
				int color = checkRect(mouseX,mouseY,guiLeft+40,guiTop+offY,100,8) ? 0xEEEEEE :
					container.getMissingUpgradeIngredients(info).isEmpty() ? 0xFFFF55 : 0xDDDDDD;
				
				fontRendererObj.drawStringWithShadow(info.getName(),41,offY,color);
				offY += 9;
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float renderPartialTicks, int mouseX, int mouseY){
		GL11.glColor4f(1F,1F,1F,1F);
		mc.getTextureManager().bindTexture(guiResource);
		
		int guiX = (width-xSize)/2, guiY = (height-ySize)/2, centerX = guiX+xSize/2;
		drawTexturedModalRect(guiX,guiY,0,0,xSize,ySize);
		
		if (selectedEnhancement != null){
			drawTexturedModalRect(centerX-50,guiY+enhListY-2,0,226,100,2);
			drawTexturedModalRect(centerX-50,guiY+enhListY,0,229,100,9);
			drawTexturedModalRect(centerX-50,guiY+enhListY+9,0,239,100,2);
			
			drawTexturedModalRect(centerX-4,guiY+27,101,226,8,4);
			drawTexturedModalRect(centerX-4,guiY+enhListY+13,101,226,8,4);
			drawTexturedModalRect(centerX-4,guiY+enhListY+39,101,226,8,4);
			
			int level = container.getEnhancements().get(selectedEnhancement.getEnhancement());
			
			for(int bar = 0; bar < selectedEnhancement.getMaxLevel(); bar++){
				drawTexturedModalRect(centerX+45-bar*3,guiY+enhListY+1,level > bar ? 146 : 143,226,2,7);
			}
			
			List<EnhancementIngredient> ingredients = selectedEnhancement.getIngredients(level+1);
			Collection<EnhancementIngredient> missing = container.getMissingUpgradeIngredients(selectedEnhancement);
			
			int ingX = centerX-(18*ingredients.size()+2*(ingredients.size()-1))/2, ingY = guiY+enhListY+19;
			
			for(EnhancementIngredient ingredient:ingredients){
				drawTexturedModalRect(ingX,ingY,124,226,18,18);
				ingX += 20;
			}
			
			ingX -= ingredients.size()*20;
			
			for(EnhancementIngredient ingredient:ingredients){
				GuiItemRenderHelper.renderItemIntoGUI(mc.getTextureManager(),ingredient.selector.getRepresentativeItem(),ingX+1,ingY+1);
				
				String amt = String.valueOf(ingredient.getAmount(level+1));
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDisable(GL11.GL_BLEND);
				fontRendererObj.drawStringWithShadow(amt,ingX+18-fontRendererObj.getStringWidth(amt),ingY+10,missing.contains(ingredient) ? 0xFFC3C3 : 0xFFFFFF);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				
				if (checkRect(mouseX,mouseY,ingX,ingY,18,18)){
					GuiItemRenderHelper.setupTooltip(mouseX,mouseY,Joiner.on('\n').join(ingredient.selector.getRepresentativeItem().getTooltip(mc.thePlayer,mc.gameSettings.advancedItemTooltips)));
				}

				ingX += 20;
			}
			
			GuiItemRenderHelper.drawTooltip(this,fontRendererObj);
		}
		else if (container.getSlot(0).getHasStack()){
			List<EnhancementInfo> enhancements = container.listEnhancementInfo();
			EnhancementList list = container.getEnhancements();
			
			drawTexturedModalRect(centerX-4,guiY+27,101,226,8,4);
			drawTexturedModalRect(centerX-50,guiY+enhListY-2,0,226,100,2);
			drawTexturedModalRect(centerX-50,guiY+enhListY+enhancements.size()*9,0,239,100,2);
			
			for(int enh = 0; enh < enhancements.size(); enh++){
				drawTexturedModalRect(centerX-50,guiY+enhListY+enh*9,0,229,100,9);
				
				int level = list.get(enhancements.get(enh).getEnhancement());
				
				for(int bar = 0; bar < enhancements.get(enh).getMaxLevel(); bar++){
					drawTexturedModalRect(centerX+45-bar*3,guiY+enhListY+1+enh*9,level > bar ? 146 : 143,226,2,7);
				}
			}
		}
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

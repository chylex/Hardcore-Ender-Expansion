package chylex.hee.mechanics.compendium.render;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.gui.helpers.GuiItemRenderHelper;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.player.PlayerCompendiumData.FragmentPurchaseStatus;

public class PurchaseDisplayElement{
	public final Object object;
	public final int price;
	private final FragmentPurchaseStatus status;
	private final int y;
	public final boolean fragmentHasRedirect;
	
	public PurchaseDisplayElement(KnowledgeFragment fragment, int y, FragmentPurchaseStatus status){
		this.object = fragment;
		this.price = fragment.getPrice();
		this.y = y;
		this.status = status;
		this.fragmentHasRedirect = fragment.getUnlockRedirect() != null;
	}
	
	public PurchaseDisplayElement(KnowledgeObject<?> object, int y, FragmentPurchaseStatus status){
		this.object = object;
		this.price = object.getUnlockPrice();
		this.y = y;
		this.status = status;
		this.fragmentHasRedirect = false;
	}
	
	public void render(GuiScreen gui, int mouseX, int mouseY, int pageCenterX){
		pageCenterX += 3;
		
		GL11.glColor4f(1F,1F,1F,0.96F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
		RenderHelper.disableStandardItemLighting();
		
		gui.mc.getTextureManager().bindTexture(GuiEnderCompendium.texPage);
		gui.drawTexturedModalRect(pageCenterX-27,y-14,155,0,54,26);
		
		RenderHelper.enableGUIStandardItemLighting();
		GuiEnderCompendium.renderItem.renderItemIntoGUI(gui.mc.fontRenderer,gui.mc.getTextureManager(),GuiEnderCompendium.knowledgeFragmentIS,pageCenterX-22,y-10);
		RenderHelper.disableStandardItemLighting();
		
		String price = status == FragmentPurchaseStatus.NOT_BUYABLE ? "---" : String.valueOf(this.price);
		int color = status == FragmentPurchaseStatus.CAN_PURCHASE ? 0x404040 :
					(status == FragmentPurchaseStatus.REQUIREMENTS_UNFULFILLED || status == FragmentPurchaseStatus.NOT_BUYABLE) ? 0x888888 :
					status == FragmentPurchaseStatus.NOT_ENOUGH_POINTS ? 0xdd2020 : 0;
		gui.mc.fontRenderer.drawString(price,pageCenterX-gui.mc.fontRenderer.getStringWidth(price)+20,y-5,color);
		
		if (object instanceof KnowledgeObject){
			String name = ((KnowledgeObject)object).getTooltip();
			List<String> parsed = gui.mc.fontRenderer.listFormattedStringToWidth(name,GuiEnderCompendium.guiPageWidth);
			
			for(int a = 0, yy = y-25-(parsed.size()-1)*gui.mc.fontRenderer.FONT_HEIGHT; a < parsed.size(); a++){
				gui.mc.fontRenderer.drawString(parsed.get(a),pageCenterX-(gui.mc.fontRenderer.getStringWidth(parsed.get(a))>>1),yy,0x404040);
				yy += gui.mc.fontRenderer.FONT_HEIGHT;
			}
		}
		else if (isMouseOver(mouseX,mouseY,pageCenterX-3)){
			if (status == FragmentPurchaseStatus.NOT_BUYABLE){
				String tooltip = I18n.format("compendium.nonBuyable");
				if (fragmentHasRedirect)tooltip = new StringBuilder(tooltip).append('\n').append(EnumChatFormatting.DARK_PURPLE).append(I18n.format("compendium.viewObject")).append(' ').append(((KnowledgeFragment)object).getUnlockRedirect().getTooltip()).toString();
				GuiItemRenderHelper.setupTooltip(mouseX,mouseY,tooltip);
			}
			else if (status == FragmentPurchaseStatus.REQUIREMENTS_UNFULFILLED){
				GuiItemRenderHelper.setupTooltip(mouseX,mouseY,I18n.format("compendium.unfulfilledRequirements"));
			}
		}
	}
	
	public boolean isMouseOver(int mouseX, int mouseY, int pageCenterX){
		return mouseX >= (pageCenterX+3)-27 && mouseY >= y-14 && mouseX <= (pageCenterX+3)+27 && mouseY <= y+12;
	}
	
	public FragmentPurchaseStatus getStatus(){
		return status;
	}
}

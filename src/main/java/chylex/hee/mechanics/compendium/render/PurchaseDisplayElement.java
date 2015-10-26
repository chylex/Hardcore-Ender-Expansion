package chylex.hee.mechanics.compendium.render;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;

public class PurchaseDisplayElement{
	public final Object object;
	public final int price;
	private final int y;
	
	public PurchaseDisplayElement(KnowledgeFragment fragment, int y){
		this.object = fragment;
		this.price = fragment.getPrice();
		this.y = y;
	}
	
	public PurchaseDisplayElement(KnowledgeObject<?> object, int y){
		this.object = object;
		this.price = object.getPrice();
		this.y = y;
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
		
		String price = String.valueOf(this.price);
		/* TODO int color = status == FragmentPurchaseStatus.CAN_PURCHASE ? 0x404040 :
					(status == FragmentPurchaseStatus.REQUIREMENTS_UNFULFILLED || status == FragmentPurchaseStatus.NOT_BUYABLE) ? 0x888888 :
					status == FragmentPurchaseStatus.NOT_ENOUGH_POINTS ? 0xdd2020 : 0;*/
		gui.mc.fontRenderer.drawString(price,pageCenterX-gui.mc.fontRenderer.getStringWidth(price)+20,y-5,0x404040);
		
		if (object instanceof KnowledgeObject){
			String name = ((KnowledgeObject)object).getTranslatedTooltip();
			List<String> parsed = gui.mc.fontRenderer.listFormattedStringToWidth(name,GuiEnderCompendium.guiPageWidth);
			
			/* TODO for(int a = 0, yy = y-25-(parsed.size()-1)*gui.mc.fontRenderer.FONT_HEIGHT; a < parsed.size(); a++){
				gui.mc.fontRenderer.drawString(parsed.get(a),pageCenterX-(gui.mc.fontRenderer.getStringWidth(parsed.get(a))>>1),yy,0x404040);
				yy += gui.mc.fontRenderer.FONT_HEIGHT;
			}*/
		}
		else if (isMouseOver(mouseX,mouseY,pageCenterX-3)){
			/* TODO if (status == FragmentPurchaseStatus.REQUIREMENTS_UNFULFILLED){
				GuiItemRenderHelper.setupTooltip(mouseX,mouseY,I18n.format("compendium.unfulfilledRequirements"));
			}*/
		}
	}
	
	public boolean isMouseOver(int mouseX, int mouseY, int pageCenterX){
		return mouseX >= (pageCenterX+3)-27 && mouseY >= y-14 && mouseX <= (pageCenterX+3)+27 && mouseY <= y+12;
	}
}

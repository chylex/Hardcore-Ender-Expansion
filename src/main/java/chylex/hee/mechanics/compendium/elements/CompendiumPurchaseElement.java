package chylex.hee.mechanics.compendium.elements;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import org.lwjgl.opengl.GL11;
import chylex.hee.game.save.types.player.CompendiumFile;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.mechanics.compendium.handlers.CompendiumPageHandler;

public class CompendiumPurchaseElement{
	public final KnowledgeFragment fragment;
	public final int price;
	private final int x, y;
	
	public CompendiumPurchaseElement(KnowledgeFragment fragment, int x, int y){
		this.fragment = fragment;
		this.price = fragment.getPrice();
		this.x = x;
		this.y = y;
	}
	
	public void render(GuiScreen gui, CompendiumFile file, int mouseX, int mouseY){
		GL11.glColor4f(1F,1F,1F,isMouseOver(mouseX,mouseY) ? 1F : 0.95F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
		RenderHelper.disableStandardItemLighting();
		
		gui.mc.getTextureManager().bindTexture(CompendiumPageHandler.texPage);
		gui.drawTexturedModalRect(x-24,y-8,158,3,48,16);
		
		RenderHelper.enableGUIStandardItemLighting();
		GuiEnderCompendium.renderItem.renderItemIntoGUI(gui.mc.fontRenderer,gui.mc.getTextureManager(),GuiEnderCompendium.knowledgeFragmentIS,x-22,y-8);
		RenderHelper.disableStandardItemLighting();
		
		int red, green, blue;
		red = green = blue = isMouseOver(mouseX,mouseY) ? 64 : 96;
		if (file.getPoints() < price)red = Math.min(255,red*4)-64;
		
		String price = String.valueOf(this.price);
		gui.mc.fontRenderer.drawString(price,x-gui.mc.fontRenderer.getStringWidth(price)+20,y-3,(red<<16)|(green<<8)|blue);
	}
	
	public boolean isMouseOver(int mouseX, int mouseY){
		return mouseX >= x-24 && mouseY >= y-8 && mouseX < x+24 && mouseY <= y+8;
	}
}

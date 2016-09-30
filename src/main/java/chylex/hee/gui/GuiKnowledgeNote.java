package chylex.hee.gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import org.apache.commons.lang3.StringUtils;
import chylex.hee.gui.helpers.GuiHelper;
import chylex.hee.mechanics.compendium.handlers.CompendiumPageHandler;
import chylex.hee.system.abstractions.GL;
import cpw.mods.fml.client.config.GuiButtonExt;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiKnowledgeNote extends GuiScreen{
	private final String translatedText;
	private final int addedPoints;
	
	public GuiKnowledgeNote(String unlocalized, int addedPoints){
		this.translatedText = I18n.format(unlocalized);
		this.addedPoints = addedPoints;
	}
	
	@Override
	public void initGui(){
		buttonList.add(new GuiButtonExt(1, width/2-75, height/2+92, 150, 20, I18n.format("gui.done")));
	}
	
	@Override
	protected void actionPerformed(GuiButton button){
		if (button.id == 1)mc.setIngameFocus();
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTickTime){
		GL.color(1F, 1F, 1F, 1F);
		mc.getTextureManager().bindTexture(CompendiumPageHandler.texPage);
		drawTexturedModalRect((width-CompendiumPageHandler.pageWidth)/2, (height-CompendiumPageHandler.pageHeight)/2-20, 0, 0, CompendiumPageHandler.pageWidth, CompendiumPageHandler.pageHeight);
		
		GuiHelper.renderUnicodeString(translatedText, (width-CompendiumPageHandler.pageWidth)/2+18, (height-CompendiumPageHandler.pageHeight)/2+1, CompendiumPageHandler.innerWidth, 255<<24);
		
		if (addedPoints > 0){
			boolean origFont = fontRendererObj.getUnicodeFlag();
			fontRendererObj.setUnicodeFlag(true);
			
			String text = StringUtils.replaceOnce(I18n.format("compendium.notePoints"), "$", String.valueOf(addedPoints));
			GuiHelper.renderUnicodeString(text, (width-fontRendererObj.getStringWidth(text))/2, (height/2)+55, CompendiumPageHandler.innerWidth, 255<<24|(32<<16)|(32<<8)|32);
			
			fontRendererObj.setUnicodeFlag(origFont);
		}
		
		super.drawScreen(mouseX, mouseY, partialTickTime);
	}
}

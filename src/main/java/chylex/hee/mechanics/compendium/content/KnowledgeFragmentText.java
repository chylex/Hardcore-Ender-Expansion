package chylex.hee.mechanics.compendium.content;
import org.apache.commons.lang3.StringUtils;
import chylex.hee.gui.GuiEnderCompendium;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class KnowledgeFragmentText extends KnowledgeFragment{
	private String content;
	
	public KnowledgeFragmentText(int globalID){
		super(globalID);
	}

	public KnowledgeFragmentText setContents(String text){
		this.content = text;
		return this;
	}


	@Override
	@SideOnly(Side.CLIENT)
	public int getHeight(GuiEnderCompendium gui, boolean isUnlocked){
		boolean origFont = gui.mc.fontRenderer.getUnicodeFlag();
		gui.mc.fontRenderer.setUnicodeFlag(true);
		int h = gui.mc.fontRenderer.listFormattedStringToWidth(getString(isUnlocked),GuiEnderCompendium.guiPageWidth-10).size()*gui.mc.fontRenderer.FONT_HEIGHT;
		gui.mc.fontRenderer.setUnicodeFlag(origFont);
		return h;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(GuiEnderCompendium gui, int x, int y, int mouseX, int mouseY, boolean isUnlocked){
		boolean origFont = gui.mc.fontRenderer.getUnicodeFlag();
		gui.mc.fontRenderer.setUnicodeFlag(true);
		gui.mc.fontRenderer.drawSplitString(getString(isUnlocked),x+1,y,GuiEnderCompendium.guiPageWidth-10,255<<24);
		gui.mc.fontRenderer.setUnicodeFlag(origFont);
	}
	
	protected String getString(boolean isUnlocked){
		return isUnlocked ? content : StringUtils.repeat('?',content.length());
	}
}

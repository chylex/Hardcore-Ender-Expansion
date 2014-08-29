package chylex.hee.mechanics.compendium.content.type;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.StringUtils;
import chylex.hee.gui.GuiKnowledgeBook;
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
	public int getHeight(Minecraft mc, boolean isUnlocked){
		boolean origFont = mc.fontRenderer.getUnicodeFlag();
		mc.fontRenderer.setUnicodeFlag(true);
		int h = mc.fontRenderer.listFormattedStringToWidth(getString(isUnlocked),GuiKnowledgeBook.guiPageWidth-10).size()*mc.fontRenderer.FONT_HEIGHT;
		mc.fontRenderer.setUnicodeFlag(origFont);
		return h;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(int x, int y, Minecraft mc, boolean isUnlocked){
		boolean origFont = mc.fontRenderer.getUnicodeFlag();
		mc.fontRenderer.setUnicodeFlag(true);
		mc.fontRenderer.drawSplitString(getString(isUnlocked),x+1,y,GuiKnowledgeBook.guiPageWidth-10,255<<24);
		mc.fontRenderer.setUnicodeFlag(origFont);
	}
	
	protected String getString(boolean isUnlocked){
		return isUnlocked ? content : StringUtils.repeat('?',content.length());
	}
}

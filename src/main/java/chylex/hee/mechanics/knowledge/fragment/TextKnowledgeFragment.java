package chylex.hee.mechanics.knowledge.fragment;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import chylex.hee.gui.GuiKnowledgeBook;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TextKnowledgeFragment extends KnowledgeFragment{
	private String name;
	private boolean useAssets;
	private short replacedBy = -1;
	private short replacementFor = -1;
	
	public TextKnowledgeFragment(int id){
		super(id);
	}
	
	public TextKnowledgeFragment setUnlocalizedName(String unlocalizedName){
		this.name = unlocalizedName;
		this.useAssets = true;
		return this;
	}
	
	public TextKnowledgeFragment setLocalizedText(String localizedText){
		this.name = localizedText;
		this.useAssets = false;
		return this;
	}
	
	public TextKnowledgeFragment setReplacedBy(int replacedById){
		this.replacedBy = (short)replacedById;
		return this;
	}
	
	public TextKnowledgeFragment setReplacementFor(int replacementForId){
		this.replacementFor = (short)replacementForId;
		return this;
	}
	
	@Override
	public boolean canShow(int[] unlockedFragments){
		return (replacedBy == -1 || !ArrayUtils.contains(unlockedFragments,replacedBy)) &&
			   (replacementFor == -1 || (ArrayUtils.contains(unlockedFragments,replacementFor) && ArrayUtils.contains(unlockedFragments,id)));
	}
	
	@Override
	public void onUnlocked(int[] unlockedFragments, EntityPlayer player){
		if (replacementFor != -1 && !ArrayUtils.contains(unlockedFragments,replacementFor)){
			ArrayUtils.add(unlockedFragments,replacementFor);
			Arrays.sort(unlockedFragments);
		}
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
		return isUnlocked ? (useAssets ? I18n.format(name) : name) : StringUtils.repeat('?',(useAssets ? I18n.format(name) : name).length());
	}
}

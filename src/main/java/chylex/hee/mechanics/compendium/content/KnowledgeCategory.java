package chylex.hee.mechanics.compendium.content;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class KnowledgeCategory{
	private final String tooltip;
	private final ItemStack displayItem;
	
	public KnowledgeCategory(String tooltip, ItemStack displayItem){
		this.tooltip = tooltip;
		this.displayItem = displayItem;
	}
	
	@SideOnly(Side.CLIENT)
	public String getTranslatedTooltip(){
		return I18n.format(tooltip);
	}
	
	public ItemStack getDisplayItem(){
		return displayItem;
	}
}

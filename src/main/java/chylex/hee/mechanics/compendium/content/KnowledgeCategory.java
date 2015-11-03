package chylex.hee.mechanics.compendium.content;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class KnowledgeCategory{
	private final String tooltip;
	private final ItemStack displayItem;
	private final KnowledgeObject<?> targetObj;
	
	public KnowledgeCategory(String tooltip, ItemStack displayItem, KnowledgeObject<?> targetObj){
		this.tooltip = tooltip;
		this.displayItem = displayItem;
		this.targetObj = targetObj;
		this.targetObj.setCategoryObject();
	}
	
	@SideOnly(Side.CLIENT)
	public String getTranslatedTooltip(){
		return I18n.format(tooltip);
	}
	
	public ItemStack getDisplayItem(){
		return displayItem;
	}
	
	public KnowledgeObject<?> getTargetObj(){
		return targetObj;
	}
}

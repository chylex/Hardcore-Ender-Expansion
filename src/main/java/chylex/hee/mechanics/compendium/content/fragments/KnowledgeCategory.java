package chylex.hee.mechanics.compendium.content.fragments;
import net.minecraft.item.ItemStack;
import chylex.hee.mechanics.knowledge.util.IGuiItemStackRenderer;

public class KnowledgeCategory implements IGuiItemStackRenderer{
	public static final byte iconSize = 44;
	
	public final byte id;
	private final int x, y;
	private final String tooltip;
	private final ItemStack showcaseItem;
	
	public KnowledgeCategory(int id, int x, int y, String tooltip, ItemStack showcaseItem){
		this.id = (byte)id;
		this.x = x-iconSize;
		this.y = y-(iconSize>>1);
		this.tooltip = tooltip;
		this.showcaseItem = showcaseItem;
	}

	@Override
	public int getX(){
		return x;
	}

	@Override
	public int getY(){
		return y;
	}

	@Override
	public ItemStack getItemStack(){
		return showcaseItem;
	}

	@Override
	public String getTooltip(){
		return tooltip;
	}
}

package chylex.hee.mechanics.compendium.content.type;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import chylex.hee.mechanics.knowledge.util.IGuiItemStackRenderer;

public class KnowledgeCategory implements IGuiItemStackRenderer{
	public static final byte iconSize = 44;
	
	public final byte id;
	private final int x, y;
	private final String tooltip;
	private final ItemStack showcaseItem;
	private final List<KnowledgeObject> objectList = new ArrayList<>();
	
	public KnowledgeCategory(int id, int x, int y, String tooltip, ItemStack showcaseItem){
		this.id = (byte)id;
		this.x = x*iconSize-iconSize;
		this.y = y*iconSize-(iconSize>>1);
		this.tooltip = tooltip;
		this.showcaseItem = showcaseItem;
	}
	
	public void addKnowledgeObjects(KnowledgeObject[] objects){
		for(KnowledgeObject object:objects)objectList.add(object);
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

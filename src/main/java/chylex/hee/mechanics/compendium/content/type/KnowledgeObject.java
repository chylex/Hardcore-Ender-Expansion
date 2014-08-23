package chylex.hee.mechanics.compendium.content.type;
import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.item.ItemStack;
import chylex.hee.mechanics.compendium.content.objects.IKnowledgeObjectInstance;
import chylex.hee.mechanics.knowledge.util.IGuiItemStackRenderer;

public class KnowledgeObject<T extends IKnowledgeObjectInstance> implements IGuiItemStackRenderer{
	private static final int iconSize = 30;
	
	private final T object;
	private final ItemStack itemToRender;
	private final String tooltip;
	private final Set<KnowledgeFragment> fragmentSet = new LinkedHashSet<>();
	private int x, y, unlockPrice;
	
	public KnowledgeObject(T object){
		this(object,object.createItemStackToRender());
	}
	
	public KnowledgeObject(T object, ItemStack itemToRender){
		this(object,itemToRender,itemToRender.getDisplayName());
	}
	
	public KnowledgeObject(T object, String tooltip){
		this(object,object.createItemStackToRender(),tooltip);
	}
	
	public KnowledgeObject(T object, ItemStack itemToRender, String tooltip){
		this.object = object;
		this.itemToRender = itemToRender;
		this.tooltip = tooltip;
	}
	
	public KnowledgeObject setPos(int x, int y){
		this.x = x*iconSize;
		this.y = y*iconSize;
		return this;
	}
	
	public KnowledgeObject setUnlockPrice(int price){
		this.unlockPrice = price;
		return this;
	}
	
	public int getUnlockPrice(){
		return unlockPrice;
	}
	
	public KnowledgeObject setFragments(KnowledgeFragment[] fragments){
		for(KnowledgeFragment fragment:fragments)this.fragmentSet.add(fragment);
		return this;
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
		return itemToRender;
	}

	@Override
	public String getTooltip(){
		return tooltip;
	}
	
	public enum Type{
		BLOCK, ITEM, MOB
	}
}

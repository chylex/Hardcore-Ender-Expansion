package chylex.hee.mechanics.compendium.content;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import chylex.hee.mechanics.compendium.objects.IKnowledgeObjectInstance;
import com.google.common.collect.ImmutableList;

public class KnowledgeCategory{
	private final String tooltip;
	private final ItemStack showcaseItem;
	private final List<KnowledgeObject> objectList = new ArrayList<>();
	private ImmutableList<KnowledgeObject> objectListImmutable;
	private KnowledgeObject<? extends IKnowledgeObjectInstance<?>> categoryObject;
	
	public KnowledgeCategory(String tooltip, ItemStack showcaseItem){
		this.tooltip = tooltip;
		this.showcaseItem = showcaseItem;
	}
	
	public void addKnowledgeObjects(KnowledgeObject[] objects){
		for(KnowledgeObject object:objects)objectList.add(object);
		objectListImmutable = ImmutableList.copyOf(objectList);
	}
	
	public ImmutableList<KnowledgeObject> getAllObjects(){
		return objectListImmutable;
	}
	
	public void setCategoryObject(KnowledgeObject<? extends IKnowledgeObjectInstance<?>> obj){
		this.categoryObject = obj;
	}
	
	public KnowledgeObject<? extends IKnowledgeObjectInstance<?>> getCategoryObject(){
		return categoryObject;
	}

	public ItemStack getItemStack(){
		return showcaseItem;
	}

	public String getTooltip(){
		return tooltip;
	}
}

package chylex.hee.mechanics.compendium.content.objects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ObjectItem implements IKnowledgeObjectInstance<Item>{
	private final Item item;
	
	public ObjectItem(Item item){
		this.item = item;
	}

	@Override
	public Item getUnderlyingObject(){
		return item;
	}
	
	@Override
	public ItemStack createItemStackToRender(){
		return new ItemStack(item);
	}

	@Override
	public boolean checkEquality(Object obj){
		return obj == item;
	}
}

package chylex.hee.mechanics.compendium.content.objects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ObjectItem implements IObjectHolder<ItemStack>{
	public static final byte wildcard = -1;
	
	private final ItemStack item;
	private final ItemStack displayIS;
	
	public ObjectItem(Item item){
		this.item = new ItemStack(item,1,wildcard);
		this.displayIS = new ItemStack(item);
	}
	
	public ObjectItem(Item item, int damage){
		this.item = new ItemStack(item,1,damage);
		this.displayIS = new ItemStack(item,1,damage == wildcard ? 0 : damage);
	}
	
	public ObjectItem(Item item, int displayDamage, boolean useWildcard){
		this.item = new ItemStack(item,1,useWildcard ? wildcard : displayDamage);
		this.displayIS = new ItemStack(item,1,displayDamage);
	}
	
	public ObjectItem(Item item, ItemStack displayIS){
		this.item = new ItemStack(item,1,wildcard);
		this.displayIS = displayIS;
	}
	
	@Override
	public ItemStack getDisplayItemStack(){
		return displayIS;
	}

	@Override
	public ItemStack getUnderlyingObject(){
		return item;
	}
	
	@Override
	public boolean areObjectsEqual(ItemStack obj){
		return obj.getItem() == item.getItem() && (obj.getItemDamage() == item.getItemDamage() || item.getItemDamage() == wildcard);
	}
}

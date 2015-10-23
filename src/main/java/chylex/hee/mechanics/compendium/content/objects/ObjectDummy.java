package chylex.hee.mechanics.compendium.content.objects;
import net.minecraft.item.ItemStack;

public class ObjectDummy implements IObjectHolder<String>{
	private final String identifier;
	private final ItemStack displayIS;
	
	public ObjectDummy(String identifier, ItemStack displayIS){
		this.identifier = identifier;
		this.displayIS = displayIS;
	}
	
	@Override
	public ItemStack getDisplayItemStack(){
		return displayIS;
	}

	@Override
	public String getUnderlyingObject(){
		return identifier;
	}
}

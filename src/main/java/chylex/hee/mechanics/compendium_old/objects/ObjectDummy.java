package chylex.hee.mechanics.compendium_old.objects;
import net.minecraft.item.ItemStack;
import chylex.hee.init.ItemList;
import chylex.hee.item.ItemSpecialEffects;

public class ObjectDummy implements IKnowledgeObjectInstance<String>{
	private final String identifier;
	
	public ObjectDummy(String identifier){
		this.identifier = identifier;
	}

	@Override
	public String getUnderlyingObject(){
		return identifier;
	}
	
	@Override
	public ItemStack createItemStackToRender(){
		return new ItemStack(ItemList.special_effects,1,ItemSpecialEffects.questionMark);
	}

	@Override
	public boolean checkEquality(Object o){
		return identifier.equals(o);
	}
}

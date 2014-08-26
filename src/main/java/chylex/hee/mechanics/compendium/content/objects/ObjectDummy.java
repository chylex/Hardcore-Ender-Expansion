package chylex.hee.mechanics.compendium.content.objects;
import net.minecraft.item.ItemStack;
import chylex.hee.item.ItemList;
import chylex.hee.item.ItemSpecialEffects;

public class ObjectDummy implements IKnowledgeObjectInstance<Object>{
	private static final Object dummy = new Object();

	@Override
	public Object getUnderlyingObject(){
		return dummy;
	}
	
	@Override
	public ItemStack createItemStackToRender(){
		return new ItemStack(ItemList.special_effects,1,ItemSpecialEffects.questionMark);
	}

	@Override
	public boolean checkEquality(Object o){
		return o == dummy;
	}
}

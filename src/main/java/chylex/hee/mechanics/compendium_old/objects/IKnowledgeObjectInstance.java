package chylex.hee.mechanics.compendium_old.objects;
import net.minecraft.item.ItemStack;

public interface IKnowledgeObjectInstance<T>{
	T getUnderlyingObject();
	ItemStack createItemStackToRender();
	boolean checkEquality(Object obj);
}

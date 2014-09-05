package chylex.hee.mechanics.compendium.objects;
import net.minecraft.item.ItemStack;

public interface IKnowledgeObjectInstance<T>{
	public T getUnderlyingObject();
	public ItemStack createItemStackToRender();
	public boolean checkEquality(Object obj);
}

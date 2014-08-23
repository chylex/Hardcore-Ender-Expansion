package chylex.hee.mechanics.compendium.content.objects;
import net.minecraft.item.ItemStack;

public interface IKnowledgeObjectInstance<T>{
	public ItemStack createItemStackToRender();
	public boolean areObjectsEqual(T obj1, T obj2);
}

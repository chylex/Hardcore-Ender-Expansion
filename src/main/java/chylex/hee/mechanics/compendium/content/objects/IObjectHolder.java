package chylex.hee.mechanics.compendium.content.objects;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;

public interface IObjectHolder<T>{
	ItemStack getDisplayItemStack();
	T getUnderlyingObject();
	
	default boolean checkEquality(@Nonnull Object obj){
		return obj.getClass() == getClass() && areObjectsEqual((T)obj);
	}
	
	default boolean areObjectsEqual(@Nonnull T obj){
		return obj.equals(getUnderlyingObject());
	}
}

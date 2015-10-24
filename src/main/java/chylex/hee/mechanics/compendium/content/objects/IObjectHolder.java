package chylex.hee.mechanics.compendium.content.objects;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;

public interface IObjectHolder<T>{
	ItemStack getDisplayItemStack();
	T getUnderlyingObject();
	
	default boolean checkEquality(@Nonnull Object obj){
		return obj.getClass() == getUnderlyingObject().getClass() && areObjectsEqual((T)obj);
	}
	
	default boolean checkEquality(@Nonnull ItemStack is){
		return ItemStack.class == getUnderlyingObject().getClass() && areObjectsEqual((T)is);
	}
	
	default boolean areObjectsEqual(@Nonnull T obj){
		return obj.equals(getUnderlyingObject());
	}
}

package chylex.hee.entity.mob.util;
import net.minecraft.item.ItemStack;

public interface IEndermanRenderer{
	default boolean isCarrying(){
		return getCarrying() != null;
	}
	
	ItemStack getCarrying();
	boolean isAggressive();
}

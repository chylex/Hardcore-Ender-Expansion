package chylex.hee.mechanics.compendium.util;
import net.minecraft.item.ItemStack;

public interface IGuiItemStackRenderer{
	int getX();
	int getY();
	ItemStack getItemStack();
	String getTooltip();
}

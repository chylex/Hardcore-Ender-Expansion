package chylex.hee.mechanics.minions.properties;
import net.minecraft.item.ItemStack;
import chylex.hee.mechanics.minions.MinionData;

public interface IMinionInfusionHandler{
	boolean canApply(MinionData data);
	void apply(MinionData data);
	int getEssenceCost();
	ItemStack[] getRecipe();
}

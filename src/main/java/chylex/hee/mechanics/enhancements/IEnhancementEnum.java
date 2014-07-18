package chylex.hee.mechanics.enhancements;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import chylex.hee.system.util.IItemSelector.IRepresentativeItemSelector;

public interface IEnhancementEnum{
	String getName();
	IRepresentativeItemSelector getItemSelector();
	void onEnhanced(ItemStack is, EntityPlayer player);
}

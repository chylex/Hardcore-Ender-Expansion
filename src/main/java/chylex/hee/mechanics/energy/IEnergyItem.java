package chylex.hee.mechanics.energy;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public interface IEnergyItem{
	default boolean canUse(ItemStack is){
		return is.getItemDamage() < is.getMaxDamage();
	};
	
	default boolean canAcceptEnergy(ItemStack is){
		return is.getItemDamage() > 0;
	};
	
	default void acceptEnergy(ItemStack is){
		is.setItemDamage(is.getItemDamage()-getEnergyAccepted(is));
	};
	
	default void useEnergy(ItemStack is, EntityLivingBase owner){
		is.damageItem(getEnergyUsage(is),owner);
	}
	
	int getEnergyUsage(ItemStack is);
	int getEnergyAccepted(ItemStack is);
}

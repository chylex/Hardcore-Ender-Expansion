package chylex.hee.item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.entity.projectile.EntityProjectileSpatialDash;
import chylex.hee.mechanics.enhancements.EnhancementHandler;
import chylex.hee.mechanics.enhancements.types.SpatialDashGemEnhancements;
import chylex.hee.system.util.MathUtil;

public class ItemSpatialDashGem extends ItemAbstractEnergyAcceptor{
	@Override
	public int getMaxDamage(ItemStack is){
		return EnhancementHandler.hasEnhancement(is,SpatialDashGemEnhancements.CAPACITY) ? MathUtil.ceil(1.5F*super.getMaxDamage(is)) : super.getMaxDamage(is);
	}
	
	@Override
	public boolean canAcceptEnergy(ItemStack is){
		return is.getItemDamage() > 0;
	}

	@Override
	public void onEnergyAccepted(ItemStack is){
		is.setItemDamage(is.getItemDamage()-3);
	}

	@Override
	public int getEnergyPerUse(ItemStack is){
		return 1;
	}
	
	@Override
	public boolean hasEffect(ItemStack is, int pass){
		return is.getItemDamage() == 1 || super.hasEffect(is,pass);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		if (is.getItemDamage() < getMaxDamage()){
			if (!world.isRemote){
				is.damageItem(getEnergyPerUse(is),player);
				world.spawnEntityInWorld(new EntityProjectileSpatialDash(world,player,EnhancementHandler.getEnhancements(is)));
			}
			else world.playSound(player.posX,player.posY,player.posZ,"hardcoreenderexpansion:player.random.spatialdash",0.8F,0.9F,false);
		}
		
		return is;
	}
}

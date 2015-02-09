package chylex.hee.item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.entity.projectile.EntityProjectileSpatialDash;

public class ItemSpatialDashGem extends ItemAbstractEnergyAcceptor{
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
	public boolean hasEffect(ItemStack is){
		return is.getItemDamage() == 1 || super.hasEffect(is);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		if (is.getItemDamage() < getMaxDamage()){
			if (!world.isRemote){
				is.damageItem(getEnergyPerUse(is),player);
				world.spawnEntityInWorld(new EntityProjectileSpatialDash(world,player));
			}
			else world.playSound(player.posX,player.posY,player.posZ,"hardcoreenderexpansion:player.random.spatialdash",0.8F,0.9F,false);
		}
		
		return is;
	}
}

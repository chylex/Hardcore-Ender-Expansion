package chylex.hee.item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.entity.projectile.EntityProjectileSpatialDash;
import chylex.hee.mechanics.enhancements._old.EnhancementHandler;

public class ItemSpatialDashGem extends ItemAbstractGem{
	public ItemSpatialDashGem(){
		setMaxDamage(120);
	}
	
	@Override
	public int getEnergyAccepted(ItemStack is){
		return 1;
	}
	
	@Override
	public int getEnergyUsage(ItemStack is){
		return 3;
	}
	
	@Override
	protected byte getCooldown(){
		return 17;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		if (!canUse(is))return is;
		
		if (!world.isRemote){
			// TODO CausatumUtils.increase(player,CausatumMeters.ITEM_USAGE,0.5F);
			useEnergy(is,player);
			world.spawnEntityInWorld(new EntityProjectileSpatialDash(world,player,EnhancementHandler.getEnhancements(is)));
		}
		else world.playSound(player.posX,player.posY,player.posZ,"hardcoreenderexpansion:player.random.spatialdash",0.8F,0.9F,false);
		
		return is;
	}
}

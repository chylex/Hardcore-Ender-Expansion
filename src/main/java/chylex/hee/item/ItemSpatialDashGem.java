package chylex.hee.item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.entity.projectile.EntityProjectileSpatialDash;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.mechanics.knowledge.util.ObservationUtil;

public class ItemSpatialDashGem extends ItemAbstractEnergyAcceptor{
	@Override
	protected boolean canAcceptEnergy(ItemStack is){
		return is.getItemDamage() > 0;
	}

	@Override
	protected void onEnergyAccepted(ItemStack is){
		is.setItemDamage(is.getItemDamage()-3);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		if (is.getItemDamage() < getMaxDamage()){
			if (!world.isRemote){
				is.setItemDamage(is.getItemDamage()+1);
				world.spawnEntityInWorld(new EntityProjectileSpatialDash(world,player));
				
				for(EntityPlayer observer:ObservationUtil.getAllObservers(player,8D)){
					KnowledgeRegistrations.SPATIAL_DASH_GEM.tryUnlockFragment(observer,0.1F);
				}
			}
			else world.playSound(player.posX,player.posY,player.posZ,"hardcoreenderexpansion:player.random.spatialdash",0.8F,0.9F,false);
		}
		
		return is;
	}
}

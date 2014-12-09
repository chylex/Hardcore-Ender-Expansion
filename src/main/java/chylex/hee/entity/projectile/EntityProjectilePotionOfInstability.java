package chylex.hee.entity.projectile;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import chylex.hee.item.ItemList;
import chylex.hee.item.ItemPotionOfInstability;

public class EntityProjectilePotionOfInstability extends EntityPotion{
	public EntityProjectilePotionOfInstability(World world){
		super(world);
	}

	public EntityProjectilePotionOfInstability(World world, EntityLivingBase thrower){
		super(world,thrower,new ItemStack(ItemList.potion_of_instability,1,1));
	}
	
	@Override
	protected void onImpact(MovingObjectPosition mop){
		if (!worldObj.isRemote){
			for(EntityLivingBase entity:(List<EntityLivingBase>)worldObj.getEntitiesWithinAABB(EntityLivingBase.class,boundingBox.expand(4D,2D,4D))){
				double dist = getDistanceSqToEntity(entity);

				if (dist < 16D){
					PotionEffect eff = ItemPotionOfInstability.getRandomPotionEffect(rand);
					int dur = (int)(((entity == mop.entityHit ? 1D : (1D-Math.sqrt(dist)/4D)))*eff.getDuration()+0.5D);
					
					if (dur > 20)entity.addPotionEffect(new PotionEffect(eff.getPotionID(),dur,eff.getAmplifier(),eff.getIsAmbient()));
				}
			}

			worldObj.playAuxSFX(2002,(int)Math.round(posX),(int)Math.round(posY),(int)Math.round(posZ),0);
			setDead();
		}
	}
}

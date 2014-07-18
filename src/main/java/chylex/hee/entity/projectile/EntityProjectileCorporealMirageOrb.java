package chylex.hee.entity.projectile;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.mob.EntityMobCorporealMirage;
import chylex.hee.item.ItemList;
import chylex.hee.system.util.MathUtil;

public class EntityProjectileCorporealMirageOrb extends EntityThrowable{
	public EntityProjectileCorporealMirageOrb(World world){
		super(world);
	}

	public EntityProjectileCorporealMirageOrb(World world, EntityPlayer thrower){
		super(world,thrower);
	}
	
	@Override
	protected void onImpact(MovingObjectPosition mop){
		if (!worldObj.isRemote){
			EntityLivingBase e = getThrower();
			
			if (e instanceof EntityPlayer){
				EntityMobCorporealMirage mirage = new EntityMobCorporealMirage(worldObj,posX,posY+0.5D,posZ,((EntityPlayer)e).getUniqueID().toString());
				mirage.usedMotionY = 0.12D;
				mirage.rotationYaw = MathHelper.wrapAngleTo180_float((float)MathUtil.toDeg(Math.atan2(posZ-e.posZ,posX-e.posX))-270F);
				mirage.rotationPitch = 20F;
				worldObj.spawnEntityInWorld(mirage);
			}
			
			setDead();
		}
		else{
			ItemStack is = new ItemStack(ItemList.corporeal_mirage_orb);
			
			for(int a = 0; a < 15; a++){
				HardcoreEnderExpansion.fx.item(is,worldObj,posX,posY,posZ,(rand.nextDouble()-rand.nextDouble())*0.1D,rand.nextDouble()*0.2D,(rand.nextDouble()-rand.nextDouble())*0.1D);
			}
			
			worldObj.playSound(posX,posY,posZ,"hardcoreenderexpansion:mob.mirage.appear",2F,1F,false);
		}
	}
	
	@Override
	protected float getGravityVelocity(){
		return 0.09F;
	}
}

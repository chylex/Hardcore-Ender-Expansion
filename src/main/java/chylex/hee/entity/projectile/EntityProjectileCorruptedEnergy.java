package chylex.hee.entity.projectile;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.api.interfaces.IIgnoreEnderGoo;
import chylex.hee.proxy.ModCommonProxy;

public class EntityProjectileCorruptedEnergy extends EntityFireball{
	public EntityProjectileCorruptedEnergy(World world){
		super(world);
		setSize(0F,0F);
	}
	
	public EntityProjectileCorruptedEnergy(World world, EntityLivingBase shooter, double x, double y, double z, Entity target){
		super(world,shooter,1D,1D,1D);
		setSize(0.25F,0.25F);
		setPosition(x,y,z);
		
		double speed = 0.21D+rand.nextDouble()*0.05D;
		Vec3 motionVec = Vec3.createVectorHelper(target.posX-x,target.posY+target.height*0.5F-y,target.posZ-z).normalize();
		motionX = motionVec.xCoord*speed+rand.nextGaussian()*0.015D;
		motionY = motionVec.yCoord*speed+rand.nextGaussian()*0.015D;
		motionZ = motionVec.zCoord*speed+rand.nextGaussian()*0.015D;
		
		accelerationX = accelerationY = accelerationZ = 0D;
	}
	
	@Override
	public void onUpdate(){
		for(int update = 0; update < 3; update++){
			super.onUpdate();
			if (worldObj.isRemote)HardcoreEnderExpansion.fx.corruptedEnergy(this);
		}
		
		if (worldObj.isRemote){
			if (ticksExisted == 1)worldObj.playSound(posX,posY,posZ,"hardcoreenderexpansion:mob.endermage.attack",1F,0.6F+rand.nextFloat()*1F,false);
			return;
		}
		
		if (ticksExisted % 3 == 0){
			for(EntityLivingBase e:(List<EntityLivingBase>)worldObj.getEntitiesWithinAABB(EntityLivingBase.class,boundingBox.offset(0D,0.5D,0D).expand(1D,1D,1D))){
				if (e.hurtResistantTime == 0 && !(e instanceof IIgnoreEnderGoo)){
					e.attackEntityFrom(DamageSource.magic,2F);
					e.hurtResistantTime = 0;
					e.attackEntityFrom(shootingEntity == null ? DamageSource.generic : DamageSource.causeMobDamage(shootingEntity),ModCommonProxy.opMobs ? 14F : 9F);
					e.hurtResistantTime = 5;
				}
			}
		}
		
		if (ticksExisted > 80 && ticksExisted > 80+rand.nextInt(50))setDead();
	}

	@Override
	protected void onImpact(MovingObjectPosition mop){}
	
	@Override
	protected float getMotionFactor(){
		return 1F;
	}
	
	@Override
	public boolean isBurning(){
		return false;
	}
	
	@Override
	public boolean canBeCollidedWith(){
		return false;
	}
	
	@Override
	public boolean isEntityInvulnerable(){
		return true;
	}
}

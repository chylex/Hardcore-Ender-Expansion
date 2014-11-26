package chylex.hee.entity.projectile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;

public class EntityProjectileCorruptedEnergy extends EntityFireball{
	public EntityProjectileCorruptedEnergy(World world){
		super(world);
		setSize(0F,0F);
	}
	
	public EntityProjectileCorruptedEnergy(World world, EntityLivingBase shooter, double x, double y, double z, Entity target){
		super(world,shooter,1D,1D,1D);
		setSize(0.25F,0.25F);
		setPosition(x,y,z);
		
		Vec3 motionVec = Vec3.createVectorHelper(target.posX-x,target.posY+target.height*0.5F-y,target.posZ-z).normalize();
		motionX = motionVec.xCoord*0.3D+rand.nextGaussian()*0.004D;
		motionY = motionVec.yCoord*0.3D+rand.nextGaussian()*0.004D;
		motionZ = motionVec.zCoord*0.3D+rand.nextGaussian()*0.004D;
		
		accelerationX = accelerationY = accelerationZ = 0D;
	}
	
	@Override
	public void onUpdate(){
		for(int update = 0; update < 5; update++){
			super.onUpdate();
			if (worldObj.isRemote)HardcoreEnderExpansion.fx.corruptedEnergy(this);
		}
		
		if (worldObj.isRemote)return;
		
		if (ticksExisted % 3 == 0){
			for(Object o:worldObj.getEntitiesWithinAABB(EntityLivingBase.class,boundingBox.expand(1D,1D,1D))){
				Entity e = (Entity)o;
				
				if (e.hurtResistantTime == 0){
					e.attackEntityFrom(DamageSource.magic,2.5F);
					e.hurtResistantTime = 0;
					e.attackEntityFrom(shootingEntity == null ? DamageSource.generic : DamageSource.causeMobDamage(shootingEntity),5F);
					e.hurtResistantTime = 8;
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

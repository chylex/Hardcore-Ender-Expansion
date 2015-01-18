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

public class EntityProjectileMinerShot extends EntityFireball{
	public EntityProjectileMinerShot(World world){
		super(world);
		setSize(0.25F,0.25F);
	}
	
	public EntityProjectileMinerShot(World world, EntityLivingBase shooter, double x, double y, double z, Entity target){
		super(world,shooter,1D,1D,1D);
		setSize(0.25F,0.25F);
		setPosition(x,y,z);
		
		Vec3 motionVec = new Vec3(target.posX-x,target.posY+target.height*0.5F-y,target.posZ-z).normalize();
		motionX = motionVec.xCoord*0.35D+rand.nextGaussian()*0.002D;
		motionY = motionVec.yCoord*0.35D+rand.nextGaussian()*0.002D;
		motionZ = motionVec.zCoord*0.35D+rand.nextGaussian()*0.002D;
		
		accelerationX = accelerationY = accelerationZ = 0D;
	}
	
	@Override
	public void onUpdate(){
		for(int update = 0; update < 4; update++){
			super.onUpdate();
			
			if (worldObj.isRemote){
				for(int a = 0; a < 5; a++)worldObj.spawnParticle("mobSpell",posX+(rand.nextDouble()-0.5D)*0.15D,posY+(rand.nextDouble()-0.5D)*0.15D,posZ+(rand.nextDouble()-0.5D)*0.15D,0.9D,0.6D,0D);
				if (rand.nextBoolean())HardcoreEnderExpansion.fx.flame(worldObj,posX+(rand.nextDouble()-0.5D)*0.15D,posY+0.2D,posZ+(rand.nextDouble()-0.5D)*0.15D,10);
			}
		}
		
		if (ticksExisted > 60)setDead();
	}

	@Override
	protected void onImpact(MovingObjectPosition mop){
		if (ticksExisted < 4)return;
		
		if (worldObj.isRemote){
			for(int a = 0; a < 42; a++)HardcoreEnderExpansion.fx.flame(worldObj,posX,posY+0.2D,posZ,(rand.nextDouble()-0.5D)*0.4D,(rand.nextDouble()-0.5D)*0.4D,(rand.nextDouble()-0.5D)*0.4D,7+rand.nextInt(12));
		}
		else{
			if (mop.entityHit != null){
				mop.entityHit.attackEntityFrom(DamageSource.magic,3F);
				mop.entityHit.hurtResistantTime = 0;
				mop.entityHit.attackEntityFrom(shootingEntity == null ? DamageSource.generic : DamageSource.causeMobDamage(shootingEntity),13F);
				mop.entityHit.setFire(4);
			}
			else{
				List<EntityLivingBase> entities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class,boundingBox.expand(1.5D,1.5D,1.5D));
				for(EntityLivingBase entity:entities)entity.setFire(4);
			}
		}
		
		setDead();
	}
	
	@Override
	protected float getMotionFactor(){
		return 1F;
	}
	
	@Override
	public boolean isBurning(){
		return true;
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

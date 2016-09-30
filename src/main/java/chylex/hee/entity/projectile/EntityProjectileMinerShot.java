package chylex.hee.entity.projectile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.system.abstractions.entity.EntitySelector;

public class EntityProjectileMinerShot extends EntityFireball{
	public EntityProjectileMinerShot(World world){
		super(world);
		setSize(0.25F, 0.25F);
	}
	
	public EntityProjectileMinerShot(World world, EntityLivingBase shooter, double x, double y, double z, Entity target){
		super(world, shooter, 1D, 1D, 1D);
		setSize(0.25F, 0.25F);
		setPosition(x, y, z);
		
		Vec3 motionVec = Vec3.createVectorHelper(target.posX-x, target.posY+target.height*0.5F-y, target.posZ-z).normalize();
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
				for(int a = 0; a < 5; a++)HardcoreEnderExpansion.fx.global("spell", posX+(rand.nextDouble()-0.5D)*0.15D, posY+(rand.nextDouble()-0.5D)*0.15D, posZ+(rand.nextDouble()-0.5D)*0.15D, 0.9D, 0.6D, 0D);
				if (rand.nextBoolean())HardcoreEnderExpansion.fx.flame(posX+(rand.nextDouble()-0.5D)*0.15D, posY+0.2D, posZ+(rand.nextDouble()-0.5D)*0.15D, 10);
			}
		}
		
		if (ticksExisted > 60)setDead();
	}

	@Override
	protected void onImpact(MovingObjectPosition mop){
		if (ticksExisted < 4)return;
		
		if (worldObj.isRemote){
			for(int a = 0; a < 42; a++)HardcoreEnderExpansion.fx.flame(posX, posY+0.2D, posZ, (rand.nextDouble()-0.5D)*0.4D, (rand.nextDouble()-0.5D)*0.4D, (rand.nextDouble()-0.5D)*0.4D, 7+rand.nextInt(12));
		}
		else{
			if (mop.entityHit != null){
				// TODO MultiDamage.from(shootingEntity).addMagic(3F).addScaled(ModCommonProxy.opMobs ? 18F : 13F).attack(mop.entityHit);
				mop.entityHit.setFire(4);
			}
			else{
				for(EntityLivingBase entity:EntitySelector.living(worldObj, boundingBox.expand(1.5D, 1.5D, 1.5D)))entity.setFire(4);
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

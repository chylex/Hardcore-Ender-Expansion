package chylex.hee.entity.projectile;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import chylex.hee.system.util.DragonUtil;

public class EntityProjectileDragonFireball extends EntityFireball{
	public float power = 2.5F;

	public EntityProjectileDragonFireball(World world){
		super(world);
		setSize(1F,1F);
	}

	public EntityProjectileDragonFireball(World world, EntityLiving shooter, double xDiff, double yDiff, double zDiff, float speedMp, boolean random, float power){
		super(world,shooter,xDiff,yDiff,zDiff);
		xDiff += rand.nextGaussian()*(random ? 0.8D : 0.2D);
		yDiff += rand.nextGaussian()*(random ? 0.8D : 0.2D);
		zDiff += rand.nextGaussian()*(random ? 0.8D : 0.2D);
		double dist = MathHelper.sqrt_double(xDiff*xDiff+yDiff*yDiff+zDiff*zDiff);
		accelerationX = (xDiff/dist)*0.21D*speedMp;
		accelerationY = (yDiff/dist)*0.21D*speedMp;
		accelerationZ = (zDiff/dist)*0.21D*speedMp;
		this.power = power;
	}

	@Override
	public void onUpdate(){
		super.onUpdate();
		if (ticksExisted > 100 && Math.abs(motionX) < 0.01D && Math.abs(motionY) < 0.01D && Math.abs(motionZ) < 0.01D)onImpact(null);
	}

	@Override
	protected void onImpact(MovingObjectPosition mop){
		if (!worldObj.isRemote){
			setDead();
			if (shootingEntity == null)DragonUtil.createMobExplosion(worldObj,posX,posY,posZ,power,true);
			else DragonUtil.createMobExplosion(shootingEntity,posX,posY,posZ,power,true);
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if (source.isFireDamage() || isDead)return false;
		
		setBeenAttacked();
		setDead();
		
		if (shootingEntity == null)DragonUtil.createMobExplosion(worldObj,posX,posY,posZ,power,true);
		else DragonUtil.createMobExplosion(shootingEntity,posX,posY,posZ,power*0.7F,true);
		
		if (source.getEntity() != null)source.getEntity().setFire(3);
		
		return true;
	}

	@Override
	public boolean isBurning(){
		return true;
	}
}
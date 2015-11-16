package chylex.hee.entity.projectile;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import chylex.hee.system.abstractions.Explosion;
import chylex.hee.system.util.MathUtil;

public class EntityProjectileDragonFireball extends EntityFireball{
	private final float power;

	public EntityProjectileDragonFireball(World world){
		super(world);
		setSize(1F,1F);
		this.power = 2.5F;
	}

	public EntityProjectileDragonFireball(World world, EntityLiving shooter, double xDiff, double yDiff, double zDiff, float speedMp, boolean random, float power){
		super(world,shooter,xDiff,yDiff,zDiff);
		xDiff += rand.nextGaussian()*(random ? 0.8D : 0.2D);
		yDiff += rand.nextGaussian()*(random ? 0.8D : 0.2D);
		zDiff += rand.nextGaussian()*(random ? 0.8D : 0.2D);
		
		double dist = MathUtil.distance(xDiff,yDiff,zDiff);
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
			Explosion explosion = new Explosion(worldObj,posX,posY,posZ,power,shootingEntity);
			explosion.spawnFire = true;
			explosion.honorMobGriefingRule = true;
			explosion.trigger();
		}
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if (source.isFireDamage() || isDead)return false;
		
		setBeenAttacked();
		setDead();
		
		Explosion explosion = new Explosion(worldObj,posX,posY,posZ,power*0.7F,shootingEntity);
		explosion.spawnFire = true;
		explosion.honorMobGriefingRule = true;
		explosion.trigger();
		
		if (source.getEntity() != null)source.getEntity().setFire(3);
		
		return true;
	}

	@Override
	public boolean isBurning(){
		return true;
	}
}
package chylex.hee.entity.projectile;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import chylex.hee.proxy.ModCommonProxy;

public class EntityProjectileDragonFreezeball extends EntityFireball{
	public EntityProjectileDragonFreezeball(World world){
		super(world);
		setSize(1F,1F);
	}

	public EntityProjectileDragonFreezeball(World world, EntityLiving shooter, double xDiff, double yDiff, double zDiff, float speedMp, boolean random){
		super(world,shooter,xDiff,yDiff,zDiff);
		xDiff += rand.nextGaussian()*(random ? 0.8D : 0.2D);
		yDiff += rand.nextGaussian()*(random ? 0.8D : 0.2D);
		zDiff += rand.nextGaussian()*(random ? 0.8D : 0.2D);
		double dist = MathHelper.sqrt_double(xDiff*xDiff+yDiff*yDiff+zDiff*zDiff);
		accelerationX = (xDiff/dist)*0.21D*speedMp;
		accelerationY = (yDiff/dist)*0.21D*speedMp;
		accelerationZ = (zDiff/dist)*0.21D*speedMp;
	}

	@Override
	public void onUpdate(){
		super.onUpdate();
		if (ticksExisted > 100 && Math.abs(motionX) < 0.01D && Math.abs(motionY) < 0.01D && Math.abs(motionZ) < 0.01D)onImpact(null);
	}

	@Override
	protected void onImpact(MovingObjectPosition mop){
		if (!worldObj.isRemote){
			PotionEffect effect = new PotionEffect(2,ModCommonProxy.opMobs ? 200 : 140,2);
			for(Object o:worldObj.getEntitiesWithinAABB(EntityLivingBase.class,boundingBox.expand(5D,5D,5D))){
				EntityLivingBase e = (EntityLivingBase)o;
				if (e.getDistanceSqToEntity(this) < 5D)e.addPotionEffect(effect);
			}
			
			setDead();
		}
		
		for(int i = 0; i < 12; i++)worldObj.spawnParticle("snowballpoof",posX+5F*(rand.nextFloat()-0.5F),posY+5F*(rand.nextFloat()-0.5F),posZ+5F*(rand.nextFloat()-0.5F),0D,0D,0D);
		worldObj.playSoundEffect(posX,posY,posZ,"hardcoreenderexpansion:environment.random.freeze",1.0F,rand.nextFloat()*0.1F+0.9F);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if (source.isFireDamage() || isDead)return false;
		
		setBeenAttacked();
		onImpact(null);
		return source.getEntity() != null;
	}

	@Override
	public boolean isBurning(){
		return false;
	}
}
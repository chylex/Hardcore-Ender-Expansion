package chylex.hee.entity.projectile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.boss.EntityMiniBossFireFiend;
import chylex.hee.entity.projectile.EntityProjectileGolemFireball.FieryExplosion;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.util.MathUtil;

public class EntityProjectileFiendFireball extends EntityLargeFireball{
	private int fiendId;
	private double centerX, centerZ;
	private float ang;
	public byte timer;
	
	public EntityProjectileFiendFireball(World world){
		super(world);
		setSize(0.2F,0.2F);
	}
	
	public EntityProjectileFiendFireball(World world, EntityLivingBase shooter, double x, double y, double z, double ang, int timer){
		super(world,shooter,0D,0D,0D);
		setPosition(x,y,z);
		setSize(0.2F,0.2F);
		this.fiendId = shooter.getEntityId();
		this.centerX = x;
		this.centerZ = z;
		this.ang = (float)MathUtil.toRad(ang);
		this.timer = (byte)timer;
	}
	
	@Override
	protected void entityInit(){
		super.entityInit();
		dataWatcher.addObject(16,Byte.valueOf((byte)0));
		dataWatcher.addObject(17,0F);
		dataWatcher.addObject(18,0);
	}
	
	public void updateCenter(EntityMiniBossFireFiend fiend){
		this.centerX = fiend.posX;
		this.centerZ = fiend.posZ;
	}
	
	public void shootAt(EntityPlayer player){
		worldObj.playAuxSFXAtEntity(null,1008,(int)posX,(int)posY,(int)posZ,0);
		
		if (player == null)setDead();
		else{
			double diffX = player.posX-posX, diffY = player.posY-posY, diffZ = player.posZ-posZ;
			
			diffX += rand.nextGaussian()*0.1D;
			diffY += rand.nextGaussian()*0.1D;
			diffZ += rand.nextGaussian()*0.1D;
			
			double dist = MathUtil.distance(diffX,diffY,diffZ);
			accelerationX = diffX/dist*0.1D;
			accelerationY = diffY/dist*0.1D;
			accelerationZ = diffZ/dist*0.1D;
		}
	}
	
	@Override
	public void onUpdate(){
		if (ticksExisted == 1){
			if (!worldObj.isRemote){
				dataWatcher.updateObject(16,timer);
				dataWatcher.updateObject(17,ang);
				dataWatcher.updateObject(18,fiendId);
			}
			else{
				for(int a = 0; a < 5; a++)HardcoreEnderExpansion.fx.flame(worldObj,posX+(rand.nextDouble()-0.5D)*0.3D,posY+(rand.nextDouble()-0.5D)*0.3D,posZ+(rand.nextDouble()-0.5D)*0.3D,7+rand.nextInt(6));
				timer = dataWatcher.getWatchableObjectByte(16);
				ang = dataWatcher.getWatchableObjectFloat(17);
				fiendId = dataWatcher.getWatchableObjectInt(18);
			}
		}
		
		if (timer > 0 && --timer > 0){
			if (worldObj.isRemote){
				Entity fiend = worldObj.getEntityByID(fiendId);
				
				if (fiend != null){
					centerX = fiend.posX;
					centerZ = fiend.posZ;
				}
			}
			
			onEntityUpdate();
			setPosition(centerX+MathHelper.cos(ang)*2.5D,posY,centerZ+MathHelper.sin(ang)*2.5D);
			ang += 0.22F;
		}
		else super.onUpdate();
	}

	@Override
	protected void onImpact(MovingObjectPosition mop){
		if (!worldObj.isRemote){
			if (mop.entityHit != null)mop.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this,shootingEntity),ModCommonProxy.opMobs ? 12F : 7F);

			Explosion explosion = new FieryExplosion(worldObj,shootingEntity,posX,posY,posZ,ModCommonProxy.opMobs ? 3.4F : 2.8F);
			explosion.doExplosionA();
			explosion.doExplosionB(true);
			
			setDead();
		}
	}
	
	@Override
	public boolean isBurning(){
		return true;
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		setDead();
	}
}

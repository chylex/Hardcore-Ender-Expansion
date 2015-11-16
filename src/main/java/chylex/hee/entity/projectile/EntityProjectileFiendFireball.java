package chylex.hee.entity.projectile;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.entity.boss.EntityMiniBossFireFiend;
import chylex.hee.entity.projectile.EntityProjectileGolemFireball.FieryExplosion;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C14FiendFireball;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityProjectileFiendFireball extends EntityLargeFireball{
	private double centerX, centerZ;
	private float ang;
	public byte timer;
	
	@SideOnly(Side.CLIENT)
	public double actualPosX, actualPosZ, prevActualPosX, prevActualPosZ;
	
	public EntityProjectileFiendFireball(World world){
		super(world);
		setSize(0.2F,0.2F);
	}
	
	public EntityProjectileFiendFireball(World world, EntityLivingBase shooter, double x, double y, double z, double ang, int timer){
		super(world,shooter,0D,0D,0D);
		setPosition(x,y,z);
		setSize(0.2F,0.2F);
		this.centerX = x;
		this.centerZ = z;
		this.ang = (float)MathUtil.toRad(ang);
		this.timer = (byte)timer;
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
		if (ticksExisted == 3 && worldObj.isRemote){
			for(int a = 0; a < 3; a++)HardcoreEnderExpansion.fx.flame(actualPosX+(rand.nextDouble()-0.5D)*0.1D,posY+(rand.nextDouble()-0.5D)*0.1D,actualPosZ+(rand.nextDouble()-0.5D)*0.1D,4+rand.nextInt(6));
		}
		
		if (!worldObj.isRemote)PacketPipeline.sendToAllAround(this,128D,new C14FiendFireball(this,posX,posZ));
		
		if (!worldObj.isRemote && timer > 0 && --timer > 0){
			onEntityUpdate();
			setPosition(centerX+MathHelper.cos(ang)*2.5D,posY,centerZ+MathHelper.sin(ang)*2.5D);
			ang += 0.22F;
		}
		else if (worldObj.isRemote){
			onEntityUpdate();
			simulateClientUpdate();
		}
		else super.onUpdate();
	}
	
	private void simulateClientUpdate(){
		posX = actualPosX;
		posZ = actualPosZ;
		Vec3 vecPos = Vec3.createVectorHelper(posX,posY,posZ);
		Vec3 vecPosNext = Vec3.createVectorHelper(posX+motionX,posY+motionY,posZ+motionZ);
		MovingObjectPosition mop = worldObj.rayTraceBlocks(vecPos,vecPosNext);
		vecPos = Vec3.createVectorHelper(posX,posY,posZ);
		vecPosNext = Vec3.createVectorHelper(posX+motionX,posY+motionY,posZ+motionZ);

		if (mop != null)vecPosNext = Vec3.createVectorHelper(mop.hitVec.xCoord,mop.hitVec.yCoord,mop.hitVec.zCoord);

		Entity closest = null;
		double minDist = Double.MAX_VALUE;
		
		for(Entity entity:(List<Entity>)worldObj.getEntitiesWithinAABBExcludingEntity(this,boundingBox.addCoord(motionX,motionY,motionZ).expand(1D,1D,1D))){
			if (entity.canBeCollidedWith()){
				AxisAlignedBB aabb = entity.boundingBox.expand(0.3F,0.3F,0.3F);
				MovingObjectPosition tmp = aabb.calculateIntercept(vecPos,vecPosNext);
				
				if (tmp != null){
					double dist = vecPos.distanceTo(tmp.hitVec);
					
					if (dist < minDist){
						minDist = dist;
						closest = entity;
					}
				}
			}
		}

		if (closest != null)mop = new MovingObjectPosition(closest);
		if (mop != null)onImpact(mop);

		posX += motionX;
		posY += motionY;
		posZ += motionZ;
		rotationYaw = (float)(MathUtil.toDeg(Math.atan2(motionZ,motionX)))+90F;
		rotationPitch = (float)MathUtil.toDeg(Math.atan2(MathUtil.distance(motionX,motionZ),motionY))-90F;

		while(rotationPitch-prevRotationPitch < -180F)prevRotationPitch -= 360F;
		while(rotationPitch-prevRotationPitch >= 180F)prevRotationPitch += 360F;
		while(rotationYaw-prevRotationYaw < -180F)prevRotationYaw -= 360F;
		while(rotationYaw-prevRotationYaw >= 180F)prevRotationYaw += 360F;

		rotationPitch = prevRotationPitch+(rotationPitch-prevRotationPitch)*0.2F;
		rotationYaw = prevRotationYaw+(rotationYaw-prevRotationYaw)*0.2F;
		float motFactor = getMotionFactor();

		if (isInWater()){
			for(int a = 0; a < 4; ++a)worldObj.spawnParticle("bubble",posX-motionX*0.25F,posY-motionY*0.25F,posZ-motionZ*0.25F,motionX,motionY,motionZ);
			motFactor = 0.8F;
		}

		motionX += accelerationX;
		motionY += accelerationY;
		motionZ += accelerationZ;
		motionX *= motFactor;
		motionY *= motFactor;
		motionZ *= motFactor;
		worldObj.spawnParticle("smoke",posX,posY+0.5D,posZ,0D,0D,0D);
		setPosition(posX,posY,posZ);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void setPositionAndRotation2(double x, double y, double z, float rotationYaw, float rotationPitch, int eger){
		super.setPositionAndRotation2(actualPosX,y,actualPosZ,rotationYaw,rotationPitch,eger);
	}

	@Override
	protected void onImpact(MovingObjectPosition mop){
		if (mop.entityHit instanceof EntityMiniBossFireFiend || mop.entityHit instanceof EntityProjectileFiendFireball || worldObj.isRemote)return;
		
		if (mop.entityHit != null)mop.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this,shootingEntity),ModCommonProxy.opMobs ? 9F : 4F);
		
		new FieryExplosion(worldObj,posX,posY,posZ,ModCommonProxy.opMobs ? 3.5F : 2.7F,this,shootingEntity).trigger();
		setDead();
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

package chylex.hee.entity.projectile;
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
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C69FiendFuckball;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityProjectileFiendFireball extends EntityLargeFireball{
	private int fiendId;
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
		this.fiendId = shooter.getEntityId();
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
		if (ticksExisted == 1 && worldObj.isRemote){
			for(int a = 0; a < 10; a++)HardcoreEnderExpansion.fx.flame(worldObj,posX+(rand.nextDouble()-0.5D)*0.3D,posY+(rand.nextDouble()-0.5D)*0.3D,posZ+(rand.nextDouble()-0.5D)*0.3D,7+rand.nextInt(6));
		}
		
		if (!worldObj.isRemote)PacketPipeline.sendToAllAround(this,128D,new C69FiendFuckball(this,timer > 0 ? centerX+MathHelper.cos(ang)*2.5D : posX,timer > 0 ? centerZ+MathHelper.sin(ang)*2.5D : posZ));
		
		if (!worldObj.isRemote && timer > 0 && --timer > 0){
			onEntityUpdate();
			setPosition(centerX+MathHelper.cos(ang)*2.5D,posY,centerZ+MathHelper.sin(ang)*2.5D);
			ang += 0.22F;
		}
		else if (worldObj.isRemote){
			onEntityUpdate();
			simulateClientUpdate();
			if (worldObj.isRemote)setPosition(actualPosX,posY,actualPosZ);
		}
		else super.onUpdate();
	}
	
	private void simulateClientUpdate(){
		// TODO
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
		return false;
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		setDead();
	}
}

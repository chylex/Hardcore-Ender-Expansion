package chylex.hee.entity.mob;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import chylex.hee.api.interfaces.IIgnoreEnderGoo;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.weather.EntityWeatherLightningBoltSafe;
import chylex.hee.mechanics.misc.Baconizer;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.util.MathUtil;

public class EntityMobVampiricBat extends EntityBat implements IIgnoreEnderGoo{
	public Entity target;
	
	public EntityMobVampiricBat(World world){
		super(world);
	}
	
	@Override
	public void onLivingUpdate(){
		super.onLivingUpdate();
		for(int a = 0; a < 3; a++)worldObj.spawnParticle(EnumParticleTypes.PORTAL,posX,posY-0.45D,posZ,0D,0D,0D);
	}
	
	@Override
	protected void updateAITasks(){
		super.updateAITasks();

		if (target == null || (!worldObj.isAirBlock(MathUtil.floor(target.posX),MathUtil.floor(target.posY),MathUtil.floor(target.posZ)) || target.isDead || target.posY < 1)){
			if ((target = worldObj.getClosestPlayerToEntity(this,32D)) == null){
				setDead();
				return;
			}
		}

		double xDiff = target.posX+0.5D-posX;
		double yDiff = target.posY+0.1D-posY;
		double zDiff = target.posZ+0.5D-posZ;
		motionX += (Math.signum(xDiff)*0.5D-motionX)*0.1D;
		motionY += (Math.signum(yDiff)*0.7D-motionY)*0.1D;
		motionZ += (Math.signum(zDiff)*0.5D-motionZ)*0.1D;
		rotationYaw += MathHelper.wrapAngleTo180_float((float)(Math.atan2(motionZ,motionX)*180D/Math.PI)-90F-rotationYaw);
		moveForward = 0.5F;
		
	}
	
	@Override
	public boolean canBePushed(){
		return true;
	}
	
	@Override
	protected void collideWithNearbyEntities(){
		List<Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(this,boundingBox.expand(0.2D,0D,0.2D));
		
		if (list != null){
			for(Entity entity:list){
				if (entity.canBePushed())collideWithEntity(entity);
			}
		}
	}

	@Override
	protected void collideWithEntity(Entity entity){
		entity.applyEntityCollision(this);
		
		if (entity instanceof EntityPlayer){
			if (!worldObj.isRemote){
				EntityPlayer player = (EntityPlayer)entity;
				player.attackEntityFrom(DamageSource.causeMobDamage(this),ModCommonProxy.opMobs ? 4F : 2F);
				
				for(Object o:worldObj.loadedEntityList){
					if (o instanceof EntityBossDragon){
						EntityBossDragon dragon = (EntityBossDragon)o;
						dragon.heal(1);
						worldObj.addWeatherEffect(new EntityWeatherLightningBoltSafe(worldObj,dragon.posX,dragon.posY+dragon.height*0.25F,dragon.posZ));
						break;
					}
				}
				
				setDead();
			}
			
			for(int a = 0; a < 6; a++){
				worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE,posX,posY+0.4D,posZ,0D,0D,0D);
				worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,posX,posY+0.4D,posZ,0D,0D,0D);
			}
		}
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if (!worldObj.isRemote)setDead();
		
		for(int a = 0; a < 6; a++){
			worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE,posX,posY+0.4D,posZ,0D,0D,0D);
			worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,posX,posY+0.4D,posZ,0D,0D,0D);
		}
		
		return true;
	}
	
	@Override
	protected float getSoundVolume(){
		return 0.4F;
	}
	
	@Override
	public void setIsBatHanging(boolean isHanging){}

	@Override
	public boolean getIsBatHanging(){
		return false;
	}
	
	@Override
	public String getName(){
		return hasCustomName() ? getCustomNameTag() : StatCollector.translateToLocal(Baconizer.mobName("entity.vampireBat.name"));
	}
}

package chylex.hee.entity.mob;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.weather.EntityWeatherLightningBoltSafe;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.mechanics.knowledge.util.ObservationUtil;
import chylex.hee.proxy.ModCommonProxy;

public class EntityMobVampiricBat extends EntityBat{
	public Entity target = null;
	
	public EntityMobVampiricBat(World world){
		super(world);
	}
	
	@Override
	public void onLivingUpdate(){
		super.onLivingUpdate();
		for(int a = 0; a < 3; a++)worldObj.spawnParticle("portal",posX,posY-0.45D,posZ,0D,0D,0D);
	}
	
	@Override
	protected void updateAITasks(){
		super.updateAITasks();

		if (target == null || (!worldObj.isAirBlock((int)Math.floor(target.posX),(int)Math.floor(target.posY),(int)Math.floor(target.posZ)) || target.isDead || target.posY<1)){
			target = worldObj.getClosestPlayerToEntity(this,32D);
			if (target == null){
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
		rotationYaw += MathHelper.wrapAngleTo180_float((float)(Math.atan2(motionZ,motionX)*180D/Math.PI)-90F-rotationYaw);;
		moveForward = 0.5F;
		
	}
	
	@Override
	public boolean canBePushed(){
		return true;
	}
	
	@Override
	protected void collideWithNearbyEntities(){
		List<?> list = worldObj.getEntitiesWithinAABBExcludingEntity(this,boundingBox.expand(0.2D,0D,0.2D));
		if (list != null){
			for(Object o:list){
				Entity entity = (Entity)o;
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
				player.attackEntityFrom(DamageSource.causeMobDamage(this),ModCommonProxy.opMobs?4F:2F);
				
				KnowledgeRegistrations.VAMPIRIC_BAT.tryUnlockFragment(player,0.65F);
				for(EntityPlayer observer:ObservationUtil.getAllObservers(player,10D))KnowledgeRegistrations.VAMPIRIC_BAT.tryUnlockFragment(observer,0.25F);
				
				for(Object o:worldObj.loadedEntityList){
					if (o instanceof EntityBossDragon){
						EntityBossDragon dragon = (EntityBossDragon)o;
						dragon.heal(1);
						worldObj.addWeatherEffect(new EntityWeatherLightningBoltSafe(worldObj,dragon.posX,dragon.posY,dragon.posZ));
						break;
					}
				}
				
				setDead();
			}
			
			for(int a = 0; a < 6; a++){
				worldObj.spawnParticle("largesmoke",posX,posY+0.4D,posZ,0D,0D,0D);
				worldObj.spawnParticle("smoke",posX,posY+0.4D,posZ,0D,0D,0D);
			}
		}
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount){
		if (!worldObj.isRemote){
			for(EntityPlayer observer:ObservationUtil.getAllObservers(this,10D))KnowledgeRegistrations.VAMPIRIC_BAT.tryUnlockFragment(observer,0.14F);
			setDead();
		}
		
		for(int a = 0; a < 6; a++){
			worldObj.spawnParticle("largesmoke",posX,posY+0.4D,posZ,0D,0D,0D);
			worldObj.spawnParticle("smoke",posX,posY+0.4D,posZ,0D,0D,0D);
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
	public String getCommandSenderName(){
		return StatCollector.translateToLocal("entity.vampireBat.name");
	}
}

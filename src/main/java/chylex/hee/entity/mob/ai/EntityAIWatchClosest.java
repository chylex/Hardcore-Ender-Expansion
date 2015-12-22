package chylex.hee.entity.mob.ai;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAIWatchClosest<T extends EntityLivingBase> extends EntityAbstractAIWatching{
	private final Class<T> targetClass;
	private float checkChance = 1F/50F;
	private double checkDistance = 8D;
	private double maxDistance = 12D;
	private int watchCooldown = 80;
	private IWatchTimerSupplier<T> timerFunction = target -> 40+entity.getRNG().nextInt(40);
	
	private int stopTimer;
	private int cooldownTimer;
	
	public EntityAIWatchClosest(EntityCreature owner, Class<T> targetClass){
		super(owner);
		this.targetClass = targetClass;
	}
	
	public EntityAIWatchClosest<T> setChancePerTick(float chance){
		this.checkChance = chance;
		return this;
	}
	
	public EntityAIWatchClosest<T> setCheckDistance(double dist){
		this.checkDistance = dist;
		return this;
	}
	
	public EntityAIWatchClosest<T> setMaxDistance(double dist){
		this.maxDistance = dist;
		return this;
	}
	
	public EntityAIWatchClosest<T> setWatchTime(IWatchTimerSupplier<T> func){
		this.timerFunction = func;
		return this;
	}
	
	public EntityAIWatchClosest<T> setWatchCooldown(int cooldown){
		this.watchCooldown = cooldown;
		return this;
	}
	
	@Override
	public boolean shouldExecute(){
		if (cooldownTimer > 0 && --cooldownTimer > 0)return false;
		if (entity.getRNG().nextFloat() >= checkChance)return false;
		
		if (targetClass == EntityPlayer.class){
			watchedEntity = entity.worldObj.getClosestPlayerToEntity(entity,checkDistance);
		}
		else{
			watchedEntity = (EntityLivingBase)entity.worldObj.findNearestEntityWithinAABB(targetClass,entity.boundingBox.expand(checkDistance,4D,checkDistance),entity);
			if (watchedEntity != null && entity.getDistanceSqToEntity(watchedEntity) > checkDistance*checkDistance)watchedEntity = null;
		}
		
		return watchedEntity != null;
	}
	
	@Override
	public void startExecuting(){
		super.startExecuting();
		stopTimer = timerFunction.getTimer((T)watchedEntity);
	}
	
	@Override
	public boolean continueExecuting(){
		return super.continueExecuting() && stopTimer > 0 && entity.getDistanceSqToEntity(watchedEntity) <= maxDistance*maxDistance;
	}
	
	@Override
	public void updateTask(){
		super.updateTask();
		--stopTimer;
	}
	
	@Override
	public void resetTask(){
		super.resetTask();
		cooldownTimer = watchCooldown;
	}
	
	@Override
	protected EntityLivingBase findTarget(){
		return watchedEntity;
	}
	
	public static interface IWatchTimerSupplier<T extends EntityLivingBase>{
		int getTimer(T target);
	}
}

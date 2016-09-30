package chylex.hee.entity.mob.ai.base;
import chylex.hee.entity.mob.ai.AIUtil;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public abstract class EntityAIAbstractWatching extends EntityAIBase{
	protected final EntityCreature entity;
	protected EntityLivingBase watchedEntity;
	
	public EntityAIAbstractWatching(EntityCreature owner){
		this.entity = owner;
		setMutexBits(AIUtil.mutexOverrideWatching);
	}
	
	protected abstract EntityLivingBase findTarget();
	
	protected float getHeadRotationSpeed(){
		return 10F;
	}
	
	@Override
	public boolean shouldExecute(){
		return (watchedEntity = findTarget()) != null;
	}
	
	@Override
	public boolean continueExecuting(){
		if (!watchedEntity.isEntityAlive())return false;
		
		EntityLivingBase newTarget = findTarget();
		if (newTarget == null)return false;
		
		watchedEntity = newTarget;
		return true;
	}
	
	@Override
	public void updateTask(){
		entity.getLookHelper().setLookPosition(watchedEntity.posX, watchedEntity.posY+watchedEntity.getEyeHeight(), watchedEntity.posZ, getHeadRotationSpeed(), entity.getVerticalFaceSpeed());
	}
	
	@Override
	public void resetTask(){
		watchedEntity = null;
	}
}

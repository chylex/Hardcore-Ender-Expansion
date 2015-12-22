package chylex.hee.entity.mob.ai.target;
import java.util.function.Predicate;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.player.EntityPlayer;
import chylex.hee.entity.mob.ai.base.EntityAIAbstractTarget;

/**
 * Special task used by {@link EntityAIAbstractTarget} to determine when to stop attacking a target.
 */
public class EntityAIResetTarget extends EntityAITarget{
	private Predicate<EntityLivingBase> conditions = target -> target.isEntityAlive();
	
	public EntityAIResetTarget(EntityCreature owner){
		super(owner,false,false);
	}

	@Override
	public boolean shouldExecute(){
		return false;
	}
	
	public EntityAIResetTarget addCondition(Predicate<EntityLivingBase> condition){
		conditions = conditions.and(condition);
		return this;
	}
	
	public EntityAIResetTarget stopIfTooFar(){
		return addCondition(target -> {
			double maxDist = getTargetDistance();
			return taskOwner.getDistanceSqToEntity(target) > maxDist*maxDist;
		});
	}
	
	public EntityAIResetTarget stopIfTooFar(final double maxDistance){
		final double maxDistSq = maxDistance*maxDistance;
		return addCondition(target -> taskOwner.getDistanceSqToEntity(target) > maxDistSq);
	}
	
	public EntityAIResetTarget stopIfLostSight(final int ticks){
		return addCondition(new Predicate<EntityLivingBase>(){
			private int limiter;
			
			@Override
			public boolean test(EntityLivingBase target){
				if (taskOwner.getEntitySenses().canSee(target))limiter = 0;
				else if (++limiter >= ticks)return false;
				
				return true;
			}
		});
	}
	
	public EntityAIResetTarget stopIfCreative(){
		return addCondition(target -> !(target instanceof EntityPlayer) || !((EntityPlayer)target).capabilities.isCreativeMode);
	}
	
	public EntityAIResetTarget setVanilla(boolean checkSight){
		stopIfTooFar();
		if (checkSight)stopIfLostSight(60);
		stopIfCreative();
		return this;
	}
	
	@Override
	public boolean continueExecuting(){
		EntityLivingBase target = taskOwner.getAttackTarget();
		return target != null && conditions.test(target);
	}
}

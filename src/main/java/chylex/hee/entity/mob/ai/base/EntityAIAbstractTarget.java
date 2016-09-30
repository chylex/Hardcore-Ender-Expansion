package chylex.hee.entity.mob.ai.base;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import chylex.hee.entity.mob.ai.AIUtil;
import chylex.hee.entity.mob.ai.target.EntityAIResetTarget;

public abstract class EntityAIAbstractTarget extends EntityAITarget{
	private final EntityAIResetTarget reset;
	
	public EntityAIAbstractTarget(EntityCreature owner, boolean shouldCheckSight, boolean nearbyOnly){
		super(owner, shouldCheckSight, nearbyOnly);
		setMutexBits(AIUtil.mutexTarget);
		
		if (taskOwner.targetTasks.taskEntries.isEmpty() || !(((EntityAITaskEntry)taskOwner.targetTasks.taskEntries.get(0)).action instanceof EntityAIResetTarget)){
			throw new IllegalStateException("Target tasks require EntityAIResetTarget as the first task.");
		}
		
		reset = (EntityAIResetTarget)((EntityAITaskEntry)taskOwner.targetTasks.taskEntries.get(0)).action;
	}
	
	protected abstract @Nullable EntityLivingBase findNewTarget();

	@Override
	public final boolean shouldExecute(){
		EntityLivingBase newTarget = findNewTarget();
		
		if (newTarget != null){
			taskOwner.setAttackTarget(newTarget);
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean continueExecuting(){
		return reset.continueExecuting();
	}
}

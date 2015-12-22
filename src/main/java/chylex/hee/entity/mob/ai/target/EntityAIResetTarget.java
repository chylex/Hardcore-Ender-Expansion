package chylex.hee.entity.mob.ai.target;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAITarget;
import chylex.hee.entity.mob.ai.base.EntityAIAbstractTarget;

/**
 * Special task used by {@link EntityAIAbstractTarget} to determine when to stop attacking a target.
 */
public class EntityAIResetTarget extends EntityAITarget{
	public EntityAIResetTarget(EntityCreature owner){
		super(owner,false,false);
	}

	@Override
	public boolean shouldExecute(){
		return false;
	}
	
	@Override
	public boolean continueExecuting(){
		// TODO
		return taskOwner.getAttackTarget() != null && taskOwner.getAttackTarget().isEntityAlive();//super.continueExecuting();
	}
}

package chylex.hee.entity.mob.ai.target;
import java.util.UUID;
import java.util.function.IntPredicate;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import chylex.hee.entity.mob.ai.AIUtil;

/**
 * Attacks the last attacker the entity was hurt by.
 * If the entity already has a target, it will only switch if the new attacker damages the entity consecutively a set amount of times within a set time period.
 */
public class EntityAIHurtByTargetConsecutively extends EntityAITarget{
	private UUID lastAttackingEntity;
	private int lastAttackingEntityCounter;
	private int lastAttackingEntityTick;
	
	private IntPredicate counterPredicate = n -> true;
	private int counterTimer = 100;
	
	public EntityAIHurtByTargetConsecutively(EntityCreature owner){
		super(owner,false);
		setMutexBits(AIUtil.mutexTarget);
	}
	
	public EntityAIHurtByTargetConsecutively setCounter(IntPredicate predicate){
		this.counterPredicate = predicate;
		return this;
	}
	
	public EntityAIHurtByTargetConsecutively setTimer(int timer){
		this.counterTimer = timer;
		return this;
	}

	@Override
	public boolean shouldExecute(){
		EntityLivingBase revengeTarget = taskOwner.getAITarget();
		if (revengeTarget == null)return false;
		
		if (taskOwner.getAttackTarget() == null)return true;
		
		if ((lastAttackingEntityTick == 0 || taskOwner.ticksExisted-lastAttackingEntityTick <= counterTimer) && revengeTarget.getUniqueID().equals(lastAttackingEntity)){
			if (counterPredicate.test(++lastAttackingEntityCounter)){
				lastAttackingEntityCounter = 0;
				lastAttackingEntityTick = 0;
				return true;
			}
		}
		else{
			lastAttackingEntity = revengeTarget.getUniqueID();
			lastAttackingEntityCounter = 0;
		}
		
		lastAttackingEntityTick = taskOwner.ticksExisted;
		taskOwner.setRevengeTarget(null);
		return false;
	}
	
	@Override
	public void startExecuting(){
		taskOwner.setAttackTarget(taskOwner.getAITarget());
		super.startExecuting();
	}
}

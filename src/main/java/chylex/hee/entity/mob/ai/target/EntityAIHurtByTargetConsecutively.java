package chylex.hee.entity.mob.ai.target;
import java.util.UUID;
import java.util.function.IntPredicate;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import chylex.hee.entity.mob.ai.AIUtil;
import chylex.hee.entity.mob.ai.base.EntityAIAbstractTarget;

/**
 * Attacks the last attacker the entity was hurt by.
 * If the entity already has a target, it will only switch if the new attacker damages the entity consecutively a set amount of times within a set time period.
 */
public class EntityAIHurtByTargetConsecutively extends EntityAIAbstractTarget{
	private UUID lastAttackingEntity;
	private int lastAttackingEntityCounter;
	private int lastAttackingEntityTick;
	
	private IntPredicate counterPredicate = n -> true;
	private int counterTimer = 100;
	
	public EntityAIHurtByTargetConsecutively(EntityCreature owner){
		super(owner,false,false);
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
	protected EntityLivingBase findNewTarget(){
		EntityLivingBase revengeTarget = taskOwner.getAITarget();
		if (revengeTarget == null)return null;
		
		if (taskOwner.getAttackTarget() == null)return revengeTarget;
		
		if ((lastAttackingEntityTick == 0 || taskOwner.ticksExisted-lastAttackingEntityTick <= counterTimer) && revengeTarget.getUniqueID().equals(lastAttackingEntity)){
			if (counterPredicate.test(++lastAttackingEntityCounter)){
				lastAttackingEntityCounter = 0;
				lastAttackingEntityTick = 0;
				return revengeTarget;
			}
		}
		else{
			lastAttackingEntity = revengeTarget.getUniqueID();
			lastAttackingEntityCounter = 0;
		}
		
		lastAttackingEntityTick = taskOwner.ticksExisted;
		taskOwner.setRevengeTarget(null);
		return null;
	}
}

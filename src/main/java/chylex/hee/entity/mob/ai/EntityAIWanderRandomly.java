package chylex.hee.entity.mob.ai;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.Vec3;

/**
 * Because EntityAIWander is FUCKING STUPID AND MAKES THE MOBS NEVER MOVE LIKE SERIOUSLY WTF IS THAT.
 */
public class EntityAIWanderRandomly extends EntityAIBase{
	private EntityCreature entity;
	private Vec3 targetPosition;
	private double speed;
	
	private float chance = 1F/120F;
	
	public EntityAIWanderRandomly(EntityCreature owner, double speed){
		this.entity = owner;
		this.speed = speed;
		this.setMutexBits(AIUtil.mutexOverrideMovement);
	}
	
	public EntityAIWanderRandomly setChancePerTick(float chance){
		this.chance = chance;
		return this;
	}

	@Override
	public boolean shouldExecute(){
		return entity.getRNG().nextFloat() < chance && (this.targetPosition = RandomPositionGenerator.findRandomTarget(entity, 10, 7)) != null;
	}

	@Override
	public boolean continueExecuting(){
		return !entity.getNavigator().noPath();
	}

	@Override
	public void startExecuting(){
		entity.getNavigator().tryMoveToXYZ(targetPosition.xCoord, targetPosition.yCoord, targetPosition.zCoord, speed);
	}
}

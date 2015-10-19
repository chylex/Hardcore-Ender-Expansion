package chylex.hee.entity.mob.ai;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.Vec3;

public class EntityAIWanderConstantly extends EntityAIBase{
	private EntityCreature entity;
	private Vec3 targetPosition;
	private double speed;
	
	public EntityAIWanderConstantly(EntityCreature owner, double speed){
		this.entity = owner;
		this.speed = speed;
        this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute(){
		return (this.targetPosition = RandomPositionGenerator.findRandomTarget(entity,10,7)) != null;
	}

	@Override
	public boolean continueExecuting(){
		if (entity.getNavigator().noPath())targetPosition = null;
		return targetPosition != null;
	}

	@Override
	public void startExecuting(){
		entity.getNavigator().tryMoveToXYZ(targetPosition.xCoord,targetPosition.yCoord,targetPosition.zCoord,speed);
	}
	
	@Override
	public void resetTask(){
		targetPosition = null;
	}
}

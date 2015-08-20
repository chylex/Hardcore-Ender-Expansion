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
	}

	@Override
	public boolean shouldExecute(){
		Vec3 vec3 = RandomPositionGenerator.findRandomTarget(entity,10,7);
		return (this.targetPosition = vec3) != null;
	}

	@Override
	public boolean continueExecuting(){
		return !entity.getNavigator().noPath();
	}

	@Override
	public void startExecuting(){
		entity.getNavigator().tryMoveToXYZ(targetPosition.xCoord,targetPosition.yCoord,targetPosition.zCoord,speed);
	}
}

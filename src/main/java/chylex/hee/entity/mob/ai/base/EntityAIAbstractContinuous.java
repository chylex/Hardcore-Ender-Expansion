package chylex.hee.entity.mob.ai.base;
import net.minecraft.entity.ai.EntityAIBase;

public abstract class EntityAIAbstractContinuous extends EntityAIBase{
	protected abstract void tick();
	
	@Override
	public final boolean shouldExecute(){
		tick();
		return false;
	}
	
	@Override
	public final boolean continueExecuting(){
		return false;
	}
	
	@Override
	public final void startExecuting(){}
	
	@Override
	public final void updateTask(){}
}

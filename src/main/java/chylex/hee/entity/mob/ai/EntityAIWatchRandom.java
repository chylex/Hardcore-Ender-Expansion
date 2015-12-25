package chylex.hee.entity.mob.ai;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.world.util.IRangeGenerator;

public class EntityAIWatchRandom extends EntityAIBase{
	protected final EntityCreature entity;
	private IRangeGenerator timerFunction = rand -> 20+rand.nextInt(20);
	private boolean allowVertical;
	
	private int timeLeft;
	private Vec lookOffset;
	
	public EntityAIWatchRandom(EntityCreature owner){
		this.entity = owner;
		setMutexBits(AIUtil.mutexOverrideWatching);
	}
	
	public EntityAIWatchRandom setWatchTime(IRangeGenerator func){
		this.timerFunction = func;
		return this;
	}
	
	public EntityAIWatchRandom setAllowVertical(){
		this.allowVertical = true;
		return this;
	}
	
	@Override
	public boolean shouldExecute(){
		return entity.getRNG().nextFloat() < 0.02F;
	}
	
	@Override
	public boolean continueExecuting(){
		return timeLeft > 0;
	}
	
	@Override
	public void startExecuting(){
		lookOffset = Vec.xzRandom(entity.getRNG());
		if (allowVertical)lookOffset.y += (entity.getRNG().nextDouble()-0.5D); // TODO test
		
		timeLeft = timerFunction.next(entity.getRNG());
	}
	
	@Override
	public void updateTask(){
		--timeLeft;
		entity.getLookHelper().setLookPosition(entity.posX+lookOffset.x,entity.posY+entity.getEyeHeight()+lookOffset.y,entity.posZ+lookOffset.z,10F,entity.getVerticalFaceSpeed());
	}
}

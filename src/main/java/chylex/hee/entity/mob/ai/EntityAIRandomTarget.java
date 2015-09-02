package chylex.hee.entity.mob.ai;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

public class EntityAIRandomTarget extends EntityAITarget{
	private final Class<? extends EntityLivingBase> targetClass;
	private EntityLivingBase currentTarget;
	
	public EntityAIRandomTarget(EntityCreature owner, Class<? extends EntityLivingBase> targetClass, boolean checkSight, boolean nearbyOnly){
		super(owner,checkSight,nearbyOnly);
		this.targetClass = targetClass;
		setMutexBits(1);
	}

	@Override
	public boolean shouldExecute(){
		double maxDist = getTargetDistance();
		
		List<EntityLivingBase> entities = taskOwner.worldObj.getEntitiesWithinAABB(targetClass,taskOwner.boundingBox.expand(maxDist,maxDist*0.5D,maxDist));
		entities = entities.stream().filter(entity -> entity.getDistanceSqToEntity(taskOwner) <= maxDist*maxDist).collect(Collectors.toList());
		if (entities.isEmpty())return false;
		
		currentTarget = entities.get(taskOwner.worldObj.rand.nextInt(entities.size()));
		return true;
	}
	
	@Override
	public void startExecuting(){
		taskOwner.setAttackTarget(currentTarget);
		super.startExecuting();
	}
}

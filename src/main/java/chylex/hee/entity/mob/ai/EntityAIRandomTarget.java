package chylex.hee.entity.mob.ai;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.player.EntityPlayer;
import chylex.hee.system.abstractions.util.EntitySelector;

public class EntityAIRandomTarget<T extends EntityLivingBase> extends EntityAITarget{
	public static final Predicate<EntityPlayer> noCreativeMode = player -> {
		return !player.capabilities.isCreativeMode;
	};
	
	private final Class<T> targetClass;
	private EntityLivingBase currentTarget;
	private Predicate<T> predicate;
	
	public EntityAIRandomTarget(EntityCreature owner, Class<T> targetClass, boolean checkSight){
		super(owner,checkSight,false);
		this.targetClass = targetClass;
		setMutexBits(1);
	}
	
	public EntityAIRandomTarget setPredicate(Predicate<T> predicate){
		this.predicate = predicate;
		return this;
	}

	@Override
	public boolean shouldExecute(){
		double maxDist = getTargetDistance();
		
		List<T> entities = EntitySelector.type(taskOwner.worldObj,targetClass,taskOwner.boundingBox.expand(maxDist,maxDist*0.5D,maxDist));
		
		Stream<T> stream = entities.stream().filter(entity -> entity.getDistanceSqToEntity(taskOwner) <= maxDist*maxDist);
		if (predicate != null)stream = stream.filter(predicate);
		
		entities = stream.collect(Collectors.toList());
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

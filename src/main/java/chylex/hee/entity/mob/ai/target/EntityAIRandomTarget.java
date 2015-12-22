package chylex.hee.entity.mob.ai.target;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import chylex.hee.entity.mob.ai.AIUtil;
import chylex.hee.entity.mob.ai.base.EntityAIAbstractTarget;
import chylex.hee.system.abstractions.entity.EntitySelector;

public class EntityAIRandomTarget<T extends EntityLivingBase> extends EntityAIAbstractTarget{
	public static final Predicate<EntityPlayer> noCreativeMode = player -> {
		return !player.capabilities.isCreativeMode;
	};
	
	private final Class<T> targetClass;
	private Predicate<T> predicate;
	
	public EntityAIRandomTarget(EntityCreature owner, Class<T> targetClass){
		super(owner,false,false);
		this.targetClass = targetClass;
		this.setMutexBits(AIUtil.mutexTarget);
	}
	
	public EntityAIRandomTarget setPredicate(Predicate<T> predicate){
		this.predicate = predicate;
		return this;
	}
	
	@Override
	protected EntityLivingBase findNewTarget(){
		double maxDist = getTargetDistance();
		
		List<T> entities = EntitySelector.type(taskOwner.worldObj,targetClass,taskOwner.boundingBox.expand(maxDist,maxDist*0.5D,maxDist));
		
		Stream<T> stream = entities.stream().filter(entity -> entity.getDistanceSqToEntity(taskOwner) <= maxDist*maxDist);
		if (predicate != null)stream = stream.filter(predicate);
		
		entities = stream.collect(Collectors.toList());
		if (entities.isEmpty())return null;
		
		return entities.get(taskOwner.worldObj.rand.nextInt(entities.size()));
	}
}

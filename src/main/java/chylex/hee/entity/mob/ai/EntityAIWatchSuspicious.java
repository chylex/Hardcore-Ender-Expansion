package chylex.hee.entity.mob.ai;
import javax.annotation.Nullable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;

public class EntityAIWatchSuspicious extends EntityAbstractAIWatching{
	private final IWatchSuspiciousEntities entityHandler;
	
	public EntityAIWatchSuspicious(EntityCreature owner, IWatchSuspiciousEntities entityHandler){
		super(owner);
		this.entityHandler = entityHandler;
	}
	
	@Override
	protected float getHeadRotationSpeed(){
		return 30F;
	}
	
	@Override
	protected EntityLivingBase findTarget(){
		return entityHandler.getSuspiciousEntity();
	}
	
	public static interface IWatchSuspiciousEntities{
		@Nullable EntityLivingBase getSuspiciousEntity();
	}
}

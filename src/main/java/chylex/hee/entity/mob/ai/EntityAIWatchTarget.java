package chylex.hee.entity.mob.ai;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;

public class EntityAIWatchTarget extends EntityAbstractAIWatching{
	public EntityAIWatchTarget(EntityCreature owner){
		super(owner);
	}

	@Override
	protected EntityLivingBase findTarget(){
		return entity.getAttackTarget();
	}
}

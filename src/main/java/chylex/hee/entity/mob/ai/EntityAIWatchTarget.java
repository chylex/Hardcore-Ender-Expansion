package chylex.hee.entity.mob.ai;
import chylex.hee.entity.mob.ai.base.EntityAIAbstractWatching;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;

public class EntityAIWatchTarget extends EntityAIAbstractWatching{
	public EntityAIWatchTarget(EntityCreature owner){
		super(owner);
	}

	@Override
	protected EntityLivingBase findTarget(){
		return entity.getAttackTarget();
	}
}

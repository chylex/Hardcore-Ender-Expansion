package chylex.hee.mechanics.minions.handlers;
import java.util.List;
import net.minecraft.entity.Entity;
import chylex.hee.entity.mob.EntityMobMinion;
import chylex.hee.mechanics.minions.properties.MinionAttributes;

public abstract class AbstractAbilityEntityTargeter extends AbstractAbilityHandler{
	private final Class<? extends Entity> entityClass;
	private final byte nextTargetTimeMax;
	private byte checkTimer = 0, nextTargetTimer = 0, lockPreventionTimer = 0;
	
	public AbstractAbilityEntityTargeter(EntityMobMinion minion, Class<? extends Entity> entityClass, int nextTargetTimeMax){
		super(minion);
		this.entityClass = entityClass;
		this.nextTargetTimeMax = (byte)nextTargetTimeMax;
	}

	@Override
	public void onUpdate(){
		if (nextTargetTimer == 0){
			for(Object o:minion.worldObj.getEntitiesWithinAABB(entityClass,minion.boundingBox.expand(0.25D,0.5D,0.25D))){
				onEntityCollision((Entity)o);
				break;
			}
			
			if (++lockPreventionTimer > 15 && minion.getDistanceFromTarget() < 1.2D){
				lockPreventionTimer = 0;
				minion.unlockTarget(this);
				nextTargetTimer = nextTargetTimeMax;
			}
		}
		else --nextTargetTimer;
		
		if (minion.isTargetLocked() || ++checkTimer < 4)return;
		checkTimer = 0;

		List list = minion.worldObj.getEntitiesWithinAABB(entityClass,minion.boundingBox.expand(8D+4D*minionData.getAttributeLevel(MinionAttributes.RANGE),4D+2D*minionData.getAttributeLevel(MinionAttributes.RANGE),8D+4D*minionData.getAttributeLevel(MinionAttributes.RANGE)));
		if (!list.isEmpty()){
			Entity entity = (Entity)list.get(rand.nextInt(list.size()));
			if (canTargetEntity(entity)){
				minion.lockTargetForAbility(this,entity.posX,entity.posY,entity.posZ);
				lockPreventionTimer = 0;
			}
		}
	}
	
	protected abstract void onEntityCollision(Entity e);
	protected abstract boolean canTargetEntity(Entity e);
}

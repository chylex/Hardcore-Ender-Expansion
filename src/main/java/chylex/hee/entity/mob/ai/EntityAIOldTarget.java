package chylex.hee.entity.mob.ai;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAIOldTarget extends EntityAITarget{
	public static void insertOldAI(EntityCreature entity){
		entity.tasks.taskEntries.clear();
		entity.targetTasks.taskEntries.clear();
		
		entity.tasks.addTask(0,new EntityAISwimming(entity));
        entity.tasks.addTask(2,new EntityAIAttackOnCollide(entity,1D,false));
        entity.tasks.addTask(7,new EntityAIWander(entity,1D));
        entity.tasks.addTask(8,new EntityAIWatchClosest(entity,EntityPlayer.class,8F));
        entity.tasks.addTask(8,new EntityAILookIdle(entity));
        entity.targetTasks.addTask(1,new EntityAIHurtByTarget(entity,false,new Class[0]));
        entity.targetTasks.addTask(2,new EntityAIOldTarget(entity));
	}
	
	private EntityLivingBase target;
	
	public EntityAIOldTarget(EntityCreature owner){
		super(owner,true,false);
		if (!(owner instanceof IOldTargetAI))throw new IllegalArgumentException(owner.getClass().getSimpleName()+" is not IOldTargetAI");
	}

	@Override
	public boolean shouldExecute(){
		if (taskOwner.getRNG().nextInt(10) != 0)return false;
		
		target = ((IOldTargetAI)taskOwner).findEntityToAttack();
		return target != null;
	}
	
	@Override
	public void startExecuting(){
		super.startExecuting();
		taskOwner.setAttackTarget(target);
	}
	
	public static double getTargetDistance(EntityLivingBase owner){
		IAttributeInstance attr = owner.getEntityAttribute(SharedMonsterAttributes.followRange);
		return attr == null ? 16D : attr.getAttributeValue();
	}
	
	public static interface IOldTargetAI{
		EntityLivingBase findEntityToAttack();
	}
}

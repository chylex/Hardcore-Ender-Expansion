package chylex.hee.entity.mob.ai;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;
import chylex.hee.entity.projectile.EntityProjectileCorruptedEnergy;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.util.MathUtil;

public class EntityAIRangedEnergyAttack extends EntityAIBase{
	private final EntityLiving entity;
	private final double moveSpeed;
	
	private EntityLivingBase target;
	private byte attackCooldown, attackShotTimer, attackShots;
	
	public EntityAIRangedEnergyAttack(EntityLiving entity, double moveSpeed){
		this.entity = entity;
		this.moveSpeed = moveSpeed;
	}
	
	@Override
	public boolean shouldExecute(){
		EntityLivingBase target = entity.getAttackTarget();
		if (target == null)return false;
		
		this.target = target;
		return true;
	}
	
	@Override
	public boolean continueExecuting(){
		return shouldExecute() || entity.getNavigator().noPath();
	}
	
	@Override
	public void resetTask(){
		target = null;
		attackCooldown = attackShotTimer = attackShots = 0;
	}
	
	@Override
	public void updateTask(){
		entity.getNavigator().tryMoveToEntityLiving(target,entity.getDistanceSqToEntity(target) > 100D ? moveSpeed : moveSpeed*0.5D);
		entity.getLookHelper().setLookPositionWithEntity(target,25F,25F);
		
		if (entity.getEntitySenses().canSee(target)){
			if (attackShots > 0){
				if (++attackShotTimer > (ModCommonProxy.opMobs ? 4 : 5)){
					shootProjectile();
					attackShotTimer = 0;
					if (++attackShots > 3+entity.worldObj.rand.nextInt(4))attackCooldown = attackShots = 0;
				}
			}
			else if (++attackCooldown > 100-entity.worldObj.getDifficulty().getDifficultyId()*7-(ModCommonProxy.opMobs ? 15 : 0))attackShots = 1;
		}
		else attackCooldown = attackShotTimer = attackShots = 0;
	}
	
	private void shootProjectile(){
		float ang = MathUtil.toRad(entity.renderYawOffset), cos = MathHelper.cos(ang), sin = MathHelper.sin(ang);
		double offX = -0.5D, offZ = 0.5D, x, y, z;
		
		x = entity.posX+cos*offX-sin*offZ;
		y = entity.posY+2F;
		z = entity.posZ+sin*offX+cos*offZ;
		
		entity.worldObj.spawnEntityInWorld(new EntityProjectileCorruptedEnergy(entity.worldObj,entity,x,y,z,target));
	}
}

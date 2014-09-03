package chylex.hee.entity.boss.dragon.attacks.special;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.attacks.special.event.CollisionEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetPositionSetEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetSetEvent;
import chylex.hee.system.util.MathUtil;

public class DragonAttackPunch extends DragonSpecialAttackBase{
	private EntityLivingBase target;
	private EntityLivingBase tempTarget;
	private float speed = 1F;
	private boolean ended;
	
	public DragonAttackPunch(EntityBossDragon dragon, int attackId){
		super(dragon,attackId);
	}
	
	@Override
	public void init(){
		super.init();
		target = tempTarget = null;
		speed = 1F;
		ended = false;
	}
	
	@Override
	public void update(){
		super.update();
		if (phase == 0){
			target = null;
			speed = 1.5F;
			dragon.targetY = 74;
			if (tick > 100)phase = 1;
		}
		else{
			if (tempTarget == null){
				tempTarget = dragon.attacks.getWeakPlayer();
				if (tempTarget == null)ended = true;
				tick = 0;
			}
			else{
				if (target == null && (MathUtil.distance(dragon.posX-tempTarget.posX,dragon.posZ-tempTarget.posZ) > 110D || tick > 200)){
					target = tempTarget;
					dragon.target = target;
					tick = 0;
				}
				
				if (target != null){
					if (tick > 20)speed = 4F;
					
					if (dragon.dragonPartHead.getDistanceSqToEntity(tempTarget) < 35D){
						target.attackEntityFrom(DamageSource.causeMobDamage(dragon),2+dragon.getWorldDifficulty());
						ended = true;
					}
				}
			}
		}
	}
	
	@Override
	public boolean hasEnded(){
		return ended || (target != null && target.isDead);
	}
	
	@Override
	public float overrideMovementSpeed(){
		return speed;
	}
	
	@Override
	public void onTargetSetEvent(TargetSetEvent event){
		event.newTarget = target;
	}
	
	@Override
	public void onTargetPositionSetEvent(TargetPositionSetEvent event){
		if (phase == 0)event.newTargetY = 74;
		else event.cancel();
	}
	
	@Override
	public void onCollisionEvent(CollisionEvent event){
		super.onCollisionEvent(event);
		event.velocityX *= 2.3D*Math.min(3D,speed);
		event.velocityY *= 1.4D*speed;
		event.velocityZ *= 2.3D*Math.min(3D,speed);
	}
}
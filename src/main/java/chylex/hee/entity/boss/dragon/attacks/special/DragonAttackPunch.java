package chylex.hee.entity.boss.dragon.attacks.special;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.attacks.special.event.CollisionEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetPositionSetEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetSetEvent;

public class DragonAttackPunch extends DragonSpecialAttackBase{
	private static final byte PHASE_PREPARATION = 0, PHASE_DISTANCE = 1, PHASE_ATTACK = 2;
	
	private EntityPlayer target;
	private EntityPlayer tempTarget;
	private float speed;
	private boolean ended;
	
	public DragonAttackPunch(EntityBossDragon dragon, int attackId, int weight){
		super(dragon, attackId, weight);
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
		
		if (phase == PHASE_PREPARATION){
			if ((tempTarget = dragon.attacks.getRandomPlayer()) == null){
				ended = true;
				return;
			}
			
			Vec3 vec = Vec3.createVectorHelper(-tempTarget.posX, 0D, -tempTarget.posZ).normalize();
			dragon.targetX = vec.xCoord*55D*(1D+rand.nextDouble()*0.25D)+rand.nextInt(25);
			dragon.targetY = 72+rand.nextInt(12);
			dragon.targetZ = vec.zCoord*55D*(1D+rand.nextDouble()*0.25D)+rand.nextInt(25);
			phase = PHASE_DISTANCE;
		}
		else if (phase == PHASE_DISTANCE){
			if (dragon.getDistance(dragon.targetX, dragon.targetY, dragon.targetZ) < 10D || tick > 180){
				target = tempTarget;
				phase = PHASE_ATTACK;
				tick = 0;
			}
		}
		else if (phase == PHASE_ATTACK){
			if (tick > 20)speed = 3F;
			
			if (dragon.dragonPartHead.getDistanceSqToEntity(tempTarget) < 35D){
				target.attackEntityFrom(DamageSource.causeMobDamage(dragon), 10F+getDifficulty());
				ended = true;
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
		event.cancel();
	}
	
	@Override
	public void onCollisionEvent(CollisionEvent event){
		super.onCollisionEvent(event);
		event.velocityX *= 2D*Math.min(2.5D, speed);
		event.velocityY *= 1.4D*speed;
		event.velocityZ *= 2D*Math.min(2.5D, speed);
	}
}
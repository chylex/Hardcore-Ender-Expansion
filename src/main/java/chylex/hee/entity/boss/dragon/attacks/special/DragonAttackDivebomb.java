package chylex.hee.entity.boss.dragon.attacks.special;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.attacks.special.event.CollisionEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.MotionUpdateEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetPositionSetEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetSetEvent;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;

public class DragonAttackDivebomb extends DragonSpecialAttackBase{
	private static final byte PHASE_FLY_UP = 0, PHASE_DIVEBOMB = 1, PHASE_POST_TARGET = 2, PHASE_END = 3;
	
	private Entity temp;
	private float speed;
	private byte timer;
	private EntityPlayer tmpTarget;
	
	public DragonAttackDivebomb(EntityBossDragon dragon, int attackId){
		super(dragon,attackId);
	}
	
	@Override
	public void init(){
		super.init();
		temp = tmpTarget = null;
		speed = 1F;
		timer = 0;
	}
	
	@Override
	public void update(){
		super.update();
		speed = 1F;
		
		if (phase == PHASE_FLY_UP){
			if (dragon.posY < 120)dragon.targetY = 140;
			
			if (tmpTarget == null)tmpTarget = DragonUtil.getClosestEntity(dragon,dragon.attacks.getViablePlayers());
			
			if (tmpTarget == null){
				dragon.targetX += dragon.getRNG().nextGaussian()*2D;
				dragon.targetZ += dragon.getRNG().nextGaussian()*2D;
			}
			else{
				dragon.targetX = tmpTarget.posX;
				dragon.targetZ = tmpTarget.posZ;
			}
			
			if (++timer > 125 || MathUtil.distance(dragon.targetX-dragon.posX,dragon.targetZ-dragon.posZ) < 6D){
				tick = 0;
				phase = PHASE_DIVEBOMB;
				tmpTarget = null;
			}
		}
		else if (phase == PHASE_DIVEBOMB){
			dragon.targetY = 20;
			speed = 3.5F;
			
			if (dragon.motionY == 0D && tick > 50)phase = PHASE_END;
			
			if (dragon.posY <= 30){
				phase = PHASE_POST_TARGET;
				tick = 1;
			}
			
			if (dragon.moveSpeedMp < 3.5D)dragon.moveSpeedMp += 0.04D;
		}
		else if (phase == PHASE_POST_TARGET){
			if (tick > 160)phase = PHASE_END;
			
			if (temp == null)temp = dragon.attacks.getRandomPlayer();
			if (temp != null){
				dragon.targetX = temp.posX;
				dragon.targetZ = temp.posZ;
			}
			
			dragon.targetY = tick < 60 ? 30 : 80;
			speed = 2F;
		}
		
		if (phase < PHASE_END)dragon.target = null;
	}
	
	@Override
	public boolean hasEnded(){
		return phase == PHASE_END;
	}
	
	@Override
	public float overrideMovementSpeed(){
		return speed;
	}
	
	@Override
	public float overrideWingSpeed(){
		return phase == PHASE_FLY_UP ? 1.15F : phase == PHASE_DIVEBOMB ? 0.4F : 1F;
	}
	
	@Override
	public void onCollisionEvent(CollisionEvent event){
		event.velocityX *= 1.5D;
		event.velocityY *= phase == PHASE_DIVEBOMB ? 8.5D : 6D;
		event.velocityZ *= 1.5D;
	}
	
	@Override
	public void onMotionUpdateEvent(MotionUpdateEvent event){
		if (phase == PHASE_FLY_UP){
			event.motionX *= 0.98D;
			event.motionZ *= 0.98D;
		}
		else if (phase == PHASE_DIVEBOMB){
			event.motionX = MathUtil.clamp(event.motionX/dragon.moveSpeedMp,-0.01D,0.01D);
			event.motionZ = MathUtil.clamp(event.motionZ/dragon.moveSpeedMp,-0.01D,0.01D);
			if (dragon.posY < 30)event.motionY = 0D;
		}
	}
	
	@Override
	public void onTargetSetEvent(TargetSetEvent event){
		event.newTarget = null;
	}
	
	@Override
	public void onTargetPositionSetEvent(TargetPositionSetEvent event){
		if (phase < PHASE_END)event.cancel();
	}
}
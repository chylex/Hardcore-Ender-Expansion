package chylex.hee.entity.boss.dragon.attacks.special;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.attacks.special.event.CollisionEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.MotionUpdateEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetPositionSetEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetSetEvent;
import chylex.hee.system.util.MathUtil;

public class DragonAttackDivebomb extends DragonSpecialAttackBase{
	private Entity temp;
	private float speed = 1F;
	private byte timer;
	private EntityPlayer tmpTarget;
	
	public DragonAttackDivebomb(EntityBossDragon dragon, int attackId){
		super(dragon,attackId);
	}
	
	@Override
	public void init(){
		super.init();
		temp = null;
		speed = 1F;
		timer = 0;
		tmpTarget = null;
	}
	
	@Override
	public void update(){
		super.update();
		speed = 1F;
		
		if (phase == 0){
			if (dragon.posY<120)dragon.targetY = 140;
			
			if (tmpTarget == null){
				EntityPlayer closest = null;
				double dist = Double.MAX_VALUE,d;
				
				for(EntityPlayer player:dragon.attacks.getViablePlayers()){
					if (player.isDead)continue;
					else if ((d = MathUtil.distance(player.posX-dragon.posX,player.posZ-dragon.posZ)) < dist){
						dist = d;
						closest = player;
					}
				}
				
				tmpTarget = closest;
			}
			
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
				phase = 1;
				tmpTarget = null;
			}
		}
		else if (phase == 1){
			dragon.targetY = 20;
			if (dragon.motionY == 0 && tick > 50){
				phase = 3;
			}
			if (dragon.posY <= 30){
				phase = 2;
				tick = 1;
			}
			if (dragon.moveSpeedMp < 3.5D)dragon.moveSpeedMp += 0.04D;
			speed = 3.5F;
		}
		else if (phase == 2){
			if (tick > 160)phase = 3;
			if (temp == null)temp = dragon.attacks.getRandomPlayer();
			if (temp != null && !temp.isDead){
				dragon.targetX = temp.posX;
				dragon.targetZ = temp.posZ;
			}
			if (tick < 60)dragon.targetY = 30;
			else dragon.targetY = 80;
			speed = 2f;
		}
		if (phase < 3)dragon.target = null;
	}
	
	@Override
	public boolean hasEnded(){
		return phase == 3;
	}
	
	@Override
	public float overrideMovementSpeed(){
		return speed;
	}
	
	@Override
	public float overrideWingSpeed(){
		return phase == 0 ? 1.15F : phase == 1 ? 0.4F:1F;
	}
	
	@Override
	public void onCollisionEvent(CollisionEvent event){
		event.velocityX *= 1.5D;
		event.velocityY *= phase == 1 ? 8.5D : 6D;
		event.velocityZ *= 1.5D;
	}
	
	@Override
	public void onMotionUpdateEvent(MotionUpdateEvent event){
		if (phase == 0){
			event.motionX *= 0.98D;
			event.motionZ *= 0.98D;
		}
		else if (phase == 1){
			event.motionX /= dragon.moveSpeedMp;
			event.motionZ /= dragon.moveSpeedMp;
		
			if (phase == 1 && dragon.posY<30)event.motionY = 0;
			
			if (event.motionX > 0.01)event.motionX = 0.01;
			else if (event.motionX < -0.01)event.motionX = -0.01;
			if (event.motionZ > 0.01)event.motionZ = 0.01;
			else if (event.motionZ < -0.01)event.motionZ = -0.01;
		}
	}
	
	@Override
	public void onTargetSetEvent(TargetSetEvent event){
		event.newTarget = null;
	}
	
	@Override
	public void onTargetPositionSetEvent(TargetPositionSetEvent event){
		if (phase < 3)event.cancel();
	}
}
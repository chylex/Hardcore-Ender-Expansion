package chylex.hee.entity.boss.dragon.attacks.special;
/*import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityThrowable;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetPositionSetEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetSetEvent;

public class DragonAttackWhirlwind extends DragonSpecialAttackBase{
	private EntityLivingBase target;
	private float speed = 1F;
	private float wingSpeed = 1F;
	
	public DragonAttackWhirlwind(EntityBossDragon dragon, int attackId){
		super(dragon,attackId);
	}
	
	@Override
	public void init(){
		super.init();
		speed = 1F;
		wingSpeed = 1F;
	}
	
	@Override
	public void update(){
		super.update();
		
		if (target == null)target = dragon.attacks.getRandomPlayer();
		
		if (phase == 0){
			if (target != null){
				dragon.targetX = target.posX;
				dragon.targetZ = target.posZ;
				
				if (dragon.getDistance(target.posX,dragon.posY,target.posZ) < 350D){
					phase = 1;
					tick = 1;
				}
			}
			
			dragon.targetY = 80;
		}
		else if (phase == 1){
			if (speed > 0)speed -= 0.005D;
			else phase = 2;
			
			tick = 1;
		}
		else if (phase == 2){
			wingSpeed = Math.min(wingSpeed+0.04F,1.35F);
			double power = tick*0.0795;
			double bodyCenterX = (dragon.dragonPartBody.boundingBox.minX+dragon.dragonPartBody.boundingBox.maxX)*0.5D;
			double bodyCenterZ = (dragon.dragonPartBody.boundingBox.minZ+dragon.dragonPartBody.boundingBox.maxZ)*0.5D;
			
			for(Iterator<Entity> iter = dragon.worldObj.getEntitiesWithinAABBExcludingEntity(dragon,dragon.dragonPartBody.boundingBox.expand(4D+tick*1.5D,dragon.posY-55D,4D+tick*1.5D).offset(0D,0D,0D)).iterator(); iter.hasNext();){
				Entity entity = iter.next();
				
				if ((entity instanceof EntityLivingBase) || (entity instanceof EntityItem) || (entity instanceof EntityArrow) || (entity instanceof EntityThrowable) || (entity instanceof EntityFireball)){
					double xDiff = entity.posX-bodyCenterX;
					double zDiff = entity.posZ-bodyCenterZ;
					double dist = (xDiff*xDiff+zDiff*zDiff)*3.5D;
					entity.addVelocity((xDiff/dist)*power,0.02535D,(zDiff/dist)*power);
				}
			}
		}
	}
	
	@Override
	public void end(){
		super.end();
		dragon.trySetTarget(target);
	}
	
	@Override
	public boolean hasEnded(){
		return phase == 2 && tick > 500;
	}
	
	@Override
	public float overrideMovementSpeed(){
		return speed;
	}
	
	@Override
	public float overrideWingSpeed(){
		return wingSpeed;
	}
	
	@Override
	public void onTargetSetEvent(TargetSetEvent event){
		event.newTarget = null;
	}
	
	@Override
	public void onTargetPositionSetEvent(TargetPositionSetEvent event){
		event.cancel();
	}
}*/
package chylex.hee.entity.boss.dragon.attacks.special;
import net.minecraft.entity.Entity;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetPositionSetEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetSetEvent;
import chylex.hee.entity.boss.dragon.managers.DragonShotManager.ShotType;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.util.MathUtil;

public class DragonAttackFireburst extends DragonSpecialAttackBase{
	private Entity target;
	private double targetY;
	private byte shootTimer;
	private byte shotAmount;
	private byte runCounter;
	private byte waitTimer;
	private boolean ended;
	
	public DragonAttackFireburst(EntityBossDragon dragon, int attackId){
		super(dragon,attackId);
	}
	
	@Override
	public void init(){
		super.init();
		target = null;
		targetY = 0D;
		shootTimer = shotAmount = runCounter = waitTimer = 0;
		ended = false;
		dragon.target = null;
	}
	
	@Override
	public void update(){
		super.update();
		
		if (target == null || target.isDead){
			if (waitTimer <= 0 && --waitTimer <= 0){
				if ((target = dragon.attacks.getRandomPlayer()) == null)ended = true;
				else{
					if (MathUtil.distance(dragon.targetX-dragon.posX,dragon.targetZ-dragon.posZ) < 30D){
						target = null;
						waitTimer = 80;
					}
					else dragon.targetY = 65D+rand.nextDouble()*10D;
				}
			}
		}
		else{
			dragon.targetX = target.posX;
			dragon.targetZ = target.posZ;
			
			double dist = MathUtil.distance(dragon.targetX-dragon.posX,dragon.targetZ-dragon.posZ);
			boolean stopShooting = false;
			
			if (dist < 80D){
				if (dist < 10D)stopShooting = true;
				else if (++shootTimer > 16-getDifficulty()*2-(ModCommonProxy.opMobs ? 3 : 0)){
					dragon.shots.createNew(ShotType.FIREBALL).setTarget(target).shoot();
					shootTimer = 0;
					
					if (++shotAmount > 7+rand.nextInt(6))stopShooting = true;
				}
			}
			
			if (stopShooting){
				waitTimer = 110;
				shootTimer = 0;
				target = null;
			}
		}
	}
	
	@Override
	public boolean hasEnded(){
		return ended;
	}
	
	@Override
	public void onTargetSetEvent(TargetSetEvent event){
		event.newTarget = null;
	}
	
	@Override
	public void onTargetPositionSetEvent(TargetPositionSetEvent event){
		if (target != null)event.cancel();
	}
}
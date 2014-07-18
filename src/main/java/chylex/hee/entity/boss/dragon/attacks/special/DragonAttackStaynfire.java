package chylex.hee.entity.boss.dragon.attacks.special;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.attacks.special.event.MotionUpdateEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetPositionSetEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetSetEvent;
import chylex.hee.entity.boss.dragon.managers.DragonShotManager.ShotType;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.mechanics.knowledge.util.ObservationUtil;
import chylex.hee.proxy.ModCommonProxy;

public class DragonAttackStaynfire extends DragonSpecialAttackBase{
	private EntityLivingBase target;
	private boolean ended = false;
	private int shootTimer = 0;
	
	public DragonAttackStaynfire(EntityBossDragon dragon, int attackId){
		super(dragon,attackId);
	}
	
	@Override
	public void init(){
		super.init();
		ended = false;
		shootTimer = 0;
	}
	
	@Override
	public void update(){
		super.update();
		if (target == null)target = dragon.attacks.getWeakPlayer();
		if (phase == 0){
			if (dragon.posY<100)dragon.targetY = 120;
			else phase = 1;
		}
		else if (phase > 0){
			boolean inWater = target != null && target.isInWater();
			
			if (inWater)dragon.targetY = 130;
			else dragon.targetY = 90;
			
			if (target != null){
				if (tick > 29-(dragon.getWorldDifficulty()*2)-(ModCommonProxy.opMobs?5:0)){
					tick = 0;
					dragon.initShot().setType(ShotType.FIREBALL).setTarget(target).shoot();
					if (shootTimer++>rand.nextInt(15)+26-damageTaken*2 || (shootTimer > 25 && rand.nextInt(5) == 0) || damageTaken > 13)ended = true;
					
					if (rand.nextBoolean() && rand.nextBoolean()){
						for(EntityPlayer observer:ObservationUtil.getAllObservers(dragon,250D))KnowledgeRegistrations.ENDER_DRAGON.tryUnlockFragment(observer,0.16F,new short[]{ 7,9 });
					}
				}
			
				dragon.targetX = target.posX;
				dragon.targetZ = target.posZ;
				if (!dragon.canEntityBeSeen(target))shootTimer++;
				if (target.isInWater() && (rand.nextInt(40) == 0 || damageTaken > 4)){
					dragon.forceSpecialAttack(dragon.attacks.getSpecialAttackById(10));
				}
			}
		}
	}
	
	@Override
	public void end(){
		super.end();
		if (target != null && target.getHealth() < 8)dragon.doFatalityAttack(target);
	}
	
	@Override
	public boolean hasEnded(){
		return ended || (target != null && target.isDead);
	}
	
	@Override
	public void onMotionUpdateEvent(MotionUpdateEvent event){
		super.onMotionUpdateEvent(event);
		if (phase == 0)return;
		if (dragon.motionX>0.4)dragon.motionX = 0.35;
		else if (dragon.motionX<-0.4)dragon.motionX = -0.35;
		if (dragon.motionZ>0.4)dragon.motionZ = 0.35;
		else if (dragon.motionZ<-0.4)dragon.motionZ = -0.35;
	}
	
	@Override
	public void onTargetSetEvent(TargetSetEvent event){
		event.newTarget = null;
	}
	
	@Override
	public void onTargetPositionSetEvent(TargetPositionSetEvent event){
		event.cancel();
	}
}
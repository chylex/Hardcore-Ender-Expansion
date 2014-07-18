package chylex.hee.entity.boss.dragon.attacks.special;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetPositionSetEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetSetEvent;
import chylex.hee.entity.boss.dragon.managers.DragonShotManager.ShotType;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.mechanics.knowledge.util.ObservationUtil;

public class DragonAttackFreezer extends DragonSpecialAttackBase{
	private EntityLivingBase target;
	private int shootTimer = 0;
	
	public DragonAttackFreezer(EntityBossDragon dragon, int attackId){
		super(dragon,attackId);
	}
	
	@Override
	public void init(){
		super.init();
		shootTimer = 0;
	}
	
	@Override
	public void update(){
		super.update();
		if (phase == 0){
			target = null;
			if (tick > 100)phase = 1;
		}
		else if (phase == 1){
			if (target == null)target = dragon.attacks.getWeakPlayer();
			if (target != null){
				if (shootTimer++>16){
					dragon.initShot().setType(ShotType.FREEZEBALL).setTarget(target).shoot();
					shootTimer = 0;
					for(EntityPlayer observer:ObservationUtil.getAllObservers(dragon,250D))KnowledgeRegistrations.ENDER_DRAGON.tryUnlockFragment(observer,0.3F,new short[]{ 7,12 });
				}
				double dist = target.getDistanceSqToEntity(dragon.dragonPartHead);
				if (dist < 105D)phase = 2;
			}
		}
	}
	
	@Override
	public boolean hasEnded(){
		return phase == 2 || (target != null && target.isDead);
	}
	
	@Override
	public short getNextAttackTimer(){
		return (short)(2+((4-dragon.getWorldDifficulty())*5));
	}
	
	@Override
	public void onTargetSetEvent(TargetSetEvent event){
		event.newTarget = target;
	}
	
	@Override
	public void onTargetPositionSetEvent(TargetPositionSetEvent event){
		event.cancel();
	}
}
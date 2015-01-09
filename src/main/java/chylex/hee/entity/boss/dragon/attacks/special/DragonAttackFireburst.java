package chylex.hee.entity.boss.dragon.attacks.special;
import net.minecraft.entity.EntityLivingBase;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetPositionSetEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetSetEvent;

public class DragonAttackFireburst extends DragonSpecialAttackBase{
	private EntityLivingBase target;
	private byte runCounter;
	private int shootTimer;
	private boolean ended;
	
	public DragonAttackFireburst(EntityBossDragon dragon, int attackId){
		super(dragon,attackId);
	}
	
	@Override
	public void init(){
		super.init();
		target = null;
		runCounter = 0;
		shootTimer = 0;
		ended = false;
	}
	
	@Override
	public void update(){
		super.update();
		
		if (target == null){
			if ((target = dragon.attacks.getRandomPlayer()) == null)ended = true;
		}
		else{
			
		}
	}
	
	@Override
	public void end(){
		super.end();
		if (target != null && target.getHealth() < 8)dragon.doFatalityAttack(target);
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
		event.cancel();
	}
}
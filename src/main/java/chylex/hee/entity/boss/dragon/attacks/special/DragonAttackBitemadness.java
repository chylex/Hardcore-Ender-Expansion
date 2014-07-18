package chylex.hee.entity.boss.dragon.attacks.special;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.attacks.special.event.CollisionEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.DamageTakenEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetPositionSetEvent;
import chylex.hee.entity.boss.dragon.attacks.special.event.TargetSetEvent;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;

public class DragonAttackBitemadness extends DragonSpecialAttackBase{
	private EntityLivingBase target;
	private byte biteCooldown = 0;
	
	public DragonAttackBitemadness(EntityBossDragon dragon, int attackId){
		super(dragon,attackId);
	}
	
	@Override
	public void update(){
		super.update();
		if (target == null){
			target = dragon.attacks.getWeakPlayer();
			dragon.target = target;
		}
		else if (target.isDead)tick = 999;
		else if (target.getHealth() < 7)--tick;
		
		if (target != null){
			if (dragon.target != null && biteCooldown == 0){
				biteCooldown = (byte)(dragon.attacks.biteClosePlayer()?6:3);
				if (dragon.target instanceof EntityPlayer)KnowledgeRegistrations.ENDER_DRAGON.tryUnlockFragment((EntityPlayer)dragon.target,0.14F,new short[]{ 7,10 });
			}
			if (biteCooldown > 0)--biteCooldown;
		}
	}
	
	@Override
	public void end(){
		super.end();
		if (target != null && target.getHealth() < 8)dragon.doFatalityAttack(target);
	}
	
	@Override
	public boolean canStart(){
		return dragon.getHealth() > 40;
	}
	
	@Override
	public boolean hasEnded(){
		return (tick > 40 && target == null) || tick > 500-rand.nextInt(70);
	}
	
	@Override
	public void onDamageTakenEvent(DamageTakenEvent event){
		event.damage = Math.max(event.damage*0.5F,1F);
		tick += 10*event.damage;
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
		event.velocityX *= 0.04D;
		event.velocityY *= 0.05D;
		event.velocityZ *= 0.04D;
	}
}
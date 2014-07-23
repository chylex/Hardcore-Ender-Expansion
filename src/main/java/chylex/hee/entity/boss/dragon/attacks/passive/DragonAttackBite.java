package chylex.hee.entity.boss.dragon.attacks.passive;
import chylex.hee.entity.boss.EntityBossDragon;

public class DragonAttackBite extends DragonPassiveAttackBase{
	private byte biteCooldown;
	
	public DragonAttackBite(EntityBossDragon dragon, int attackId){
		super(dragon,attackId);
	}
	
	@Override
	public void update(){
		if (dragon.target != null && biteCooldown == 0){
			if (dragon.attacks.biteClosePlayer())dragon.trySetTarget(null);
			biteCooldown = 8;
		}
		if (biteCooldown > 0)--biteCooldown;
	}
}

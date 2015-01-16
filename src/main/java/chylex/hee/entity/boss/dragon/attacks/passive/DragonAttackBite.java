package chylex.hee.entity.boss.dragon.attacks.passive;
import chylex.hee.entity.boss.EntityBossDragon;

public class DragonAttackBite extends DragonPassiveAttackBase{
	private byte biteCooldown, biteCounter;
	
	public DragonAttackBite(EntityBossDragon dragon, int attackId){
		super(dragon,attackId);
	}
	
	@Override
	public void update(){
		if (biteCooldown == 0 || --biteCooldown == 0){
			if (dragon.attacks.biteClosePlayers() && ++biteCounter >= (dragon.angryStatus ? 3 : 1)){
				dragon.trySetTarget(null);
				biteCounter = 0;
			}
			
			biteCooldown = (byte)(dragon.angryStatus ? 7 : 9);
		}
	}
}

package chylex.hee.entity.boss.dragon.attacks.passive;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.managers.DragonShotManager.ShotType;

public class DragonAttackFireball extends DragonPassiveAttackBase{
	public DragonAttackFireball(EntityBossDragon dragon, int attackId){
		super(dragon,attackId);
	}
	
	@Override
	public void update(){
		if (dragon.target != null && dragon.ticksExisted%(24-(dragon.getWorldDifficulty()*4)+(dragon.angryStatus?10:30)-((100-dragon.attacks.getHealthPercentage())>>3)) == 0){
			if (dragon.target.getDistanceSqToEntity(dragon.dragonPartHead) > 400D)dragon.initShot().setType(ShotType.FIREBALL).setTarget(dragon.target).shoot();
		}
	}
}

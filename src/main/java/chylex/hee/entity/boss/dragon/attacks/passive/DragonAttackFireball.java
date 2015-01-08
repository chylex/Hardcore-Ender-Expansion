package chylex.hee.entity.boss.dragon.attacks.passive;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.managers.DragonShotManager.ShotType;

public class DragonAttackFireball extends DragonPassiveAttackBase{
	private byte timer;
	
	public DragonAttackFireball(EntityBossDragon dragon, int attackId){
		super(dragon,attackId);
	}
	
	@Override
	public void update(){
		if (dragon.target != null && ++timer >= (24-dragon.worldObj.difficultySetting.getDifficultyId()*4)+(dragon.angryStatus ? 10 : 30)-((100-dragon.attacks.getHealthPercentage())>>3)){
			if (dragon.target.getDistanceSqToEntity(dragon.dragonPartHead) > 400D){
				dragon.shots.createNew(ShotType.FIREBALL).setTarget(dragon.target).shoot();
				timer = 0;
			}
			else --timer;
		}
		else timer = 8;
	}
}

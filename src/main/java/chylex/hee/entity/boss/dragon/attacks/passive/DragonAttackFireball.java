package chylex.hee.entity.boss.dragon.attacks.passive;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.managers.DragonShotManager.ShotType;
import chylex.hee.proxy.ModCommonProxy;

public class DragonAttackFireball extends DragonPassiveAttackBase{
	private byte timer;
	
	public DragonAttackFireball(EntityBossDragon dragon, int attackId){
		super(dragon,attackId);
	}
	
	@Override
	public void update(){
		if (dragon.target == null)timer = 8;
		else if (dragon.target != null && ++timer >= (24-dragon.worldObj.difficultySetting.getDifficultyId()*4)+(dragon.angryStatus ? 5 : 20)-(ModCommonProxy.opMobs ? 5 : 0)-((100-dragon.attacks.getHealthPercentage())>>4)){
			if (dragon.target.getDistanceSqToEntity(dragon.dragonPartHead) > 400D){
				dragon.shots.createNew(ShotType.FIREBALL).setTarget(dragon.target).shoot();
				timer = 0;
			}
			else --timer;
		}
	}
}

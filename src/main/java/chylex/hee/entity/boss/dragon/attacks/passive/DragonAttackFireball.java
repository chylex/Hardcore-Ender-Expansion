package chylex.hee.entity.boss.dragon.attacks.passive;
import net.minecraft.entity.player.EntityPlayer;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.managers.DragonShotManager.ShotType;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;

public class DragonAttackFireball extends DragonPassiveAttackBase{
	public DragonAttackFireball(EntityBossDragon dragon, int attackId){
		super(dragon,attackId);
	}
	
	@Override
	public void update(){
		if (dragon.target != null && dragon.ticksExisted%(24-(dragon.getWorldDifficulty()*4)+(dragon.angryStatus?10:30)-((100-dragon.attacks.getHealthPercentage())>>3)) == 0){
			double dist = dragon.target.getDistanceSqToEntity(dragon.dragonPartHead);
			if (dist > 400D){
				dragon.initShot().setType(ShotType.FIREBALL).setTarget(dragon.target).shoot();
				if (dragon.target instanceof EntityPlayer)KnowledgeRegistrations.ENDER_DRAGON.tryUnlockFragment((EntityPlayer)dragon.target,0.15F,new short[]{ 0,2 });
			}
		}
	}
}

package chylex.hee.entity.boss.dragon.attacks.passive;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.managers.DragonShotManager.ShotType;

public class DragonAttackFreezeball extends DragonPassiveAttackBase{
	public DragonAttackFreezeball(EntityBossDragon dragon, int attackId){
		super(dragon,attackId);
	}
	
	@Override
	public void update(){
		if (!dragon.angryStatus && dragon.ticksExisted%10 == 0){
			for(int a = 0; a < dragon.worldObj.playerEntities.size(); a++){
				Entity e = (Entity)dragon.worldObj.playerEntities.get(a);
				
				if (e != null && !dragon.worldObj.getEntitiesWithinAABB(EntityTNTPrimed.class,e.boundingBox.expand(8D,60D,8D)).isEmpty()){
					dragon.trySetTarget(e);
					dragon.shots.createNew(ShotType.FREEZEBALL).setTarget(e).setRandom().shoot();
				}
			}
		}
	}
}

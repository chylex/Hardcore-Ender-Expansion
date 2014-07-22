package chylex.hee.entity.boss.dragon.attacks.passive;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.boss.dragon.managers.DragonShotManager.ShotType;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;

public class DragonAttackFreezeball extends DragonPassiveAttackBase{
	public DragonAttackFreezeball(EntityBossDragon dragon, int attackId){
		super(dragon,attackId);
	}
	
	@Override
	public void update(){
		if (!dragon.angryStatus && dragon.ticksExisted%10 == 0){
			for(int a = 0; a < dragon.worldObj.playerEntities.size(); a++){
				Entity e = (Entity)dragon.worldObj.playerEntities.get(a);
				
				if (e != null && dragon.worldObj.getEntitiesWithinAABB(EntityTNTPrimed.class,e.boundingBox.expand(8D,60D,8D)).size() > 0){
					dragon.trySetTarget(e);
					dragon.initShot().setType(ShotType.FREEZEBALL).setTarget(e).setRandom().shoot();
					if (e instanceof EntityPlayer)KnowledgeRegistrations.ENDER_DRAGON.tryUnlockFragment((EntityPlayer)e,0.2F,new byte[]{ 0,2 });
				}
			}
		}
	}
}

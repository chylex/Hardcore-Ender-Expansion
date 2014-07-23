package chylex.hee.entity.boss.dragon.managers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.projectile.EntityProjectileDragonFireball;
import chylex.hee.entity.projectile.EntityProjectileDragonFreezeball;
import chylex.hee.proxy.ModCommonProxy;

public class DragonShotManager{
	public enum ShotType{
		NONE, FIREBALL, FREEZEBALL
	}
	
	private final EntityBossDragon dragon;
	private double x,y,z;
	private boolean random;
	private ShotType type;
	
	public DragonShotManager(EntityBossDragon dragon){
		this.dragon = dragon;
		x = y = z = 0d;
		random = false;
		type = ShotType.NONE;
	}
	
	public DragonShotManager setTarget(Entity e){
		if (e == null)return this;
		return setTarget(e.posX,(e.boundingBox.minY+(e.height/2F))-(dragon.posY+(dragon.height/2F)),e.posZ);
	}
	
	public DragonShotManager setTarget(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	public DragonShotManager setType(ShotType type){
		this.type = type;
		return this;
	}
	
	public DragonShotManager setRandom(){
		random = true;
		return this;
	}
	
	public void shoot(){
		if (x == 0 && y == 0 && z == 0)return;
		double xDiff = x-dragon.posX;
		double yTarget = y;
		double zDiff = z-dragon.posZ;
		
		EntityFireball e = null;
		if (type == ShotType.FIREBALL)e = new EntityProjectileDragonFireball(dragon.worldObj,dragon,xDiff,yTarget,zDiff,dragon.angryStatus?1.5F:1F,random,(dragon.angryStatus?2.8F:2.5F)+(ModCommonProxy.opMobs?0.8F:0F));
		else if (type == ShotType.FREEZEBALL)e = new EntityProjectileDragonFreezeball(dragon.worldObj,dragon,xDiff,yTarget,zDiff,dragon.angryStatus?1.5F:1F,random);
		else return;
		
		e.posX = dragon.dragonPartHead.posX;
		e.posY = dragon.dragonPartHead.posY+(dragon.height/6F);
		e.posZ = dragon.dragonPartHead.posZ;
		dragon.worldObj.spawnEntityInWorld(e);
	}
}

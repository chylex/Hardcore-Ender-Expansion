package chylex.hee.entity.boss.dragon.managers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.entity.projectile.EntityProjectileDragonFireball;
import chylex.hee.proxy.ModCommonProxy;

public class DragonShotManager{
	public enum ShotType{
		NONE, FIREBALL
	}
	
	private final EntityBossDragon dragon;
	private ShotType type;
	private double x, y, z;
	private boolean random;
	
	public DragonShotManager(EntityBossDragon dragon){
		this.dragon = dragon;
		reset();
	}
	
	private void reset(){
		type = ShotType.NONE;
		x = y = z = 0D;
		random = false;
	}
	
	public DragonShotManager createNew(ShotType type){
		this.type = type;
		return this;
	}
	
	public DragonShotManager setTarget(Entity e){
		if (e == null)return this;
		return setTarget(e.posX,e.boundingBox.minY+e.height*0.5F,e.posZ);
	}
	
	public DragonShotManager setTarget(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	public DragonShotManager setRandom(){
		random = true;
		return this;
	}
	
	public void shoot(){
		if (x == 0D && y == 0D && z == 0D)return;
		double xDiff = x-dragon.posX;
		double yDiff = y-(dragon.posY+dragon.height*0.5F);
		double zDiff = z-dragon.posZ;
		
		EntityFireball e = type == ShotType.FIREBALL ? new EntityProjectileDragonFireball(dragon.worldObj,dragon,xDiff,yDiff,zDiff,dragon.angryStatus ? 1.5F : 1F,random,(dragon.angryStatus ? 3F : 2.7F)+(ModCommonProxy.opMobs ? 0.8F : 0F)) : null;
		if (e == null)return;
		
		e.posX = dragon.dragonPartHead.posX;
		e.posY = dragon.dragonPartHead.posY+(dragon.height/6F);
		e.posZ = dragon.dragonPartHead.posZ;
		dragon.worldObj.spawnEntityInWorld(e);
		
		reset();
	}
}

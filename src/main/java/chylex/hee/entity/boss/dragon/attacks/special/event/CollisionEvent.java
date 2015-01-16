package chylex.hee.entity.boss.dragon.attacks.special.event;
import net.minecraft.entity.Entity;

public class CollisionEvent{
	public Entity collidedEntity;
	public double velocityX, velocityY, velocityZ;
	
	public CollisionEvent(Entity collidedEntity, double velocityX, double velocityY, double velocityZ){
		this.collidedEntity = collidedEntity;
		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.velocityZ = velocityZ;
	}
	
	public void cancel(){
		velocityX = velocityY = velocityZ = 0D;
	}
}

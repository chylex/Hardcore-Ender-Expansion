package chylex.hee.entity.boss.dragon.attacks.special.event;
import net.minecraft.entity.Entity;

public class TargetSetEvent{
	public final Entity oldTarget;
	public Entity newTarget;
	
	public TargetSetEvent(Entity oldTarget, Entity newTarget){
		this.oldTarget = oldTarget;
		this.newTarget = newTarget;
	}
	
	public void cancel(){
		newTarget = oldTarget;
	}
}

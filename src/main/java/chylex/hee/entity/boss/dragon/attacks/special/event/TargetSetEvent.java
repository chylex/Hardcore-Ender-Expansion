package chylex.hee.entity.boss.dragon.attacks.special.event;
import net.minecraft.entity.player.EntityPlayer;

public class TargetSetEvent{
	public final EntityPlayer oldTarget;
	public EntityPlayer newTarget;
	
	public TargetSetEvent(EntityPlayer oldTarget, EntityPlayer newTarget){
		this.oldTarget = oldTarget;
		this.newTarget = newTarget;
	}
	
	public void cancel(){
		newTarget = oldTarget;
	}
}

package chylex.hee.entity.boss.dragon.attacks.special.event;
import net.minecraft.entity.player.EntityPlayer;

public class TargetPositionSetEvent{
	public EntityPlayer currentEntityTarget;
	public double oldTargetX, oldTargetY, oldTargetZ;
	public double newTargetX, newTargetY, newTargetZ;
	private boolean isCancelled;
	
	public TargetPositionSetEvent(EntityPlayer currentEntityTarget, double oldTargetX, double oldTargetY, double oldTargetZ, double newTargetX, double newTargetY, double newTargetZ){
		this.currentEntityTarget = currentEntityTarget;
		this.oldTargetX = oldTargetX;
		this.oldTargetY = oldTargetY;
		this.oldTargetZ = oldTargetZ;
		this.newTargetX = newTargetX;
		this.newTargetY = newTargetY;
		this.newTargetZ = newTargetZ;
	}
	
	public void cancel(){
		isCancelled = true;
	}
	
	public boolean isCancelled(){
		return isCancelled;
	}
}

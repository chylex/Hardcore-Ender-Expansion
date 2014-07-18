package chylex.hee.entity.boss.dragon.attacks.special.event;

public class MotionUpdateEvent{
	public double motionX,motionY,motionZ;
	
	public MotionUpdateEvent(double motionX, double motionY, double motionZ){
		this.motionX = motionX;
		this.motionY = motionY;
		this.motionZ = motionZ;
	}
}

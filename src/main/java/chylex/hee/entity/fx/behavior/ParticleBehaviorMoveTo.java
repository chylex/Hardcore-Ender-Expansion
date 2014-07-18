package chylex.hee.entity.fx.behavior;
import chylex.hee.system.util.MathUtil;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.util.Vec3;

public class ParticleBehaviorMoveTo implements IParticleBehavior{
	private final Vec3 motionVec;
	private final double targetX,targetY,targetZ;
	private final double checkDist;
	
	public ParticleBehaviorMoveTo(EntityFX particle, double targetX, double targetY, double targetZ, float speedMultiplier){
		motionVec = Vec3.createVectorHelper((this.targetX = targetX)-particle.posX,(this.targetY = targetY)-particle.posY,(this.targetZ = targetZ)-particle.posZ).normalize();
		motionVec.xCoord *= speedMultiplier;
		motionVec.yCoord *= speedMultiplier;
		motionVec.zCoord *= speedMultiplier;
		checkDist = speedMultiplier*0.6D;
	}
	
	@Override
	public void update(EntityFX particle){
		particle.prevPosX = particle.posX;
		particle.prevPosY = particle.posY;
		particle.prevPosZ = particle.posZ;
		
		particle.moveEntity(motionVec.xCoord,motionVec.yCoord,motionVec.zCoord);
		
		if (MathUtil.distance(targetX-particle.posX,targetY-particle.posY,targetZ-particle.posZ) < checkDist)particle.setDead();
	}
}

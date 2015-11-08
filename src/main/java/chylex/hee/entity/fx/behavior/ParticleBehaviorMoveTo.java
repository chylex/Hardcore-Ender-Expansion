package chylex.hee.entity.fx.behavior;
import net.minecraft.client.particle.EntityFX;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.system.util.MathUtil;

public class ParticleBehaviorMoveTo implements IParticleBehavior{
	private final Vec motionVec;
	private final double targetX,targetY,targetZ;
	private final double checkDist;
	
	public ParticleBehaviorMoveTo(EntityFX particle, double targetX, double targetY, double targetZ, float speedMultiplier){
		motionVec = Vec.xyz((this.targetX = targetX)-particle.posX,(this.targetY = targetY)-particle.posY,(this.targetZ = targetZ)-particle.posZ).normalized().multiplied(speedMultiplier);
		checkDist = speedMultiplier*0.51D;
	}
	
	@Override
	public void update(EntityFX particle){
		particle.prevPosX = particle.posX;
		particle.prevPosY = particle.posY;
		particle.prevPosZ = particle.posZ;
		
		particle.moveEntity(motionVec.x,motionVec.y,motionVec.z);
		
		if (MathUtil.distance(targetX-particle.posX,targetY-particle.posY,targetZ-particle.posZ) < checkDist)particle.setDead();
	}
}

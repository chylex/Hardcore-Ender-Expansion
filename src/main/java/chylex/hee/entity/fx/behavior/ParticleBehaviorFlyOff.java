package chylex.hee.entity.fx.behavior;
import net.minecraft.client.particle.EntityFX;
import chylex.hee.system.abstractions.Vec;

public class ParticleBehaviorFlyOff implements IParticleBehavior{
	private final double speedMp;
	private Vec motionVec;
	
	public ParticleBehaviorFlyOff(EntityFX particle, double speedMp){
		this.speedMp = speedMp;
		this.motionVec = Vec.xyzRandom(particle.worldObj.rand).multiplied(speedMp);
	}
	
	@Override
	public void update(EntityFX particle){
		particle.prevPosX = particle.posX;
		particle.prevPosY = particle.posY;
		particle.prevPosZ = particle.posZ;
		
		particle.moveEntity(motionVec.x, motionVec.y, motionVec.z);
		
		if (particle.worldObj.rand.nextInt(3) == 0){
			Vec modifier = Vec.xyzRandom(particle.worldObj.rand).multiplied(particle.worldObj.rand.nextDouble());
			motionVec = motionVec.offset(modifier).normalized().multiplied(speedMp);
		}
	}
}

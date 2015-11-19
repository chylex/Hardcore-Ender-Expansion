package chylex.hee.entity.fx;
import net.minecraft.client.particle.EntityPortalFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityOrbitingPortalFX extends EntityPortalFX{
	private final double orbitSpeed, portalPosX, portalPosZ;
	private final float portalParticleScale;
	private final int customMaxAge;
	private int customAge;
	private double orbitAngle;

	public EntityOrbitingPortalFX(World world, double x, double y, double z, double motionY){
		super(world,x,y,z,0D,motionY,0D);
		portalPosX = x;
		portalPosZ = z;
		motionX = motionZ = 0D;
		orbitAngle = world.rand.nextDouble()*2D*Math.PI;
		orbitSpeed = (world.rand.nextDouble()*0.3217D+0.1954D)*(rand.nextBoolean() ? 1 : -1);
		
		customMaxAge = 2*(particleAge = particleMaxAge = rand.nextInt(15)+10);
		particleScale = portalParticleScale = 0.6F;
	}
	
	@Override
	public void renderParticle(Tessellator tessellator, float partialTickTime, float rotX, float rotXZ, float rotZ, float rotYZ, float rotXY){
		float scale = 1F-((particleAge+partialTickTime)/particleMaxAge);
		scale *= scale;
		particleScale = portalParticleScale*(1F-scale);
		super.renderParticle(tessellator,partialTickTime,rotX,rotXZ,rotZ,rotYZ,rotXY);
	}

	@Override
	public void onUpdate(){
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;

		posX = portalPosX+Math.cos(orbitAngle)*0.35D;
		posY += motionY;
		posZ = portalPosZ+Math.sin(orbitAngle)*0.35D;
		
		orbitAngle += orbitSpeed;
		if (++customAge > customMaxAge)setDead();
		else if (customAge >= customMaxAge/2)--particleAge;
	}
}

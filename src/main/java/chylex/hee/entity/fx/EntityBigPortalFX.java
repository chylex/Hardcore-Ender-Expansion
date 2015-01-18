package chylex.hee.entity.fx;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityBigPortalFX extends EntityFX{
	private byte realParticleAge;
	protected float portalParticleScale;
	
	public EntityBigPortalFX(World world, double x, double y, double z, double motionX, double motionY, double motionZ){
		super(world,x,y,z,motionX,motionY,motionZ);
		
		this.motionX = motionX;
		this.motionY = motionY;
		this.motionZ = motionZ;
		
		particleRed = particleGreen = particleBlue = 1F*rand.nextFloat()*0.6F+0.4F;
		particleGreen *= 0.3F;
		particleRed *= 0.9F;
		particleMaxAge = (int)(Math.random()*10D)+40;
		noClip = true;
		setParticleTextureIndex((int)(Math.random()*8D));
		
		particleScale = portalParticleScale = 2.75F+rand.nextFloat()*0.4F;
		realParticleAge = (byte)(rand.nextInt(20)+35);
		
		renderDistanceWeight = 20D;
	}
	
	public EntityBigPortalFX(World world, double x, double y, double z, double motionX, double motionY, double motionZ, float particleScaleMp){
		this(world,x,y,z,motionX,motionY,motionZ);
		particleScale = portalParticleScale*= particleScaleMp;
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

		moveEntity(motionX,motionY,motionZ);
		motionX *= 0.98D;
		motionY *= 0.98D;
		motionZ *= 0.98D;
		
		if (particleAge < particleMaxAge)particleAge += 3;
		if (--realParticleAge < 0)setDead();
	}
}

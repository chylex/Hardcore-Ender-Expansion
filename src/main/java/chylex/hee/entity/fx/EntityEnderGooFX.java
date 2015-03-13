package chylex.hee.entity.fx;
import net.minecraft.client.particle.EntityLavaFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityEnderGooFX extends EntityLavaFX{
	private final float lavaParticleScale;
	
	public EntityEnderGooFX(World world, double x, double y, double z, double motionX, double motionY, double motionZ){
		super(world,x,y,z);
		this.motionX = motionX;
		this.motionY = motionY;
		this.motionZ = motionZ;
		noClip = true;
		setParticleTextureIndex((int)(Math.random()*8D));
		
		lavaParticleScale = particleScale = rand.nextFloat()*0.4F+0.3F;
		particleRed = particleGreen = particleBlue = rand.nextFloat()*0.6F+0.4F;
		particleGreen *= 0.3F;
		particleRed *= 0.9F;
	}
	
	@Override
	public void renderParticle(Tessellator tessellator, float partialTickTime, float rotX, float rotXZ, float rotZ, float rotYZ, float rotXY){
		float scale = (particleAge+partialTickTime)/particleMaxAge;
		particleScale = lavaParticleScale*(1.0F-scale*scale);
		super.renderParticle(tessellator,partialTickTime,rotX,rotXZ,rotZ,rotYZ,rotXY);
	}
	
	@Override
	public void onUpdate(){
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;

		if (particleAge++ >= particleMaxAge)setDead();
		if (rand.nextFloat() < 0.004F)worldObj.spawnParticle("smoke",posX,posY,posZ,motionX,motionY,motionZ);

		motionY -= 0.005D;
		moveEntity(motionX,motionY,motionZ);
		
		motionX *= 0.999D;
		motionY *= 0.999D;
		motionZ *= 0.999D;
		
		motionX += (rand.nextDouble()-0.5D)*0.04D;
		motionZ += (rand.nextDouble()-0.5D)*0.04D;
	}
}

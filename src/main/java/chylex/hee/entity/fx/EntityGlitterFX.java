package chylex.hee.entity.fx;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityGlitterFX extends EntityFX{
	public EntityGlitterFX(World world, double x, double y, double z, double motionX, double motionY, double motionZ, float red, float green, float blue){
		super(world,x,y,z);
		this.particleRed = red;
		this.particleGreen = green;
		this.particleBlue = blue;
		this.particleAlpha = 0.9F+rand.nextFloat()*0.1F;
		this.particleScale = 0.35F+rand.nextFloat()*0.15F;
		this.particleMaxAge *= 3+rand.nextInt(3);
		this.motionX = motionX;
		this.motionY = motionY;
		this.motionZ = motionZ;
		this.noClip = true;
		
		setParticleTextureIndex(1+(int)(Math.random()*4D));
	}
	
	@Override
	public void onUpdate(){
		super.onUpdate();
		if (particleAge < particleMaxAge*3/4 && rand.nextInt(5) == 0)particleAlpha = 0.5F+rand.nextFloat()*0.5F;
		particleAlpha -= 0.025F;
	}
}

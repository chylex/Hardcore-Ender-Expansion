package chylex.hee.entity.fx;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.entity.fx.behavior.ParticleBehaviorMoveTo;

@SideOnly(Side.CLIENT)
public class EntityRelicGlyphFX extends EntityFX{
	private final ParticleBehaviorMoveTo moveBehavior;
	private byte glyphSwitchTimer = 8;
	
	public EntityRelicGlyphFX(World world, double x, double y, double z, double targetX, double targetY, double targetZ){
		super(world,x,y,z,0D,0D,0D);
		
		moveBehavior = new ParticleBehaviorMoveTo(this,targetX,targetY,targetZ,rand.nextFloat()*0.005F+0.02F);
		
		setParticleTextureIndex((int)(Math.random()*26D+1D+224D));
		particleScale = rand.nextFloat()*0.2F+1.2F;
		particleRed = particleGreen = particleBlue = particleAlpha = 1F;
		particleMaxAge = 280;
		noClip = true;
	}
	
	@Override
	public int getBrightnessForRender(float partialTickTime){
		return 240|(super.getBrightnessForRender(partialTickTime)>>16&255)<<16;
	}
	
	@Override
	public float getBrightness(float partialTickTime){
		return 1F;
	}
	
	@Override
	public void onUpdate(){
		if (--glyphSwitchTimer < 0){
			setParticleTextureIndex((int)(Math.random()*26D+1D+224D));
			glyphSwitchTimer = (byte)(6+rand.nextInt(10));
		}
		
		moveBehavior.update(this);
		if (++particleAge > particleMaxAge)setDead();
	}
}

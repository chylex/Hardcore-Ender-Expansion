package chylex.hee.world.end;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import chylex.hee.system.abstractions.Vec;

public abstract class TerritoryEnvironment{
	public static final TerritoryEnvironment defaultEnvironment = new TerritoryEnvironment(){};
	
	protected Vec3 fogColor = Vec.zero().toVec3();
	
	/**
	 * Returns true to apply default vanilla values to fog position. Can be overridden to configure fog GL settings.
	 */
	public boolean setupFog(){
		GL11.glFogi(GL11.GL_FOG_MODE,GL11.GL_EXP);
		GL11.glFogf(GL11.GL_FOG_DENSITY,getFogDensity());
		GL11.glHint(GL11.GL_FOG_HINT,GL11.GL_DONT_CARE);
		return true;
	}
	
	public final Vec3 getFogColor(){
		return fogColor;
	}
	
	// Main methods for overriding
	
	public void updateFogColor(){
		float r = 160F/255F;
		float g = 128F/255F;
		float b = 160F/255F;
		
		r *= 0.015F;
		g *= 0.015F;
		b *= 0.015F;
		
		fogColor.xCoord = r;
		fogColor.yCoord = g;
		fogColor.zCoord = b;
	}
	
	public float getFogDensity(){
		return 0.02F;
	}
	
	public int getSkyColor(){
		return (40<<16)|(40<<8)|40;
	}
}

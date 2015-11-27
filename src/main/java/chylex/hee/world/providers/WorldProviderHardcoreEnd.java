package chylex.hee.world.providers;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.chunk.IChunkProvider;
import org.lwjgl.opengl.GL11;
import chylex.hee.world.TeleportHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WorldProviderHardcoreEnd extends WorldProviderEnd{
	@Override
	public IChunkProvider createChunkGenerator(){
		return new ChunkProviderHardcoreEnd(worldObj,worldObj.getSeed());
	}
	
	@Override
	public boolean canRespawnHere(){
		return true;
	}
	
	@Override
	public ChunkCoordinates getEntrancePortalLocation(){
		return TeleportHandler.endSpawn;
	}
	
	@Override
	public ChunkCoordinates getSpawnPoint(){
		return TeleportHandler.endSpawn;
	}
	
	@Override
	public ChunkCoordinates getRandomizedSpawnPoint(){
		return TeleportHandler.endSpawn;
	}
	
	// OVERRIDES FOR TESTING AND MESSING AROUND

	@Override
	@SideOnly(Side.CLIENT)
	public boolean doesXZShowFog(int x, int z){
		GL11.glFogi(GL11.GL_FOG_MODE,GL11.GL_EXP);
		GL11.glFogf(GL11.GL_FOG_DENSITY,0.02F);
		GL11.glHint(GL11.GL_FOG_HINT,GL11.GL_DONT_CARE);
		return true;
	}

	@Override
	public float calculateCelestialAngle(long worldTime, float partialTickTime){
		return 0F;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public float[] calcSunriseSunsetColors(float celestialAngle, float partialTickTime){
		return null;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Vec3 getFogColor(float celestialAngle, float partialTickTime){
		int color = 10518688;
		//float omgThisIsGeniusMojang = MathUtil.clamp(MathHelper.cos(celestialAngle*MathUtil.PI*2F)*2F+0.5F,0F,1F);

		float r = (color>>16&255)/255F;
		float g = (color>>8&255)/255F;
		float b = (color&255)/255F;
		
		r *= 0.015F;
		g *= 0.015F;
		b *= 0.015F;
		
		return Vec3.createVectorHelper(r,g,b);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean isSkyColored(){
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean getWorldHasVoidParticles(){
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public double getVoidFogYFactor(){
		return 0.03125D;
	}
}

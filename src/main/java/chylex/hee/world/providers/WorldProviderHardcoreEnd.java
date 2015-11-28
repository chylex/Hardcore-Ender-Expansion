package chylex.hee.world.providers;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.client.IRenderHandler;
import chylex.hee.render.environment.RenderEnvironmentSky;
import chylex.hee.render.environment.RenderEnvironmentWeather;
import chylex.hee.world.TeleportHandler;
import chylex.hee.world.end.EndTerritory;
import chylex.hee.world.end.TerritoryEnvironment;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WorldProviderHardcoreEnd extends WorldProviderEnd{
	@SideOnly(Side.CLIENT)
	private IRenderHandler skyRenderer, weatherRenderer;
	
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
	
	@SideOnly(Side.CLIENT)
	public final @Nonnull TerritoryEnvironment getEnvironment(Minecraft mc){
		if (mc.thePlayer != null && mc.thePlayer.dimension == 1){
			EndTerritory territory = EndTerritory.fromPosition(mc.thePlayer.posX);
			if (territory != null)return territory.environment;
		}
		
		return TerritoryEnvironment.defaultEnvironment;
	}
	
	// Environment overrides
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean doesXZShowFog(int x, int z){
		return getEnvironment(Minecraft.getMinecraft()).setupFog();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IRenderHandler getSkyRenderer(){
		if (skyRenderer == null)skyRenderer = new RenderEnvironmentSky();
		return skyRenderer;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IRenderHandler getWeatherRenderer(){
		if (weatherRenderer == null)weatherRenderer = new RenderEnvironmentWeather();
		return weatherRenderer;
	}
	
	// OVERRIDES FOR TESTING AND MESSING AROUND

	@Override
	@SideOnly(Side.CLIENT)
	public float[] calcSunriseSunsetColors(float celestialAngle, float partialTickTime){
		return null;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Vec3 getFogColor(float celestialAngle, float partialTickTime){
		TerritoryEnvironment environment = getEnvironment(Minecraft.getMinecraft());
		environment.updateFogColor();
		return environment.getFogColor();
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

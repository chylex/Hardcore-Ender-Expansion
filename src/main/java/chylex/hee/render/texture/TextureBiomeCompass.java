package chylex.hee.render.texture;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import chylex.hee.item.ItemBiomeCompass;
import chylex.hee.system.util.MathUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TextureBiomeCompass extends TextureAtlasSprite{
	public double currentAngle;
	public double angleDelta;
	private double lastSavedX,lastSavedZ;
	private ChunkCoordinates cachedCoords;
	private byte cachedBiomeId;

	public TextureBiomeCompass(String iconName){
		super(iconName);
	}

	@Override
	public void updateAnimation(){
		Minecraft mc = Minecraft.getMinecraft();

		if (mc.theWorld != null && mc.thePlayer != null)updateCompass(mc.theWorld,mc.thePlayer.posX,mc.thePlayer.posZ,mc.thePlayer.rotationYaw,false,false);
		else{
			updateCompass((World)null,0D,0D,0D,true,false);
			lastSavedX = lastSavedZ = 0D;
			cachedCoords = null;
		}
	}

	public void updateCompass(World world, double x, double z, double yaw, boolean isInMenu, boolean isItemFrame){
		if (!framesTextureData.isEmpty()){
			double angle = 0D;

			if (world != null && !isInMenu){
				if (lastSavedX == 0D && lastSavedZ == 0D){
					lastSavedX = x;
					lastSavedZ = z;
					cachedCoords = null;
				}
				else if (cachedBiomeId != ItemBiomeCompass.currentBiome){
					cachedBiomeId = ItemBiomeCompass.currentBiome;
					cachedCoords = null;
				}
				else if (MathUtil.square(x-lastSavedX)+MathUtil.square(z-lastSavedZ) > 230)cachedCoords = null;
				
				if (cachedCoords == null && cachedBiomeId != -1 && !ItemBiomeCompass.locations.isEmpty()){
					Set<ChunkCoordinates> coords = ItemBiomeCompass.locations.get(cachedBiomeId);
					double minDist = Double.MAX_VALUE, dist;
					
					for(ChunkCoordinates coord:coords){
						if (minDist == Double.MAX_VALUE){
							cachedCoords = coord;
							minDist = MathUtil.square(coord.posX-x)+MathUtil.square(coord.posZ-z);
							continue;
						}
						
						dist = MathUtil.square(coord.posX-x)+MathUtil.square(coord.posZ-z);
						if (dist < minDist){
							cachedCoords = coord;
							minDist = dist;
						}
					}
					
					lastSavedX = x;
					lastSavedZ = z;
				}
				
				if (cachedCoords == null || world.provider.getDimensionId() != 1)angle = Math.random()*Math.PI*2D;
				else{
					yaw %= 360D;
					angle = -((yaw-90D)*Math.PI/180D-Math.atan2(cachedCoords.posZ-z,cachedCoords.posX-x));
				}
			}

			if (isItemFrame)currentAngle = angle;
			else{
				double targetDelta;

				for(targetDelta = angle-currentAngle; targetDelta < -Math.PI; targetDelta += Math.PI*2D);
				while(targetDelta >= Math.PI)targetDelta -= Math.PI*2D;
				targetDelta = MathUtil.clamp(targetDelta,-1D,1D);

				angleDelta += targetDelta*0.1D;
				angleDelta *= 0.8D;
				currentAngle += angleDelta;
			}
			
			final int framesPerBiome = 32;

			int frame = (int)((currentAngle/(Math.PI*2D)+1D)*framesPerBiome)%framesPerBiome;
			for(; frame < 0; frame = (frame+framesPerBiome)%framesPerBiome);

			if (frame != frameCounter){
				frameCounter = frame+framesPerBiome*cachedBiomeId;
				TextureUtil.uploadTextureMipmap((int[][])framesTextureData.get(frameCounter),width,height,originX,originY,false,false);
			}
		}
	}
}

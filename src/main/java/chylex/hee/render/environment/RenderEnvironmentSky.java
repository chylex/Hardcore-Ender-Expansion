package chylex.hee.render.environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IRenderHandler;
import chylex.hee.system.abstractions.GL;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.end.EndTerritory;
import chylex.hee.world.end.TerritoryEnvironment;
import chylex.hee.world.providers.WorldProviderHardcoreEnd;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderEnvironmentSky extends IRenderHandler{
	public enum SkyTexture{
		DEFAULT("end_default"),
		BLUR("end_blur");
		
		final ResourceLocation resource;
		
		SkyTexture(String name){
			resource = new ResourceLocation("hardcoreenderexpansion:textures/environment/"+name+".png");
		}
	}
	
	@Override
	public void render(float partialTickTime, WorldClient world, Minecraft mc){
		final TerritoryEnvironment environment = ((WorldProviderHardcoreEnd)world.provider).getEnvironment(mc);
		final double voidFactor = EndTerritory.getVoidFactor(mc.thePlayer);
		
		GL.disableFog();
		GL.disableAlphaTest();
		GL.enableBlend(GL.SRC_ALPHA, GL.ONE_MINUS_SRC_ALPHA, 1, 0);
		RenderHelper.disableStandardItemLighting();
		GL.disableDepthMask();
		
		mc.renderEngine.bindTexture(environment.getSkyTexture().resource);
		
		Tessellator tessellator = Tessellator.instance;
		int color = environment.getSkyColor();
		int alpha = MathUtil.floor(255F-255F*MathUtil.clamp(voidFactor, 0F, 1F));

		for(int side = 0; side < 6; side++){
			GL.pushMatrix();
			
			switch(side){
				case 1: GL.rotate(90F, 1F, 0F, 0F); break;
				case 2: GL.rotate(-90F, 1F, 0F, 0F); break;
				case 3: GL.rotate(180F, 1F, 0F, 0F); break;
				case 4: GL.rotate(90F, 0F, 0F, 1F); break;
				case 5: GL.rotate(-90F, 0F, 0F, 1F); break;
			}
			
			tessellator.startDrawingQuads();
			tessellator.setColorRGBA_I(color, alpha);
			tessellator.addVertexWithUV(-100D, -100D, -100D, 0D, 0D);
			tessellator.addVertexWithUV(-100D, -100D, 100D, 0D, 16D);
			tessellator.addVertexWithUV(100D, -100D, 100D, 16D, 16D);
			tessellator.addVertexWithUV(100D, -100D, -100D, 16D, 0D);
			tessellator.draw();
			GL.popMatrix();
		}

		GL.enableDepthMask();
		GL.enableTexture2D();
		GL.enableAlphaTest();
	}
}

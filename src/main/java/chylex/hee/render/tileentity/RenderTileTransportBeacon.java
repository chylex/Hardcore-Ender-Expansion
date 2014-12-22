package chylex.hee.render.tileentity;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import chylex.hee.tileentity.TileEntityLaserBeam;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTileTransportBeacon extends TileEntitySpecialRenderer{
	private static final ResourceLocation beam = new ResourceLocation("hardcoreenderexpansion:textures/blocks/transport_beacon_beam.png");

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTickTime){
		Tessellator tessellator = Tessellator.instance;
		bindTexture(beam);
		
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_WRAP_S,10497F);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D,GL11.GL_TEXTURE_WRAP_T,10497F);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDepthMask(true);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE);
		
		float beamAngle = ((TileEntityLaserBeam)tile).getBeamAngle();
		float f3 = -beamAngle*0.2F-MathHelper.floor_float(-beamAngle*0.1F);
		byte b0 = 1;
		double d3 = beamAngle*025D*(1D-(b0&1)*2.5D); // TODO wtf 025 (check other class too)
		
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA(255,255,255,32);
		double beamSize = b0*0.1D;
		double d5 = 0.5D+Math.cos(d3+2.3562D)*beamSize;
		double d6 = 0.5D+Math.sin(d3+2.3562D)*beamSize;
		double d7 = 0.5D+Math.cos(d3+(Math.PI/4D))*beamSize;
		double d8 = 0.5D+Math.sin(d3+(Math.PI/4D))*beamSize;
		double d9 = 0.5D+Math.cos(d3+3.927D)*beamSize;
		double d10 = 0.5D+Math.sin(d3+3.927D)*beamSize;
		double d11 = 0.5D+Math.cos(d3+5.498D)*beamSize;
		double d12 = 0.5D+Math.sin(d3+5.498D)*beamSize;
		double d16 = (-1F+f3);
		double d17 = (0.5D/beamSize)+d16;
		
		tessellator.addVertexWithUV(x+d5,y+256D,z+d6,1D,d17);
		tessellator.addVertexWithUV(x+d5,y,z+d6,1D,d16);
		tessellator.addVertexWithUV(x+d7,y,z+d8,0D,d16);
		tessellator.addVertexWithUV(x+d7,y+256D,z+d8,0D,d17);
		tessellator.addVertexWithUV(x+d11,y+256D,z+d12,1D,d17);
		tessellator.addVertexWithUV(x+d11,y,z+d12,1D,d16);
		tessellator.addVertexWithUV(x+d9,y,z+d10,0D,d16);
		tessellator.addVertexWithUV(x+d9,y+256D,z+d10,0D,d17);
		tessellator.addVertexWithUV(x+d7,y+256D,z+d8,1D,d17);
		tessellator.addVertexWithUV(x+d7,y,z+d8,1D,d16);
		tessellator.addVertexWithUV(x+d11,y,z+d12,0D,d16);
		tessellator.addVertexWithUV(x+d11,y+256D,z+d12,0D,d17);
		tessellator.addVertexWithUV(x+d9,y+256D,z+d10,1D,d17);
		tessellator.addVertexWithUV(x+d9,y,z+d10,1D,d16);
		tessellator.addVertexWithUV(x+d5,y,z+d6,0D,d16);
		tessellator.addVertexWithUV(x+d5,y+256D,z+d6,0D,d17);
		tessellator.draw();
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glDepthMask(false);
		
		tessellator.startDrawingQuads();
		tessellator.setColorRGBA(255,255,255,32);

		double d29 = (-1F+f3);
		double posMin = 0.4D;
		double posMax = 0.6D;
		
		tessellator.addVertexWithUV(x+posMin,y+1D,z+posMin,1D,d29);
		tessellator.addVertexWithUV(x+posMin,y,z+posMin,1D,d29);
		tessellator.addVertexWithUV(x+posMax,y,z+posMin,0D,d29);
		tessellator.addVertexWithUV(x+posMax,y+1D,z+posMin,0D,d29);
		tessellator.addVertexWithUV(x+posMax,y+1D,z+posMax,1D,d29);
		tessellator.addVertexWithUV(x+posMax,y,z+posMax,1D,d29);
		tessellator.addVertexWithUV(x+posMin,y,z+posMax,0D,d29);
		tessellator.addVertexWithUV(x+posMin,y+1D,z+posMax,0D,d29);
		tessellator.addVertexWithUV(x+posMax,y+1D,z+posMin,1D,d29);
		tessellator.addVertexWithUV(x+posMax,y,z+posMin,1D,d29);
		tessellator.addVertexWithUV(x+posMax,y,z+posMax,0D,d29);
		tessellator.addVertexWithUV(x+posMax,y+1D,z+posMax,0D,d29);
		tessellator.addVertexWithUV(x+posMin,y+1D,z+posMax,1D,d29);
		tessellator.addVertexWithUV(x+posMin,y,z+posMax,1D,d29);
		tessellator.addVertexWithUV(x+posMin,y,z+posMin,0D,d29);
		tessellator.addVertexWithUV(x+posMin,y+1D,z+posMin,0D,d29);
		tessellator.draw();
		
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(true);
	}
}
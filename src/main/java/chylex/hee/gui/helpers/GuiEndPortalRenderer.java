package chylex.hee.gui.helpers;
import java.util.Random;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import chylex.hee.system.abstractions.GL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class GuiEndPortalRenderer{
	private static final ResourceLocation texPortalSky = new ResourceLocation("textures/environment/end_sky.png");
	private static final ResourceLocation texPortal = new ResourceLocation("textures/entity/end_portal.png");
	private static final Random consistentRandom = new Random(31100L);
	
	private final GuiScreen gui;
	private int portalWidthHalf, portalHeightHalf, portalTopOffset, portalTranslation, prevPortalTranslation;
	
	public GuiEndPortalRenderer(GuiScreen ownerGui){
		this.gui = ownerGui;
	}
	
	public void init(int portalWidth, int portalHeight, int portalTopOffset){
		this.portalWidthHalf = portalWidth/2;
		this.portalHeightHalf = portalHeight/2;
		this.portalTopOffset = portalTopOffset;
		this.prevPortalTranslation = this.portalTranslation = gui.mc.theWorld.rand.nextInt(10000);
	}
	
	public void update(int speed){
		prevPortalTranslation = portalTranslation;
		portalTranslation += speed;
	}
	
	public void render(float x, float y, float portalScale, float partialTickTime){
		int hw = gui.width>>1, hh = gui.height>>1;
		
		float div = (float)portalWidthHalf/portalHeightHalf;

		GL.disableLighting();
		GL.enableBlend();
		consistentRandom.setSeed(31100L);

		for(int layer = 0; layer < 16; ++layer){
			float revLayer = (16-layer), scale = 0.09625F, colorMultiplier = 1F/(revLayer+1F), layerMp = 1F+(layer*0.4F);

			if (layer == 0){
				gui.mc.getTextureManager().bindTexture(texPortalSky);
				colorMultiplier = 0.1F;
				scale = 1.125F;
				GL.setBlendFunc(GL.SRC_ALPHA,GL.ONE_MINUS_SRC_ALPHA);
			}

			if (layer >= 1){
				gui.mc.getTextureManager().bindTexture(texPortal);
				
				if (layer == 1){
					GL.setBlendFunc(GL.ONE,GL.ONE);
					scale = 0.2F;
				}
			}
			
			if (layer >= 1 && layer <= 3){ // skip 3 layers for a little bit of performance
				for(int col = 0; col < 3; col++)consistentRandom.nextFloat();
				continue;
			}

			GL.setMatrixMode(GL.TEXTURE);
			GL.pushMatrix();
			GL.loadIdentity();

			GL.translate(0F,layerMp*(prevPortalTranslation+(portalTranslation-prevPortalTranslation)*partialTickTime)*0.00002F,0F);
			GL.scale(scale,scale,1F);
			GL.scale(1F+revLayer*0.15F,1F+revLayer*0.15F,1F);
			GL.translate(0.5F,0.5F,0F);
			GL.rotate((layer*layer*4321+layer*9)*4F+180F,0F,0F,1F);
			
			GL.translate(x*0.0025F*layerMp,y*0.0025F*layerMp,0F);
			GL.translate(0.5F*div,0.5F,0F);
			GL.scale(4F*portalScale,4F*portalScale,1F);
			GL.translate(-0.5F*div,-0.5F,0F);
			
			GL.scale(div,1F,1F);
			
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			
			float red = consistentRandom.nextFloat()*0.5F+0.1F;
			float green = consistentRandom.nextFloat()*0.5F+0.4F;
			float blue = consistentRandom.nextFloat()*0.5F+0.5F;
			if (layer == 0)red = green = blue = 1F;

			tessellator.setColorRGBA_F(red*colorMultiplier,green*colorMultiplier,blue*colorMultiplier,1F);
			tessellator.addVertexWithUV(hw-portalWidthHalf,hh+portalHeightHalf+portalTopOffset,0D,0D,1D);
			tessellator.addVertexWithUV(hw+portalWidthHalf,hh+portalHeightHalf+portalTopOffset,0D,1D,1D);
			tessellator.addVertexWithUV(hw+portalWidthHalf,hh-portalHeightHalf+portalTopOffset,0D,1D,0D);
			tessellator.addVertexWithUV(hw-portalWidthHalf,hh-portalHeightHalf+portalTopOffset,0D,0D,0D);
			tessellator.draw();
			GL.popMatrix();
			GL.setMatrixMode(GL.MODELVIEW);
		}

		GL.disableBlend();
		GL.enableLighting();
	}
}

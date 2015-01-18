package chylex.hee.gui.helpers;
import java.util.Random;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class GuiEndPortalRenderer{
	private static final ResourceLocation texPortalSky = new ResourceLocation("textures/environment/end_sky.png");
	private static final ResourceLocation texPortal = new ResourceLocation("textures/entity/end_portal.png");
	private static final Random consistentRandom = new Random(31100L);
	
	private final GuiScreen gui;
	private final int portalWidthHalf, portalHeightHalf, portalTopOffset;
	private int portalTranslation, prevPortalTranslation;
	
	public GuiEndPortalRenderer(GuiScreen ownerGui, int portalWidth, int portalHeight, int portalTopOffset){
		this.gui = ownerGui;
		this.portalWidthHalf = portalWidth>>1;
		this.portalHeightHalf = portalHeight>>1;
		this.portalTopOffset = portalTopOffset;
		this.prevPortalTranslation = this.portalTranslation = gui.mc.theWorld.rand.nextInt(10000);
	}
	
	public void update(int speed){
		prevPortalTranslation = portalTranslation;
		portalTranslation += speed;
	}
	
	public void draw(float x, float y, float portalScale, float partialTickTime){
		int hw = gui.width>>1, hh = gui.height>>1;
		
		float div = (float)portalWidthHalf/portalHeightHalf;

		GL11.glDisable(GL11.GL_LIGHTING);
		consistentRandom.setSeed(31100L);

		for(int layer = 0; layer < 16; ++layer){
			float revLayer = (16-layer), scale = 0.09625F, colorMultiplier = 1F/(revLayer+1F), layerMp = 1F+(layer*0.4F);

			if (layer == 0){
				gui.mc.getTextureManager().bindTexture(texPortalSky);
				colorMultiplier = 0.1F;
				scale = 1.125F;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
			}

			if (layer >= 1){
				gui.mc.getTextureManager().bindTexture(texPortal);
				
				if (layer == 1){
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_ONE,GL11.GL_ONE);
					scale = 0.2F;
				}
			}

			GL11.glMatrixMode(GL11.GL_TEXTURE);
			GL11.glPushMatrix();
			GL11.glLoadIdentity();

			GL11.glTranslatef(0F,layerMp*(prevPortalTranslation+(portalTranslation-prevPortalTranslation)*partialTickTime)*0.00002F,0F);
			GL11.glScalef(scale,scale,1F);
			GL11.glScalef(1F+revLayer*0.15F,1F+revLayer*0.15F,1F);
			GL11.glTranslatef(0.5F,0.5F,0F);
			GL11.glRotatef((layer*layer*4321+layer*9)*4F+180F,0F,0F,1F);
			
			GL11.glTranslatef(x*0.0025F*layerMp,y*0.0025F*layerMp,0F);
			GL11.glTranslatef(0.5F*div,0.5F,0F);
			GL11.glScalef(4F*portalScale,4F*portalScale,1F);
			GL11.glTranslatef(-0.5F*div,-0.5F,0F);
			
			GL11.glScalef(div,1F,1F);
			
			WorldRenderer renderer = Tessellator.getInstance().getWorldRenderer();
			renderer.startDrawingQuads();
			
			float red = consistentRandom.nextFloat()*0.5F+0.1F;
			float green = consistentRandom.nextFloat()*0.5F+0.4F;
			float blue = consistentRandom.nextFloat()*0.5F+0.5F;
			if (layer == 0)red = green = blue = 1F;

			renderer.setColorRGBA_F(red*colorMultiplier,green*colorMultiplier,blue*colorMultiplier,1F);
			renderer.addVertexWithUV(hw-portalWidthHalf,hh+portalHeightHalf+portalTopOffset,0D,0D,1D);
			renderer.addVertexWithUV(hw+portalWidthHalf,hh+portalHeightHalf+portalTopOffset,0D,1D,1D);
			renderer.addVertexWithUV(hw+portalWidthHalf,hh-portalHeightHalf+portalTopOffset,0D,1D,0D);
			renderer.addVertexWithUV(hw-portalWidthHalf,hh-portalHeightHalf+portalTopOffset,0D,0D,0D);
			Tessellator.getInstance().draw();
			GL11.glPopMatrix();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
		}

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);
	}
}

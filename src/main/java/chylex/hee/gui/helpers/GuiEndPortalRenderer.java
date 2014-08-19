package chylex.hee.gui.helpers;
import java.nio.FloatBuffer;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class GuiEndPortalRenderer{
	private static final ResourceLocation texPortalSky = new ResourceLocation("textures/environment/end_sky.png");
	private static final ResourceLocation texPortal = new ResourceLocation("textures/entity/end_portal.png");
	private static final Random consistentRandom = new Random(31100L);
	
	private final FloatBuffer floatBuffer = GLAllocation.createDirectFloatBuffer(16);
	
	private final GuiScreen gui;
	private final int portalWidthHalf, portalHeightHalf, portalTopOffset;
	
	public GuiEndPortalRenderer(GuiScreen ownerGui, int portalWidth, int portalHeight, int portalTopOffset){
		this.gui = ownerGui;
		this.portalWidthHalf = portalWidth>>1;
		this.portalHeightHalf = portalHeight>>1;
		this.portalTopOffset = portalTopOffset;
	}
	
	public void draw(float x, float y, float portalScale){
		int hw = gui.width>>1, hh = gui.height>>1;

		GL11.glDisable(GL11.GL_LIGHTING);
		consistentRandom.setSeed(31100L);

		for(int layer = 0; layer < 16; ++layer){
			GL11.glPushMatrix();

			float revLayer = (16-layer), scale = 0.09625F, colorMultiplier = 1F/(revLayer+1F);

			if (layer == 0){
				gui.mc.getTextureManager().bindTexture(texPortalSky);
				colorMultiplier = 0.1F;
				revLayer = 15F;
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

			// magic stuff
			float magicScale = 100F*portalScale;
			GL11.glTranslatef(0,revLayer*1000,0);
			GL11.glRotatef(120F,1F,0F,1F);
			GL11.glTranslatef(0F,5500F,0F);
			GL11.glScalef(magicScale,magicScale,magicScale);
			// end of magic stuff
			GL11.glTexGeni(GL11.GL_S,GL11.GL_TEXTURE_GEN_MODE,GL11.GL_EYE_LINEAR);
			GL11.glTexGeni(GL11.GL_T,GL11.GL_TEXTURE_GEN_MODE,GL11.GL_EYE_LINEAR);
			GL11.glTexGeni(GL11.GL_R,GL11.GL_TEXTURE_GEN_MODE,GL11.GL_EYE_LINEAR);
			GL11.glTexGeni(GL11.GL_Q,GL11.GL_TEXTURE_GEN_MODE,GL11.GL_EYE_LINEAR);
			GL11.glTexGen(GL11.GL_S,GL11.GL_OBJECT_PLANE,insertIntoBufferAndFlip(1F,0F,0F,0F));
			GL11.glTexGen(GL11.GL_T,GL11.GL_OBJECT_PLANE,insertIntoBufferAndFlip(0F,0F,1F,0F));
			GL11.glTexGen(GL11.GL_R,GL11.GL_OBJECT_PLANE,insertIntoBufferAndFlip(0F,0F,0F,1F));
			GL11.glTexGen(GL11.GL_Q,GL11.GL_EYE_PLANE,insertIntoBufferAndFlip(0F,1F,0F,0F));
			GL11.glEnable(GL11.GL_TEXTURE_GEN_S);
			GL11.glEnable(GL11.GL_TEXTURE_GEN_T);
			GL11.glEnable(GL11.GL_TEXTURE_GEN_R);
			GL11.glEnable(GL11.GL_TEXTURE_GEN_Q);
			GL11.glPopMatrix();
			GL11.glMatrixMode(GL11.GL_TEXTURE);
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			GL11.glTranslatef(0F,(Minecraft.getSystemTime()%200000L)/200000F,0F);
			GL11.glScalef(scale,scale,scale);
			GL11.glTranslatef(0.5F,0.5F,0F);
			GL11.glRotatef((layer*layer*4321+layer*9)*4F,0F,0F,1F);
			GL11.glTranslatef(-0.5F,-0.5F,0.0F);
			GL11.glTranslatef(x*0.015F,y*0.015F,0F); // TRANSLATE
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
			GL11.glPopMatrix();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
		}

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_S);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_T);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_R);
		GL11.glDisable(GL11.GL_TEXTURE_GEN_Q);
		GL11.glEnable(GL11.GL_LIGHTING);
	}
	
	private FloatBuffer insertIntoBufferAndFlip(float value1, float value2, float value3, float value4){
		floatBuffer.clear();
		floatBuffer.put(value1).put(value2).put(value3).put(value4);
		floatBuffer.flip();
		return floatBuffer;
	}
}

package chylex.hee.render.tileentity;
import java.nio.FloatBuffer;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.RenderEndPortal;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import chylex.hee.proxy.ModClientProxy;
import chylex.hee.system.abstractions.Meta;

public class RenderTileEndPortal extends RenderEndPortal{
	private static final ResourceLocation texPortalBackground = new ResourceLocation("textures/environment/end_sky.png");
	private static final ResourceLocation texPortalLayers = new ResourceLocation("textures/entity/end_portal.png");
	private FloatBuffer buffer = GLAllocation.createDirectFloatBuffer(16);

	@Override
	public void renderTileEntityAt(TileEntityEndPortal tile, double x, double y, double z, float partialTickTime){
		float globalX = (float)field_147501_a.field_147560_j;
		float globalY = (float)field_147501_a.field_147561_k;
		float globalZ = (float)field_147501_a.field_147558_l;
		GL11.glDisable(GL11.GL_LIGHTING);
		
		Random rand = ModClientProxy.seedableRand;
		rand.setSeed(31100L);
		
		int layers = tile.getBlockMetadata() == Meta.endPortalActive ? 16 : 3;
		
		for(int layer = 0; layer < layers; ++layer){
			GL11.glPushMatrix();
			float revLayer = (16-layer);
			float scale = 0.0625F;
			float colorMp = 1F/(revLayer+1F);

			if (layer == 0){
				bindTexture(texPortalBackground);
				colorMp = 0.1F;
				revLayer = 65.0F;
				scale = 0.125F;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
			}

			if (layer >= 1)bindTexture(texPortalLayers);
			
			if (layer == 1){
				bindTexture(texPortalLayers);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_ONE,GL11.GL_ONE);
				scale = 0.5F;
			}

			float offY = (float)(-y-0.75F);
			float topY = offY+ActiveRenderInfo.objectY;
			float layerY = offY+revLayer+ActiveRenderInfo.objectY;
			float translateY = topY/layerY;
			translateY += (float)(y+0.75F);
			GL11.glTranslatef(globalX,translateY,globalZ);
			GL11.glTexGeni(GL11.GL_S,GL11.GL_TEXTURE_GEN_MODE,GL11.GL_OBJECT_LINEAR);
			GL11.glTexGeni(GL11.GL_T,GL11.GL_TEXTURE_GEN_MODE,GL11.GL_OBJECT_LINEAR);
			GL11.glTexGeni(GL11.GL_R,GL11.GL_TEXTURE_GEN_MODE,GL11.GL_OBJECT_LINEAR);
			GL11.glTexGeni(GL11.GL_Q,GL11.GL_TEXTURE_GEN_MODE,GL11.GL_EYE_LINEAR);
			GL11.glTexGen(GL11.GL_S,GL11.GL_OBJECT_PLANE,updateBuffer(1F,0F,0F,0F));
			GL11.glTexGen(GL11.GL_T,GL11.GL_OBJECT_PLANE,updateBuffer(0F,0F,1F,0F));
			GL11.glTexGen(GL11.GL_R,GL11.GL_OBJECT_PLANE,updateBuffer(0F,0F,0F,1F));
			GL11.glTexGen(GL11.GL_Q,GL11.GL_EYE_PLANE,updateBuffer(0F,1F,0F,0F));
			GL11.glEnable(GL11.GL_TEXTURE_GEN_S);
			GL11.glEnable(GL11.GL_TEXTURE_GEN_T);
			GL11.glEnable(GL11.GL_TEXTURE_GEN_R);
			GL11.glEnable(GL11.GL_TEXTURE_GEN_Q);
			GL11.glPopMatrix();
			GL11.glMatrixMode(GL11.GL_TEXTURE);
			GL11.glPushMatrix();
			GL11.glLoadIdentity();
			GL11.glTranslatef(0F,(Minecraft.getSystemTime()%700000L)/700000F,0F);
			GL11.glScalef(scale,scale,scale);
			GL11.glTranslatef(0.5F,0.5F,0F);
			GL11.glRotatef((layer*layer*4321+layer*9)*2F,0F,0F,1F);
			GL11.glTranslatef(-0.5F,-0.5F,0F);
			GL11.glTranslatef(-globalX,-globalZ,-globalY);
			topY = offY+ActiveRenderInfo.objectY;
			GL11.glTranslatef(ActiveRenderInfo.objectX*revLayer/topY,ActiveRenderInfo.objectZ*revLayer/topY,-globalY);
			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			
			float red = rand.nextFloat()*0.5F+0.1F;
			float green = rand.nextFloat()*0.5F+0.4F;
			float blue = rand.nextFloat()*0.5F+0.5F;
			if (layer == 0)red = green = blue = 1F;

			tessellator.setColorRGBA_F(red*colorMp,green*colorMp,blue*colorMp,1F);
			tessellator.addVertex(x,y+0.75F,z);
			tessellator.addVertex(x,y+0.75F,z+1D);
			tessellator.addVertex(x+1D,y+0.75F,z+1D);
			tessellator.addVertex(x+1D,y+0.75F,z);
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

	private FloatBuffer updateBuffer(float value1, float value2, float value3, float value4){
		buffer.clear();
		buffer.put(value1).put(value2).put(value3).put(value4);
		buffer.flip();
		return buffer;
	}
}

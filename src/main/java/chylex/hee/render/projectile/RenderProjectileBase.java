package chylex.hee.render.projectile;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(Side.CLIENT)
abstract class RenderProjectileBase extends Render{
	public RenderProjectileBase(RenderManager renderManager){
		super(renderManager);
	}
	
	protected abstract void render(Entity entity);
	
	@Override
	public void doRender(Entity entity, double x, double y, double z, float rotationYaw, float partialTickTime){
		GL11.glPushMatrix();
		GL11.glTranslated(x,y,z);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glScalef(0.5F,0.5F,0.5F);
		
		GL11.glRotatef(180F-renderManager.playerViewY,0F,1F,0F);
		GL11.glRotatef(-renderManager.playerViewX,1F,0F,0F);
		
		bindEntityTexture(entity);
		render(entity);
		
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}
	
	@Override
	protected final ResourceLocation getEntityTexture(Entity entity){
		return TextureMap.locationItemsTexture;
	}
	
	protected static final void renderIcon(Tessellator tessellator, IIcon icon){
		float minU = icon.getMinU(), maxU = icon.getMaxU(),
			  minV = icon.getMinV(), maxV = icon.getMaxV();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0F,1F,0F);
		tessellator.addVertexWithUV(-0.5F,-0.25F,0D,minU,maxV);
		tessellator.addVertexWithUV(0.5F,-0.25F,0D,maxU,maxV);
		tessellator.addVertexWithUV(0.5F,0.75F,0D,maxU,minV);
		tessellator.addVertexWithUV(-0.5F,0.75F,0D,minU,minV);
		tessellator.draw();
	}
}

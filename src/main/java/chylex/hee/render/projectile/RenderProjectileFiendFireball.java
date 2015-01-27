package chylex.hee.render.projectile;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import chylex.hee.entity.projectile.EntityProjectileFiendFireball;
import chylex.hee.render.RenderUtil;

@SideOnly(Side.CLIENT)
public class RenderProjectileFiendFireball extends Render{
	private final float scale;
	
	public RenderProjectileFiendFireball(RenderManager renderManager, float scale){
		super(renderManager);
		this.scale = scale;
	}
	
	@Override
	public void doRender(Entity entity, double x, double y, double z, float rotationYaw, float partialTickTime){
		EntityProjectileFiendFireball fireball = (EntityProjectileFiendFireball)entity;
		
		GL11.glPushMatrix();
		bindEntityTexture(entity);
		GL11.glTranslated(getX(fireball,partialTickTime),y,getZ(fireball,partialTickTime));
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glScalef(scale,scale,scale);
		
		TextureAtlasSprite icon = RenderUtil.getIcon(Items.fire_charge);
		float minU = icon.getMinU(), maxU = icon.getMaxU();
		float minV = icon.getMinV(), maxV = icon.getMaxV();
		
		GL11.glRotatef(180F-renderManager.playerViewY,0F,1F,0F);
		GL11.glRotatef(-renderManager.playerViewX,1F,0F,0F);
		
		WorldRenderer renderer = Tessellator.getInstance().getWorldRenderer();
		renderer.startDrawingQuads();
		renderer.setNormal(0F,1F,0F);
		renderer.addVertexWithUV(-0.5F,-0.25F,0D,minU,maxV);
		renderer.addVertexWithUV(0.5F,-0.25F,0D,maxU,maxV);
		renderer.addVertexWithUV(0.5F,0.75F,0D,maxU,minV);
		renderer.addVertexWithUV(-0.5F,0.75F,0D,minU,minV);
		Tessellator.getInstance().draw();
		
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
	}
	
	@Override
	public void doRenderShadowAndFire(Entity entity, double x, double y, double z, float rotationYaw, float partialTickTime){
		EntityProjectileFiendFireball fireball = (EntityProjectileFiendFireball)entity;
		super.doRenderShadowAndFire(entity,getX(fireball,partialTickTime),y,getZ(fireball,partialTickTime),rotationYaw,partialTickTime);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity){
		return TextureMap.locationBlocksTexture;
	}
	
	private double getX(EntityProjectileFiendFireball fireball, float partialTickTime){
		return fireball.prevActualPosX+(fireball.actualPosX-fireball.prevActualPosX)*partialTickTime-TileEntityRendererDispatcher.staticPlayerX;
	}
	
	private double getZ(EntityProjectileFiendFireball fireball, float partialTickTime){
		return fireball.prevActualPosZ+(fireball.actualPosZ-fireball.prevActualPosZ)*partialTickTime-TileEntityRendererDispatcher.staticPlayerZ;
	}
}

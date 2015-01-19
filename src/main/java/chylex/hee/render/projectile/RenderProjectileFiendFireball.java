package chylex.hee.render.projectile;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import chylex.hee.entity.projectile.EntityProjectileFiendFireball;

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
		
		IIcon iicon = Items.fire_charge.getIconFromDamage(0);
		Tessellator tessellator = Tessellator.instance;
		float minU = iicon.getMinU(), maxU = iicon.getMaxU();
		float minV = iicon.getMinV(), maxV = iicon.getMaxV();
		
		GL11.glRotatef(180F-renderManager.playerViewY,0F,1F,0F);
		GL11.glRotatef(-renderManager.playerViewX,1F,0F,0F);
		
		tessellator.startDrawingQuads();
		tessellator.setNormal(0F,1F,0F);
		tessellator.addVertexWithUV(-0.5F,-0.25F,0D,minU,maxV);
		tessellator.addVertexWithUV(0.5F,-0.25F,0D,maxU,maxV);
		tessellator.addVertexWithUV(0.5F,0.75F,0D,maxU,minV);
		tessellator.addVertexWithUV(-0.5F,0.75F,0D,minU,minV);
		tessellator.draw();
		
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
		return fireball.prevActualPosX+(fireball.actualPosX-fireball.prevActualPosX)*partialTickTime-RenderManager.renderPosX;
	}
	
	private double getZ(EntityProjectileFiendFireball fireball, float partialTickTime){
		return fireball.prevActualPosZ+(fireball.actualPosZ-fireball.prevActualPosZ)*partialTickTime-RenderManager.renderPosZ;
	}
}

package chylex.hee.render.projectile;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import chylex.hee.entity.projectile.EntityProjectileFiendFireball;
import chylex.hee.system.abstractions.GL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderProjectileFiendFireball extends Render{
	private final float scale;
	
	public RenderProjectileFiendFireball(float scale){
		this.scale = scale;
	}
	
	@Override
	public void doRender(Entity entity, double x, double y, double z, float rotationYaw, float partialTickTime){
		EntityProjectileFiendFireball fireball = (EntityProjectileFiendFireball)entity;
		
		GL.pushMatrix();
		bindEntityTexture(entity);
		GL.translate(getX(fireball, partialTickTime), y, getZ(fireball, partialTickTime));
		GL.enableRescaleNormal();
		GL.scale(scale, scale, scale);
		
		IIcon iicon = Items.fire_charge.getIconFromDamage(0);
		Tessellator tessellator = Tessellator.instance;
		float minU = iicon.getMinU(), maxU = iicon.getMaxU();
		float minV = iicon.getMinV(), maxV = iicon.getMaxV();
		
		GL.rotate(180F-renderManager.playerViewY, 0F, 1F, 0F);
		GL.rotate(-renderManager.playerViewX, 1F, 0F, 0F);
		
		tessellator.startDrawingQuads();
		tessellator.setNormal(0F, 1F, 0F);
		tessellator.addVertexWithUV(-0.5F, -0.25F, 0D, minU, maxV);
		tessellator.addVertexWithUV(0.5F, -0.25F, 0D, maxU, maxV);
		tessellator.addVertexWithUV(0.5F, 0.75F, 0D, maxU, minV);
		tessellator.addVertexWithUV(-0.5F, 0.75F, 0D, minU, minV);
		tessellator.draw();
		
		GL.disableRescaleNormal();
		GL.popMatrix();
	}
	
	@Override
	public void doRenderShadowAndFire(Entity entity, double x, double y, double z, float rotationYaw, float partialTickTime){
		EntityProjectileFiendFireball fireball = (EntityProjectileFiendFireball)entity;
		super.doRenderShadowAndFire(entity, getX(fireball, partialTickTime), y, getZ(fireball, partialTickTime), rotationYaw, partialTickTime);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity){
		return TextureMap.locationItemsTexture;
	}
	
	private double getX(EntityProjectileFiendFireball fireball, float partialTickTime){
		return fireball.prevActualPosX+(fireball.actualPosX-fireball.prevActualPosX)*partialTickTime-RenderManager.renderPosX;
	}
	
	private double getZ(EntityProjectileFiendFireball fireball, float partialTickTime){
		return fireball.prevActualPosZ+(fireball.actualPosZ-fireball.prevActualPosZ)*partialTickTime-RenderManager.renderPosZ;
	}
}

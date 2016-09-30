package chylex.hee.render.projectile;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import chylex.hee.system.abstractions.GL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
abstract class RenderProjectileBase extends Render{
	protected abstract void render(Entity entity);
	
	@Override
	public void doRender(Entity entity, double x, double y, double z, float rotationYaw, float partialTickTime){
		GL.pushMatrix();
		GL.translate(x, y, z);
		GL.enableRescaleNormal();
		GL.scale(0.5F, 0.5F, 0.5F);
		
		GL.rotate(180F-renderManager.playerViewY, 0F, 1F, 0F);
		GL.rotate(-renderManager.playerViewX, 1F, 0F, 0F);
		
		bindEntityTexture(entity);
		render(entity);
		
		GL.disableRescaleNormal();
		GL.popMatrix();
	}
	
	@Override
	protected final ResourceLocation getEntityTexture(Entity entity){
		return TextureMap.locationItemsTexture;
	}
	
	protected static final void renderIcon(Tessellator tessellator, IIcon icon){
		float minU = icon.getMinU(), maxU = icon.getMaxU(),
			  minV = icon.getMinV(), maxV = icon.getMaxV();

		tessellator.startDrawingQuads();
		tessellator.setNormal(0F, 1F, 0F);
		tessellator.addVertexWithUV(-0.5F, -0.25F, 0D, minU, maxV);
		tessellator.addVertexWithUV(0.5F, -0.25F, 0D, maxU, maxV);
		tessellator.addVertexWithUV(0.5F, 0.75F, 0D, maxU, minV);
		tessellator.addVertexWithUV(-0.5F, 0.75F, 0D, minU, minV);
		tessellator.draw();
	}
}

package chylex.hee.render.block;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import chylex.hee.entity.block.EntityBlockTokenHolder;
import chylex.hee.render.model.ModelTokenHolder;
import chylex.hee.system.abstractions.GL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockTokenHolder extends Render{
	private static final ResourceLocation texture = new ResourceLocation("hardcoreenderexpansion:textures/entity/token_holder.png");
	
	private ModelTokenHolder model = new ModelTokenHolder();
	
	@Override
	public void doRender(Entity entity, double x, double y, double z, float rotationYaw, float partialTickTime){
		EntityBlockTokenHolder e = (EntityBlockTokenHolder)entity;
		
		GL.pushMatrix();
		GL.translate(x,y,z);
		GL.enableBlend(GL.ONE,GL.SRC_ALPHA);
		bindTexture(texture);
		model.setRotation(e.prevRotation+(e.rotation-e.prevRotation)*partialTickTime);
		model.setCharge(e.prevCharge+(e.getChargeProgress()-e.prevCharge)*partialTickTime);
		model.render(entity,0F,0F,0F,0F,0F,0.0625F);
		GL.disableBlend();
		GL.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity){
		return texture;
	}
}

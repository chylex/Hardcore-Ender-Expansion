package chylex.hee.render.block;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import chylex.hee.entity.block.EntityBlockTokenHolder;
import chylex.hee.render.model.ModelTokenHolder;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockTokenHolder extends Render{
	private static final ResourceLocation texture = new ResourceLocation("hardcoreenderexpansion:textures/entity/token_holder.png");
	
	private ModelTokenHolder model = new ModelTokenHolder();
	
	@Override
	public void doRender(Entity entity, double x, double y, double z, float rotationYaw, float partialTickTime){
		EntityBlockTokenHolder e = (EntityBlockTokenHolder)entity;
		
		GL11.glPushMatrix();
		GL11.glTranslated(x,y,z);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE,GL11.GL_SRC_ALPHA);
		bindTexture(texture);
		model.setRotation(e.prevRotation+(e.rotation-e.prevRotation)*partialTickTime);
		model.setCharge(e.prevCharge+(e.getChargeProgress()-e.prevCharge)*partialTickTime);
		model.render(entity,0F,0F,0F,0F,0F,0.0625F);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity){
		return texture;
	}
}

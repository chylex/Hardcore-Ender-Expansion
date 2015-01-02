package chylex.hee.render.block;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import chylex.hee.entity.block.EntityBlockHomelandCache;
import chylex.hee.render.model.ModelHomelandCache;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockHomelandCache extends Render{
	private static final ResourceLocation texHomelandCache = new ResourceLocation("hardcoreenderexpansion:textures/entity/homeland_cache.png");
	
	private ModelHomelandCache model = new ModelHomelandCache();
	
	@Override
	public void doRender(Entity entity, double x, double y, double z, float rotationYaw, float partialTickTime){
        EntityBlockHomelandCache e = (EntityBlockHomelandCache)entity;
        
        GL11.glPushMatrix();
        GL11.glTranslated(x,y,z);
        bindTexture(texHomelandCache);
        model.setRotation(e.prevRotation+(e.rotation-e.prevRotation)*partialTickTime);
        model.render(entity,0F,0F,0F,0F,0F,0.0625F);
        GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity){
		return texHomelandCache;
	}
}

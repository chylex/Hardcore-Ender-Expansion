package chylex.hee.render.block;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockHomelandCache extends Render{
	private static final ResourceLocation texHomelandCache = new ResourceLocation("hardcoreenderexpansion:entity/homeland_cache.png");
	
	private ModelBase model;
	
	@Override
	public void doRender(Entity entity, double x, double y, double z, float rotationYaw, float partialTickTime){
        GL11.glPushMatrix();
        GL11.glTranslated(x,y,z);
        bindTexture(texHomelandCache);
        model.render(entity,0F,0F,0F,0F,0F,0.0625F);
        GL11.glPopMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity){
		return texHomelandCache;
	}
}

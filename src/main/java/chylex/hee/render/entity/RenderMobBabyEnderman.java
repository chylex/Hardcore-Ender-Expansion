package chylex.hee.render.entity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import chylex.hee.entity.mob.util.IEndermanRenderer;
import chylex.hee.render.entity.layer.LayerBabyEndermanHeldItem;

@SideOnly(Side.CLIENT)
public class RenderMobBabyEnderman extends AbstractRenderMobEnderman{
	public RenderMobBabyEnderman(RenderManager renderManager){
		super(renderManager);
	}
	
	@Override
	protected LayerRenderer getHeldItemRenderer(){
		return new LayerBabyEndermanHeldItem(this);
	}

	@Override
	public void renderEnderman(IEndermanRenderer enderman, double x, double y, double z, float yaw, float partialTickTime){
		GL11.glPushMatrix();
		GL11.glTranslated(x,y,z);
		GL11.glScalef(0.48F,0.48F,0.48F);
		GL11.glTranslated(-x,-y,-z);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		superDoRender(enderman,x,y,z,yaw,partialTickTime);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
	}
}
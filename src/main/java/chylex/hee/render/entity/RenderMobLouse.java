package chylex.hee.render.entity;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import chylex.hee.render.model.ModelLouse;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMobLouse extends RenderLiving{
	private static final ResourceLocation texLouse = new ResourceLocation("textures/entity/enderman/enderman.png"); // TODO dynamic textures

	public RenderMobLouse(){
		super(new ModelLouse(),1F);
	}

	@Override
	protected void preRenderCallback(EntityLivingBase entityliving, float partialTickTime){
		GL11.glScalef(0.6F,0.6F,0.6F);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity){
		return texLouse;
	}
}

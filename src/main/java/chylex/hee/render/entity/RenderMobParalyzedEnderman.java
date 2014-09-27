package chylex.hee.render.entity;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderEnderman;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMobParalyzedEnderman extends RenderEnderman{
	private static final ResourceLocation texEndermanEyes = new ResourceLocation("hardcoreenderexpansion:textures/entity/enderman_eyes_brainless.png");

	@Override
	protected int shouldRenderPass(EntityEnderman enderman, int pass, float partialTickTime){
		if (pass != 0)return -1;

		bindTexture(texEndermanEyes);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_ONE,GL11.GL_ONE);
		GL11.glDisable(GL11.GL_LIGHTING);

		GL11.glDepthMask(!enderman.isInvisible());

		char c = 61680;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,c%65536,c/65536);
		GL11.glColor4f(1F,1F,1F,1F);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glColor4f(1F,1F,1F,1F);
		return 1;
	}
}
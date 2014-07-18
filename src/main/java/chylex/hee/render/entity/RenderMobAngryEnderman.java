package chylex.hee.render.entity;
import java.util.Random;
import net.minecraft.client.model.ModelEnderman;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import chylex.hee.entity.mob.EntityMobAngryEnderman;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMobAngryEnderman extends RenderLiving{
    private static final ResourceLocation texEndermanEyes = new ResourceLocation("textures/entity/enderman/enderman_eyes.png");
    private static final ResourceLocation texEndermanBody = new ResourceLocation("textures/entity/enderman/enderman.png");

	private ModelEnderman endermanModel;
	private Random rand = new Random();

	public RenderMobAngryEnderman(){
		super(new ModelEnderman(),0.5F);
		endermanModel = (ModelEnderman)super.mainModel;
		setRenderPassModel(endermanModel);
	}

	public void renderEnderman(EntityMobAngryEnderman entity, double x, double y, double z, float yaw, float partialTickTime){
		endermanModel.isCarrying = false;
		endermanModel.isAttacking = entity.isScreaming();

		if (entity.isScreaming()){
			double spazzAmount = 0.02D;
			x += rand.nextGaussian()*spazzAmount;
			z += rand.nextGaussian()*spazzAmount;
		}

		super.doRender(entity,x,y,z,yaw,partialTickTime);
	}

	protected int renderEyes(EntityMobAngryEnderman enderman, int pass, float partialTickTime){
		if (pass != 0)return -1;

		bindTexture(texEndermanEyes);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
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

	@Override
	protected int shouldRenderPass(EntityLivingBase entity, int pass, float partialTickTime){
		return renderEyes((EntityMobAngryEnderman)entity,pass,partialTickTime);
	}

	@Override
	protected void renderEquippedItems(EntityLivingBase entity, float partialTickTime){}

	@Override
	public void doRender(EntityLivingBase entity, double x, double y, double z, float yaw, float partialTickTime){
		renderEnderman((EntityMobAngryEnderman)entity,x,y,z,yaw,partialTickTime);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity){
		return texEndermanBody;
	}

	@Override
	public void doRender(EntityLiving entity, double x, double y, double z, float yaw, float partialTickTime){
		renderEnderman((EntityMobAngryEnderman)entity,x,y,z,yaw,partialTickTime);
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTickTime){
		renderEnderman((EntityMobAngryEnderman)entity,x,y,z,yaw,partialTickTime);
	}
}
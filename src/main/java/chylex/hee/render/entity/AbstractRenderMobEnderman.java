package chylex.hee.render.entity;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelEnderman;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import chylex.hee.entity.mob.util.IEndermanRenderer;
import chylex.hee.mechanics.misc.Baconizer;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.render.model.ModelBaconmanHead;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class AbstractRenderMobEnderman extends RenderLiving{
	protected static final ResourceLocation texEndermanEyes = new ResourceLocation("textures/entity/enderman/enderman_eyes.png");
	protected static final ResourceLocation texEndermanBody = new ResourceLocation("textures/entity/enderman/enderman.png");

	protected final ModelEnderman endermanModel;
	protected final Random rand = new Random();

	public AbstractRenderMobEnderman(){
		super(new ModelEnderman(),0.5F);
		endermanModel = (ModelEnderman)super.mainModel;
		setRenderPassModel(endermanModel);
		
		if (ModCommonProxy.hardcoreEnderbacon){
			endermanModel.bipedHead = new ModelBaconmanHead(endermanModel,0,0);
		}
	}
	
	protected final void superDoRender(IEndermanRenderer entity, double x, double y, double z, float yaw, float partialTickTime){
		super.doRender((EntityLiving)entity,x,y,z,yaw,partialTickTime);
	}

	public void renderEnderman(IEndermanRenderer entity, double x, double y, double z, float yaw, float partialTickTime){
		endermanModel.isCarrying = entity.isCarrying();
		endermanModel.isAttacking = entity.isScreaming();

		if (entity.isScreaming()){
			double spazzAmount = 0.02D;
			x += rand.nextGaussian()*spazzAmount;
			z += rand.nextGaussian()*spazzAmount;
		}

		superDoRender(entity,x,y,z,yaw,partialTickTime);
	}

	protected int renderEyes(IEndermanRenderer enderman, int pass, float partialTickTime){
		if (pass != 0 || ModCommonProxy.hardcoreEnderbacon)return -1;

		bindTexture(texEndermanEyes);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(GL11.GL_ONE,GL11.GL_ONE);
		GL11.glDisable(GL11.GL_LIGHTING);

		GL11.glDepthMask(!((Entity)enderman).isInvisible());

		char c = 61680;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,c%65536,c/65536);
		GL11.glColor4f(1F,1F,1F,1F);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glColor4f(1F,1F,1F,1F);
		return 1;
	}
	
	protected void renderEquippedItems(IEndermanRenderer enderman, float partialTickTime){
		ItemStack carrying = enderman.getCarrying();
		
		if (carrying != null){
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glPushMatrix();
			GL11.glTranslatef(0F,0.6875F,-0.75F);
			GL11.glRotatef(20F,1F,0F,0F);
			GL11.glRotatef(45F,0F,1F,0F);
			GL11.glScalef(-0.5F,-0.5F,0.5F);
			int brightness = ((Entity)enderman).getBrightnessForRender(partialTickTime);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,brightness%65536F,brightness/65536F);
			GL11.glColor4f(1F,1F,1F,1F);
			bindTexture(TextureMap.locationBlocksTexture);
			field_147909_c.renderBlockAsItem(Block.getBlockFromItem(carrying.getItem()),carrying.getItemDamage(),1.0F);
			GL11.glPopMatrix();
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		}
	}

	@Override
	protected int shouldRenderPass(EntityLivingBase entity, int pass, float partialTickTime){
		return renderEyes((IEndermanRenderer)entity,pass,partialTickTime);
	}

	@Override
	protected void renderEquippedItems(EntityLivingBase entity, float partialTickTime){
		renderEquippedItems((IEndermanRenderer)entity,partialTickTime);
		super.renderEquippedItems(entity,partialTickTime);
	}

	@Override
	public void doRender(EntityLivingBase entity, double x, double y, double z, float yaw, float partialTickTime){
		renderEnderman((IEndermanRenderer)entity,x,y,z,yaw,partialTickTime);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity){
		return Baconizer.mobTexture(this,texEndermanBody);
	}

	@Override
	public void doRender(EntityLiving entity, double x, double y, double z, float yaw, float partialTickTime){
		renderEnderman((IEndermanRenderer)entity,x,y,z,yaw,partialTickTime);
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTickTime){
		renderEnderman((IEndermanRenderer)entity,x,y,z,yaw,partialTickTime);
	}
}
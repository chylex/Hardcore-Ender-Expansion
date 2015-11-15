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
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class AbstractRenderMobEnderman extends RenderLiving{
	protected static final ResourceLocation texEndermanEyes = new ResourceLocation("textures/entity/enderman/enderman_eyes.png");
	protected static final ResourceLocation texEndermanBody = new ResourceLocation("textures/entity/enderman/enderman.png");

	protected final ModelEnderman endermanModel;
	protected final Random rand = new Random();

	public AbstractRenderMobEnderman(){
		super(new ModelEnderman(),0.5F);
		endermanModel = (ModelEnderman)super.mainModel;
		endermanModel.isAttacking = false;
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
		superDoRender(entity,x,y,z,yaw,partialTickTime);
	}

	protected int renderEyes(IEndermanRenderer enderman, int pass, float partialTickTime){
		if (pass <= 0 || ModCommonProxy.hardcoreEnderbacon)return -1;
		
		bindTexture(texEndermanEyes);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glBlendFunc(GL11.GL_ONE,GL11.GL_ONE);
		GL11.glDisable(GL11.GL_LIGHTING);

		GL11.glDepthMask(true);

		char c = 61680;
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,c%65536,c/65536);
		GL11.glColor4f(1F,1F,1F,1F);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glColor4f(1F,1F,1F,1F);
		
		return 1;
	}
	
	protected void renderEquippedItems(IEndermanRenderer enderman, float partialTickTime){
		if (!enderman.isCarrying())return;
		
		ItemStack carrying = enderman.getCarrying();
		
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
		field_147909_c.renderBlockAsItem(Block.getBlockFromItem(carrying.getItem()),carrying.getItemDamage(),1F);
		
		GL11.glPopMatrix();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
	}
	
	@Override
	protected void renderModel(EntityLivingBase entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel){
		super.renderModel(entity,limbSwing,limbSwingAngle,entityTickTime,rotationYaw,rotationPitch,unitPixel);
		
		if (((IEndermanRenderer)entity).isAggressive()){
			rand.setSeed(entity.worldObj.getTotalWorldTime());
			
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glDepthMask(false);
			GL11.glAlphaFunc(GL11.GL_GREATER,0.004F);
			
			for(int a = 0; a < 3; a++){
				GL11.glColor4f(1F,1F,1F,0.025F+rand.nextFloat()*0.075F);
				GL11.glPushMatrix();
				GL11.glTranslated(rand.nextGaussian()*0.04D,rand.nextGaussian()*0.04D,rand.nextGaussian()*0.04D);
				super.renderModel(entity,limbSwing,limbSwingAngle,entityTickTime,rotationYaw,rotationPitch,unitPixel);
				GL11.glPopMatrix();
			}
			
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glAlphaFunc(GL11.GL_GREATER,0.1F);
			GL11.glDepthMask(true);
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
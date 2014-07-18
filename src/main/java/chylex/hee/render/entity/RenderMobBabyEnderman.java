package chylex.hee.render.entity;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelEnderman;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import chylex.hee.entity.mob.EntityMobBabyEnderman;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMobBabyEnderman extends RenderLiving{
	private static final ResourceLocation texEndermanEyes = new ResourceLocation("textures/entity/enderman/enderman_eyes.png");
	private static final ResourceLocation texEndermanBody = new ResourceLocation("textures/entity/enderman/enderman.png");

	private ModelEnderman endermanModel;

	public RenderMobBabyEnderman(){
		super(new ModelEnderman(),0.5F);
		endermanModel = (ModelEnderman)super.mainModel;
		setRenderPassModel(endermanModel);
	}

	public void renderEnderman(EntityMobBabyEnderman enderman, double x, double y, double z, float yaw, float partialTickTime){
		GL11.glPushMatrix();
		GL11.glTranslated(x,y,z);
		GL11.glScalef(0.48F,0.48F,0.48F);
		GL11.glTranslated(-x,-y,-z);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDepthFunc(GL11.GL_LEQUAL);

		endermanModel.isCarrying = enderman.isCarryingItemStack();
		endermanModel.isAttacking = false;

		super.doRender(enderman,x,y,z,yaw,partialTickTime);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glPopMatrix();
	}

	protected int renderEyes(EntityMobBabyEnderman enderman, int pass, float partialTickTime){
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

	protected void renderCarrying(EntityMobBabyEnderman enderman, float partialTickTime){
		super.renderEquippedItems(enderman,partialTickTime);
		
		ItemStack is = enderman.getCarriedItemStack();
		if (is == null)return;
		
		Item item = is.getItem();
		Block block = Block.getBlockFromItem(item);
		
		if (block != Blocks.bedrock){
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glPushMatrix();
			GL11.glTranslatef(0F,0.6875F,-0.75F);
			GL11.glRotatef(20F,1F,0F,0F);
			GL11.glRotatef(45F,0F,1F,0F);
			GL11.glScalef(-0.5F,-0.5F,0.5F);
			int brightness = enderman.getBrightnessForRender(partialTickTime);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,brightness%65536F,brightness/65536F);
			GL11.glColor4f(1F,1F,1F,1F);
			GL11.glColor4f(1F,1F,1F,1F);
			
			if (is.getItemSpriteNumber() == 0){
				bindTexture(TextureMap.locationBlocksTexture);
				field_147909_c.renderBlockAsItem(block,is.getItemDamage(),1F);
			}
			else{
				GL11.glPushMatrix();
				GL11.glTranslatef(0.2F,-0.3F,-0.3F);
				renderManager.itemRenderer.renderItem(enderman,is,0,ItemRenderType.ENTITY);
				GL11.glPopMatrix();
			}
			
			GL11.glPopMatrix();
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		}
	}

	@Override
	protected int shouldRenderPass(EntityLivingBase entity, int pass, float partialTickTime){
		return renderEyes((EntityMobBabyEnderman)entity,pass,partialTickTime);
	}

	@Override
	protected void renderEquippedItems(EntityLivingBase entity, float partialTickTime){
		renderCarrying((EntityMobBabyEnderman)entity,partialTickTime);
	}

	@Override
	public void doRender(EntityLivingBase entity, double x, double y, double z, float yaw, float partialTickTime){
		renderEnderman((EntityMobBabyEnderman)entity,x,y,z,yaw,partialTickTime);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity){
		return texEndermanBody;
	}

	@Override
	public void doRender(EntityLiving entity, double x, double y, double z, float yaw, float partialTickTime){
		renderEnderman((EntityMobBabyEnderman)entity,x,y,z,yaw,partialTickTime);
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTickTime){
		renderEnderman((EntityMobBabyEnderman)entity,x,y,z,yaw,partialTickTime);
	}
}
package chylex.hee.render.entity;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import chylex.hee.entity.mob.EntityMobCorporealMirage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMobCorporealMirage extends RendererLivingEntity{
	private static final double angleStep = 0.01745329251994327D;
	
	public RenderMobCorporealMirage(){
		super(new ModelBiped(),0F);
	}
	
	@Override
	protected void renderModel(EntityLivingBase entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel){
		if (entity.ticksExisted < 2){
			entity.prevRotationYaw = entity.renderYawOffset = entity.prevRenderYawOffset = entity.rotationYaw;
			return;
		}

		float alpha = Math.min(0.15F,(entity.ticksExisted-2)*0.006F);
		
		bindEntityTexture(entity);

		GL11.glPushMatrix();
		GL11.glColor4f(1F,1F,1F,alpha*(0.25F+(entity.getHealth()*0.75F/entity.getMaxHealth())));
		GL11.glDepthMask(false);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glAlphaFunc(GL11.GL_GREATER,0.003921569F);
		
		GL11.glTranslated(0F,Math.cos(((EntityMobCorporealMirage)entity).angle += angleStep)*alpha,0F);
		mainModel.render(entity,limbSwing,limbSwingAngle,entityTickTime,rotationYaw,rotationPitch,unitPixel);
		
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glAlphaFunc(GL11.GL_GREATER,0.1F);
		GL11.glPopMatrix();
		GL11.glDepthMask(true);
	}
	
	@Override
	protected float renderSwingProgress(EntityLivingBase entity, float partialTickTime){
		return 0F;
	}

	@Override
	protected float handleRotationFloat(EntityLivingBase entity, float partialTickTime){
		return 0F;
	}
	
	@Override
	protected float getDeathMaxRotation(EntityLivingBase entity){
		return 0F;
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity){
		return ((EntityMobCorporealMirage)entity).getMirageSkin();
	}

	@Override
	protected boolean func_110813_b(EntityLivingBase entity){ // OBFUSCATED show mob name
		return false;
	}
}

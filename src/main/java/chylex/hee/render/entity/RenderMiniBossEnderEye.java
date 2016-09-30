package chylex.hee.render.entity;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.util.ResourceLocation;
import chylex.hee.entity.boss.EntityMiniBossEnderEye;
import chylex.hee.render.model.ModelEnderEye;
import chylex.hee.system.abstractions.GL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMiniBossEnderEye extends RenderLiving{
	private static final ResourceLocation texAwake = new ResourceLocation("hardcoreenderexpansion:textures/entity/ender_eye.png");
	private static final ResourceLocation texAsleep = new ResourceLocation("hardcoreenderexpansion:textures/entity/ender_eye_asleep.png");

	private int statusTick = 0;
	
	public RenderMiniBossEnderEye(){
		super(new ModelEnderEye(), 0.75F);
	}
	
	@Override
	protected void preRenderCallback(EntityLivingBase entity, float partialTickTime){
		GL.scale(0.8F, 0.8F, 0.8F);
		GL.translate(0F, 0.6F, 0F);
	}
	
	@Override
	public void doRender(EntityLiving entity, double x, double y, double z, float yaw, float partialTickTime){
		EntityMiniBossEnderEye eye = (EntityMiniBossEnderEye)entity;
		if (!eye.isAsleep() || eye.getHealth() != eye.getMaxHealth())statusTick = 130;
		
		if (statusTick > 0){
			--statusTick;
			BossStatus.setBossStatus(eye, true);
		}
		
		super.doRender(entity, x, y, z, yaw, partialTickTime);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity){
		return ((EntityMiniBossEnderEye)entity).isAsleep() ? texAsleep : texAwake;
	}
	
	@Override
	protected boolean func_110813_b(EntityLiving entity){ // OBFUSCATED show mob name
		return false;
	}
}

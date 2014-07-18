package chylex.hee.render.entity;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import chylex.hee.entity.boss.EntityBossEnderDemon;
import chylex.hee.render.model.ModelEnderDemon;
import chylex.hee.system.sound.BossType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBossEnderDemon extends RenderLiving{
	private static final ResourceLocation tex = new ResourceLocation("hardcoreenderexpansion:textures/entity/ender_demon.png");
	
	public RenderBossEnderDemon(){
		super(new ModelEnderDemon(),1.5F);
	}
	
	@Override
	protected void preRenderCallback(EntityLivingBase entity, float partialTickTime){
		GL11.glScalef(2.5f,2.5f,2.5f);
		GL11.glTranslatef(0f,0.9f,0f);
	}
	
	@Override
	public void doRender(EntityLiving entity, double x, double y, double z, float yaw, float partialTickTime){
		BossStatus.setBossStatus((EntityBossEnderDemon)entity,true);
		BossType.update(BossType.ENDER_DEMON);
		super.doRender(entity,x,y,z,yaw,partialTickTime);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity){
		return tex;
	}
}

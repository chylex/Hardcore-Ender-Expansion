package chylex.hee.render.entity;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import chylex.hee.render.model.ModelEnderDemon;
import chylex.hee.system.abstractions.GL;
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
		GL.scale(2.5F,2.5F,2.5F);
		GL.translate(0F,0.9F,0F);
	}
	
	@Override
	public void doRender(EntityLiving entity, double x, double y, double z, float yaw, float partialTickTime){
		// TODO BossStatus.setBossStatus((EntityBossEnderDemon)entity,true);
		super.doRender(entity,x,y,z,yaw,partialTickTime);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity){
		return tex;
	}
}

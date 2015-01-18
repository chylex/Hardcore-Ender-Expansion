package chylex.hee.render.entity;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import chylex.hee.entity.boss.EntityMiniBossFireFiend;
import chylex.hee.render.model.ModelFireFiend;
import chylex.hee.sound.BossType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMiniBossFireFiend extends RenderLiving{
	private static final ResourceLocation tex = new ResourceLocation("hardcoreenderexpansion:textures/entity/fire_fiend.png");

	public RenderMiniBossFireFiend(){
		super(new ModelFireFiend(),1F);
	}
	
	@Override
	protected void preRenderCallback(EntityLivingBase entity, float partialTickTime){
		GL11.glScalef(1.5F,1.5F,1.5F);
	}
	
	@Override
	public void doRender(EntityLiving entity, double x, double y, double z, float yaw, float partialTickTime){
		BossStatus.setBossStatus((EntityMiniBossFireFiend)entity,true);
		BossType.update(BossType.FIRE_FIEND);
		super.doRender(entity,x,y,z,yaw,partialTickTime);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity){
		return tex;
	}
	
	@Override
	protected boolean func_110813_b(EntityLiving entity){ // OBFUSCATED show mob name
		return false;
	}
}

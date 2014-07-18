package chylex.hee.render.entity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import chylex.hee.entity.boss.EntityMiniBossEnderEye;
import chylex.hee.render.model.ModelEnderEye;
import chylex.hee.system.sound.BossType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMiniBossEnderEye extends RenderLiving{
	private static final ResourceLocation texAwake = new ResourceLocation("hardcoreenderexpansion:textures/entity/ender_eye.png");
	private static final ResourceLocation texAsleep = new ResourceLocation("hardcoreenderexpansion:textures/entity/ender_eye_asleep.png");

	private byte statusTick = 0;
	
	public RenderMiniBossEnderEye(){
		super(new ModelEnderEye(),0.75F);
	}
	
	@Override
	protected void preRenderCallback(EntityLivingBase entity, float partialTickTime){
		GL11.glScalef(0.8F,0.8F,0.8F);
		GL11.glTranslatef(0F,0.6F,0F);
	}
	
	@Override
	public void doRender(EntityLiving entity, double x, double y, double z, float yaw, float partialTickTime){
		EntityMiniBossEnderEye eye = (EntityMiniBossEnderEye)entity;
		if (!eye.isAsleep() || eye.getHealth() != eye.getMaxHealth())statusTick = 126;
		
		if (statusTick > 0){
			--statusTick;
			BossStatus.setBossStatus(eye,true);
			
			if (!eye.isAsleep())BossType.update(BossType.ENDER_EYE);
			else{
				EntityPlayer player = Minecraft.getMinecraft().thePlayer;
				double px = eye.posX,pz = eye.posZ;
				
				if (eye.rotationYaw == 0)pz += 2;
				else if (eye.rotationYaw == 90)px -= 2;
				else if (eye.rotationYaw == 180)pz -= 2;
				else if (eye.rotationYaw == 270)px += 2;
				
				if (player.posX >= px-3.5D && player.posX <= px+3.5D &&
					player.posZ >= pz-3.5D && player.posZ <= pz+3.5D &&
					player.posY <= eye.posY+5D && player.posY >= eye.posY-26D)BossType.update(BossType.ENDER_EYE);
			}
		}
		
		super.doRender(entity,x,y,z,yaw,partialTickTime);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity){
		return ((EntityMiniBossEnderEye)entity).isAsleep()?texAsleep:texAwake;
	}
	
	@Override
	protected boolean func_110813_b(EntityLiving entity){ // OBFUSCATED show mob name
		return false;
	}
}

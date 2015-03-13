package chylex.hee.render.entity;
import java.util.Random;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import chylex.hee.entity.boss.EntityBossDragon;
import chylex.hee.mechanics.misc.Baconizer;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.render.model.ModelEnderDragon;
import chylex.hee.sound.EndMusicType;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBossDragon extends RenderLiving{
	private static final ResourceLocation texDragon = new ResourceLocation("textures/entity/enderdragon/dragon.png");
	private static final ResourceLocation texDragonEyes = new ResourceLocation("textures/entity/enderdragon/dragon_eyes.png");
	private static final ResourceLocation texCrystalBeam = new ResourceLocation("textures/entity/endercrystal/endercrystal_beam.png");
	private static final ResourceLocation texDeathExplosions = new ResourceLocation("textures/entity/enderdragon/dragon_exploding.png");

	public RenderBossDragon(){
		super(new ModelEnderDragon(),0.5F);
		setRenderPassModel(mainModel);
	}

	protected void rotateDragonBody(EntityBossDragon dragon, float entityTickTime, float yawOffset, float partialTickTime){
		GL11.glRotatef(-(float)dragon.getMovementOffsets(7,partialTickTime)[0],0F,1F,0F);
		GL11.glRotatef(10F*((float)(dragon.getMovementOffsets(5,partialTickTime)[1]-dragon.getMovementOffsets(10,partialTickTime)[1])),1F,0F,0F);
		GL11.glTranslatef(0F,0F,1F);

		if (dragon.deathTime > 0){
			GL11.glRotatef(Math.min(1F,MathHelper.sqrt_float((dragon.deathTime+partialTickTime-1F)/20F*1.6F))*getDeathMaxRotation(dragon),0F,0F,1F);
		}
	}

	protected void renderDragonModel(EntityBossDragon dragon, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel){
		if (dragon.deathTicks > 0){
			GL11.glDepthFunc(GL11.GL_LEQUAL);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			GL11.glAlphaFunc(GL11.GL_GREATER,dragon.deathTicks*0.005F);
			bindTexture(texDeathExplosions);
			mainModel.render(dragon,limbSwing,limbSwingAngle,entityTickTime,rotationYaw,rotationPitch,unitPixel);
			GL11.glAlphaFunc(GL11.GL_GREATER,0.1F);
			GL11.glDepthFunc(GL11.GL_EQUAL);
		}

		bindEntityTexture(dragon);
		mainModel.render(dragon,limbSwing,limbSwingAngle,entityTickTime,rotationYaw,rotationPitch,unitPixel);

		if (dragon.hurtTime > 0){
			GL11.glDepthFunc(GL11.GL_EQUAL);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glColor4f(1F,0F,0F,0.5F);
			mainModel.render(dragon,limbSwing,limbSwingAngle,entityTickTime,rotationYaw,rotationPitch,unitPixel);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glDepthFunc(GL11.GL_LEQUAL);
		}
	}

	public void renderDragon(EntityBossDragon dragon, double x, double y, double z, float yaw, float partialTickTime){
		BossStatus.setBossStatus(dragon,false);
		BossStatus.bossName = (dragon.isAngry() ? EnumChatFormatting.LIGHT_PURPLE : "")+I18n.format(dragon.getCommandSenderName());
		EndMusicType.update(dragon.isAngry() ? EndMusicType.DRAGON_ANGRY : EndMusicType.DRAGON_CALM);
		super.doRender(dragon,x,y,z,yaw,partialTickTime);

		if (dragon.healingEnderCrystal != null){
			float animRot = dragon.healingEnderCrystal.innerRotation+partialTickTime;
			float yCorrection = MathHelper.sin(animRot*0.2F)*0.5F+0.5F;
			yCorrection = (yCorrection*yCorrection+yCorrection)*0.2F;
			float diffX = (float)(dragon.healingEnderCrystal.posX-dragon.posX-(dragon.prevPosX-dragon.posX)*(1F-partialTickTime));
			float diffY = (float)(yCorrection+dragon.healingEnderCrystal.posY-1D-dragon.posY-(dragon.prevPosY-dragon.posY)*(1F-partialTickTime));
			float diffZ = (float)(dragon.healingEnderCrystal.posZ-dragon.posZ-(dragon.prevPosZ-dragon.posZ)*(1F-partialTickTime));
			float distXZ = MathHelper.sqrt_float(diffX*diffX+diffZ*diffZ);
			float distXYZ = MathHelper.sqrt_float(diffX*diffX+diffY*diffY+diffZ*diffZ);
			GL11.glPushMatrix();
			GL11.glTranslatef((float)x,(float)y+2F,(float)z);
			GL11.glRotatef(MathUtil.toDeg((float)-Math.atan2(diffZ,diffX))-90F,0F,1F,0F);
			GL11.glRotatef(MathUtil.toDeg((float)-Math.atan2(distXZ,diffY))-90F,1F,0F,0F);
			Tessellator tessellator = Tessellator.instance;
			RenderHelper.disableStandardItemLighting();
			GL11.glDisable(GL11.GL_CULL_FACE);
			bindTexture(texCrystalBeam);
			GL11.glShadeModel(GL11.GL_SMOOTH);
			float animTime = -(dragon.ticksExisted+partialTickTime)*0.01F;
			float textureV = MathHelper.sqrt_float(diffX*diffX+diffY*diffY+diffZ*diffZ)*0.03125F-(dragon.ticksExisted+partialTickTime)*0.01F;
			tessellator.startDrawing(5);
			byte sideAmount = 8;

			for(int i = 0; i <= sideAmount; ++i){
				float f11 = MathHelper.sin((i%sideAmount)*(float)Math.PI*2F/sideAmount)*0.75F;
				float f12 = MathHelper.cos((i%sideAmount)*(float)Math.PI*2F/sideAmount)*0.75F;
				float f13 = (i%sideAmount)/sideAmount;
				tessellator.setColorOpaque_I(0);
				tessellator.addVertexWithUV(f11*0.2F,f12*0.2F,0D,f13,textureV);
				tessellator.setColorOpaque_I(16777215);
				tessellator.addVertexWithUV(f11,f12,distXYZ,f13,animTime);
			}

			tessellator.draw();
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glShadeModel(GL11.GL_FLAT);
			RenderHelper.enableStandardItemLighting();
			GL11.glPopMatrix();
		}
	}

	protected void renderDragonDying(EntityBossDragon dragon, float partialTickTime){
		super.renderEquippedItems(dragon,partialTickTime);

		if (dragon.deathTicks > 0){
			Tessellator tessellator = Tessellator.instance;
			RenderHelper.disableStandardItemLighting();
			float animPerc = (dragon.deathTicks+partialTickTime)*0.005F;
			float fade = animPerc > 0.8F ? (animPerc-0.8F)*5F : 0F;

			Random random = new Random(432L);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glShadeModel(GL11.GL_SMOOTH);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glDepthMask(false);
			GL11.glPushMatrix();
			GL11.glTranslatef(0F,-1F,-2F);

			for(int beam = 0; beam < (animPerc+animPerc*animPerc)/2F*60F; ++beam){
				GL11.glRotatef(random.nextFloat()*360F,1F,0F,0F);
				GL11.glRotatef(random.nextFloat()*360F,0F,1F,0F);
				GL11.glRotatef(random.nextFloat()*360F,0F,0F,1F);
				GL11.glRotatef(random.nextFloat()*360F,1F,0F,0F);
				GL11.glRotatef(random.nextFloat()*360F,0F,1F,0F);
				GL11.glRotatef(random.nextFloat()*360F+animPerc*90F,0F,0F,1F);
				tessellator.startDrawing(6);
				float yRot = random.nextFloat()*20F+5F+fade*10F;
				float xzRot = random.nextFloat()*2F+1F+fade*2F;
				tessellator.setColorRGBA_I(16777215,(int)(255F*(1F-fade)));
				tessellator.addVertex(0D,0D,0D);
				tessellator.setColorRGBA_I(16711935,0);
				tessellator.addVertex(-0.866D*xzRot,yRot,-0.5F*xzRot);
				tessellator.addVertex(0.866D*xzRot,yRot,-0.5F*xzRot);
				tessellator.addVertex(0D,yRot,xzRot);
				tessellator.addVertex(-0.866D*xzRot,yRot,-0.5F*xzRot);
				tessellator.draw();
			}

			GL11.glPopMatrix();
			GL11.glDepthMask(true);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glShadeModel(GL11.GL_FLAT);
			GL11.glColor4f(1F,1F,1F,1F);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
			RenderHelper.enableStandardItemLighting();
		}
	}

	protected int renderGlow(EntityBossDragon dragon, int pass, float partialTickTime){
		if (pass == 1)GL11.glDepthFunc(GL11.GL_LEQUAL);
		if (pass != 0 || ModCommonProxy.hardcoreEnderbacon)return -1;
		else{
			bindTexture(texDragonEyes);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glBlendFunc(GL11.GL_ONE,GL11.GL_ONE);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDepthFunc(GL11.GL_EQUAL);
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,61680%65536,61680/65536);
			GL11.glColor4f(1F,1F,1F,1F);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glColor4f(1F,1F,1F,1F);
			return 1;
		}
	}

	@Override
	public void doRender(EntityLiving entity, double x, double y, double z, float yaw, float partialTickTime){
		renderDragon((EntityBossDragon)entity,x,y,z,yaw,partialTickTime);
	}

	@Override
	protected int shouldRenderPass(EntityLivingBase entity, int pass, float partialTickTime){
		return renderGlow((EntityBossDragon)entity,pass,partialTickTime);
	}

	@Override
	protected void renderEquippedItems(EntityLivingBase entity, float partialTickTime){
		renderDragonDying((EntityBossDragon)entity,partialTickTime);
	}

	@Override
	protected void rotateCorpse(EntityLivingBase entity, float entityTickTime, float yawOffset, float partialTickTime){
		rotateDragonBody((EntityBossDragon)entity,entityTickTime,yawOffset,partialTickTime);
	}

	@Override
	protected void renderModel(EntityLivingBase entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel){
		renderDragonModel((EntityBossDragon)entity,limbSwing,limbSwingAngle,entityTickTime,rotationYaw,rotationPitch,unitPixel);
	}

	@Override
	public void doRender(EntityLivingBase entity, double x, double y, double z, float yaw, float partialTickTime){
		renderDragon((EntityBossDragon)entity,x,y,z,yaw,partialTickTime);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity){
		return Baconizer.mobTexture(this,texDragon);
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTickTime){
		renderDragon((EntityBossDragon)entity,x,y,z,yaw,partialTickTime);
	}
}

package chylex.hee.render.model;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;
import chylex.hee.entity.boss.EntityBossDragon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelEnderDragon extends ModelBase{
	private ModelRenderer head;
	private ModelRenderer neck;
	private ModelRenderer jaw;
	private ModelRenderer body;
	private ModelRenderer rearLeg;
	private ModelRenderer frontLeg;
	private ModelRenderer rearLegTip;
	private ModelRenderer frontLegTip;
	private ModelRenderer rearFoot;
	private ModelRenderer frontFoot;
	private ModelRenderer wing;
	private ModelRenderer wingTip;
	private float partialTicks;

	public ModelEnderDragon(){
		textureWidth = 256;
		textureHeight = 256;
		setTextureOffset("body.body",0,0);
		setTextureOffset("wing.skin",-56,88);
		setTextureOffset("wingtip.skin",-56,144);
		setTextureOffset("rearleg.main",0,0);
		setTextureOffset("rearfoot.main",112,0);
		setTextureOffset("rearlegtip.main",196,0);
		setTextureOffset("head.upperhead",112,30);
		setTextureOffset("wing.bone",112,88);
		setTextureOffset("head.upperlip",176,44);
		setTextureOffset("jaw.jaw",176,65);
		setTextureOffset("frontleg.main",112,104);
		setTextureOffset("wingtip.bone",112,136);
		setTextureOffset("frontfoot.main",144,104);
		setTextureOffset("neck.box",192,104);
		setTextureOffset("frontlegtip.main",226,138);
		setTextureOffset("body.scale",220,53);
		setTextureOffset("head.scale",0,0);
		setTextureOffset("neck.scale",48,0);
		setTextureOffset("head.nostril",112,0);
		
		float f1 = -16F;
		head = new ModelRenderer(this,"head");
		head.addBox("upperlip",-6F,-1F,-8F+f1,12,5,16);
		head.addBox("upperhead",-8F,-8F,6F+f1,16,16,16);
		head.mirror = true;
		head.addBox("scale",-5F,-12F,12F+f1,2,4,6);
		head.addBox("nostril",-5F,-3F,-6F+f1,2,2,4);
		head.mirror = false;
		head.addBox("scale",3F,-12F,12F+f1,2,4,6);
		head.addBox("nostril",3F,-3F,-6F+f1,2,2,4);
		
		jaw = new ModelRenderer(this,"jaw");
		jaw.setRotationPoint(0F,4F,8F+f1);
		jaw.addBox("jaw",-6F,0F,-16F,12,4,16);
		head.addChild(jaw);
		
		neck = new ModelRenderer(this,"neck");
		neck.addBox("box",-5F,-5F,-5F,10,10,10);
		neck.addBox("scale",-1F,-9F,-3F,2,4,6);
		
		body = new ModelRenderer(this,"body");
		body.setRotationPoint(0F,4F,8F);
		body.addBox("body",-12F,0F,-16F,24,24,64);
		body.addBox("scale",-1F,-6F,-10F,2,6,12);
		body.addBox("scale",-1F,-6F,10F,2,6,12);
		body.addBox("scale",-1F,-6F,30F,2,6,12);
		
		wing = new ModelRenderer(this,"wing");
		wing.setRotationPoint(-12F,5F,2F);
		wing.addBox("bone",-56F,-4F,-4F,56,8,8);
		wing.addBox("skin",-56F,0F,2F,56,0,56);
		
		wingTip = new ModelRenderer(this,"wingtip");
		wingTip.setRotationPoint(-56F,0F,0F);
		wingTip.addBox("bone",-56F,-2F,-2F,56,4,4);
		wingTip.addBox("skin",-56F,0F,2F,56,0,56);
		wing.addChild(wingTip);
		
		frontLeg = new ModelRenderer(this,"frontleg");
		frontLeg.setRotationPoint(-12F,20F,2F);
		frontLeg.addBox("main",-4F,-4F,-4F,8,24,8);
		
		frontLegTip = new ModelRenderer(this,"frontlegtip");
		frontLegTip.setRotationPoint(0F,20F,-1F);
		frontLegTip.addBox("main",-3F,-1F,-3F,6,24,6);
		frontLeg.addChild(frontLegTip);
		
		frontFoot = new ModelRenderer(this,"frontfoot");
		frontFoot.setRotationPoint(0F,23F,0F);
		frontFoot.addBox("main",-4F,0F,-12F,8,4,16);
		frontLegTip.addChild(frontFoot);
		
		rearLeg = new ModelRenderer(this,"rearleg");
		rearLeg.setRotationPoint(-16F,16F,42F);
		rearLeg.addBox("main",-8F,-4F,-8F,16,32,16);
		
		rearLegTip = new ModelRenderer(this,"rearlegtip");
		rearLegTip.setRotationPoint(0F,32F,-4F);
		rearLegTip.addBox("main",-6F,-2F,0F,12,32,12);
		rearLeg.addChild(rearLegTip);
		
		rearFoot = new ModelRenderer(this,"rearfoot");
		rearFoot.setRotationPoint(0F,31F,4F);
		rearFoot.addBox("main",-9F,0F,-20F,18,6,24);
		rearLegTip.addChild(rearFoot);
	}

	@Override
	public void setLivingAnimations(EntityLivingBase entity, float par2, float par3, float partialTickTime){
		partialTicks = partialTickTime;
	}

	@Override
	public void render(Entity entity, float limbSwing, float prevLimbSwing, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel){
		GL11.glPushMatrix();
		EntityBossDragon dragon = (EntityBossDragon)entity;
		float animTime = dragon.prevAnimTime+(dragon.animTime-dragon.prevAnimTime)*partialTicks;
		jaw.rotateAngleX = (float)(Math.sin((animTime*(float)Math.PI*2F))+1D)*0.2F;
		float f7 = (float)(Math.sin((animTime*(float)Math.PI*2F-1F))+1D);
		f7 = (f7*f7+f7*2F)*0.05F;
		GL11.glTranslatef(0F,f7-2F,-3F);
		GL11.glRotatef(f7*2F,1F,0F,0F);
		float f8 = -30F;
		float f9 = 0F;
		float f10 = 1.5F;
		double[] adouble = dragon.getMovementOffsets(6,partialTicks);
		float f11 = updateRotations(dragon.getMovementOffsets(5,partialTicks)[0]-dragon.getMovementOffsets(10,partialTicks)[0]);
		float f12 = updateRotations(dragon.getMovementOffsets(5,partialTicks)[0]+(f11/2F));
		f8 += 2F;
		float f13 = animTime*(float)Math.PI*2F;
		f8 = 20F;
		float f14 = -12F;
		float f15;

		for(int neckPart = 0; neckPart < 5; ++neckPart){
			double[] adouble1 = dragon.getMovementOffsets(5-neckPart,partialTicks);
			f15 = (float)Math.cos((neckPart*0.45F+f13))*0.15F;
			neck.rotateAngleY = updateRotations(adouble1[0]-adouble[0])*(float)Math.PI/180F*f10;
			neck.rotateAngleX = f15+(float)(adouble1[1]-adouble[1])*(float)Math.PI/180F*f10*5F;
			neck.rotateAngleZ = -updateRotations(adouble1[0]-f12)*(float)Math.PI/180F*f10;
			neck.rotationPointY = f8;
			neck.rotationPointZ = f14;
			neck.rotationPointX = f9;
			f8 = (float)(f8+Math.sin(neck.rotateAngleX)*10D);
			f14 = (float)(f14-Math.cos(neck.rotateAngleY)*Math.cos(neck.rotateAngleX)*10D);
			f9 = (float)(f9-Math.sin(neck.rotateAngleY)*Math.cos(neck.rotateAngleX)*10D);
			neck.render(unitPixel);
		}

		head.rotationPointY = f8;
		head.rotationPointZ = f14;
		head.rotationPointX = f9;
		double[] adouble2 = dragon.getMovementOffsets(0,partialTicks);
		head.rotateAngleY = updateRotations(adouble2[0]-adouble[0])*(float)Math.PI/180F;
		head.rotateAngleZ = -updateRotations(adouble2[0]-f12)*(float)Math.PI/180F;
		head.render(unitPixel);
		GL11.glPushMatrix();
		GL11.glTranslatef(0F,1F,0F);
		GL11.glRotatef(-f11*f10,0F,0F,1F);
		GL11.glTranslatef(0F,-1F,0F);
		body.rotateAngleZ = 0F;
		body.render(unitPixel);

		for(int a = 0; a < 2; ++a){
			GL11.glEnable(GL11.GL_CULL_FACE);
			f15 = animTime*(float)Math.PI*2F;
			wing.rotateAngleX = 0.125F-(float)Math.cos(f15)*0.2F;
			wing.rotateAngleY = 0.25F;
			wing.rotateAngleZ = (float)(Math.sin(f15)+0.125D)*0.8F;
			wingTip.rotateAngleZ = -((float)(Math.sin((f15+2F))+0.5D))*0.75F;
			rearLeg.rotateAngleX = 1F+f7*0.1F;
			rearLegTip.rotateAngleX = 0.5F+f7*0.1F;
			rearFoot.rotateAngleX = 0.75F+f7*0.1F;
			frontLeg.rotateAngleX = 1.3F+f7*0.1F;
			frontLegTip.rotateAngleX = -0.5F-f7*0.1F;
			frontFoot.rotateAngleX = 0.75F+f7*0.1F;
			wing.render(unitPixel);
			frontLeg.render(unitPixel);
			rearLeg.render(unitPixel);
			GL11.glScalef(-1F,1F,1F);

			if (a == 0)GL11.glCullFace(GL11.GL_FRONT);
		}

		GL11.glPopMatrix();
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glDisable(GL11.GL_CULL_FACE);
		float f16 = -((float)Math.sin((animTime*(float)Math.PI*2F)))*0F;
		f13 = animTime*(float)Math.PI*2F;
		f8 = 10F;
		f14 = 60F;
		f9 = 0F;
		adouble = dragon.getMovementOffsets(11,partialTicks);

		for(int neckPart = 0; neckPart < 12; ++neckPart){
			adouble2 = dragon.getMovementOffsets(12+neckPart,partialTicks);
			f16 = (float)(f16+Math.sin((neckPart*0.45F+f13))*0.05D);
			neck.rotateAngleY = (updateRotations(adouble2[0]-adouble[0])*f10+180F)*(float)Math.PI/180F;
			neck.rotateAngleX = f16+(float)(adouble2[1]-adouble[1])*(float)Math.PI/180F*f10*5F;
			neck.rotateAngleZ = updateRotations(adouble2[0]-f12)*(float)Math.PI/180F*f10;
			neck.rotationPointY = f8;
			neck.rotationPointZ = f14;
			neck.rotationPointX = f9;
			f8 = (float)(f8+Math.sin(neck.rotateAngleX)*10D);
			f14 = (float)(f14-Math.cos(neck.rotateAngleY)*Math.cos(neck.rotateAngleX)*10D);
			f9 = (float)(f9-Math.sin(neck.rotateAngleY)*Math.cos(neck.rotateAngleX)*10D);
			neck.render(unitPixel);
		}

		GL11.glPopMatrix();
	}

	private float updateRotations(double angle){
		while(angle >= 180D)angle -= 360D;
		while(angle < -180D)angle += 360D;
		return (float)angle;
	}
}

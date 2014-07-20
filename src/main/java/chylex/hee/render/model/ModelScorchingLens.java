package chylex.hee.render.model;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelScorchingLens extends ModelBase{
	ModelRenderer body;
	ModelRenderer rightLeg1,rightLeg2,leftLeg1,leftLeg2;
	ModelRenderer rightFoot,RToeIn,RToeOut,leftFoot,LToeIn,LToeOut;

	public ModelScorchingLens() {
		textureWidth = 64;
		textureHeight = 32;

		rightLeg1 = new ModelRenderer(this,41,0);
		rightLeg1.addBox(-3F,-1F,-1.5F,3,6,3);
		rightLeg1.setRotationPoint(-5F,17F,0F);
		rightLeg1.setTextureSize(64,32);
		rightLeg1.mirror = true;
		setRotation(rightLeg1,1.047198F,0F,0F);
		
		rightLeg2 = new ModelRenderer(this,41,10);
		rightLeg2.addBox(-2.5F,1F,2.5F,2,4,2);
		rightLeg2.setTextureSize(64,32);
		rightLeg2.mirror = true;
		setRotation(rightLeg2,-0.5235988F,0F,0F);
		
		rightFoot = new ModelRenderer(this,41,17);
		rightFoot.addBox(-3F,5F,-2.5F,3,2,5);
		rightFoot.setRotationPoint(-5F,17F,0F);
		rightFoot.setTextureSize(64,32);
		rightFoot.mirror = true;
		setRotation(rightFoot,0F,0F,0F);
		
		RToeIn = new ModelRenderer(this,41,25);
		RToeIn.addBox(-1F,6F,-4.5F,1,1,2);
		RToeIn.setTextureSize(64,32);
		RToeIn.mirror = true;
		setRotation(RToeIn,0F,0F,0F);
		
		RToeOut = new ModelRenderer(this,41,25);
		RToeOut.addBox(-3F,6F,-4.5F,1,1,2);
		RToeOut.setTextureSize(64,32);
		RToeOut.mirror = true;
		setRotation(RToeOut,0F,0F,0F);
		
		leftLeg1 = new ModelRenderer(this,41,0);
		leftLeg1.addBox(0F,-1F,-1.5F,3,6,3);
		leftLeg1.setRotationPoint(5F,17F,0F);
		leftLeg1.setTextureSize(64,32);
		leftLeg1.mirror = true;
		setRotation(leftLeg1,1.047198F,0F,0F);
		
		leftLeg2 = new ModelRenderer(this,41,10);
		leftLeg2.addBox(0.5F,1F,2.5F,2,4,2);
		leftLeg2.setTextureSize(64,32);
		leftLeg2.mirror = true;
		setRotation(leftLeg2,-0.5235988F,0F,0F);
		
		leftFoot = new ModelRenderer(this,41,17);
		leftFoot.addBox(0F,5F,-2.5F,3,2,5);
		leftFoot.setRotationPoint(5F,17F,0F);
		leftFoot.setTextureSize(64,32);
		leftFoot.mirror = true;
		setRotation(leftFoot,0F,0F,0F);
		
		LToeIn = new ModelRenderer(this,41,25);
		LToeIn.addBox(0F,6F,-4.5F,1,1,2);
		LToeIn.setTextureSize(64,32);
		LToeIn.mirror = true;
		setRotation(LToeIn,0F,0F,0F);
		
		LToeOut = new ModelRenderer(this,41,25);
		LToeOut.addBox(2F,6F,-4.5F,1,1,2);
		LToeOut.setTextureSize(64,32);
		LToeOut.mirror = true;
		setRotation(LToeOut,0F,0F,0F);
		
		body = new ModelRenderer(this,0,0);
		body.addBox(-5F,-7F,-5F,10,10,10);
		body.setRotationPoint(0F,17F,0F);
		body.setTextureSize(64,32);
		body.mirror = true;
		setRotation(body,0F,0F,0F);

		rightLeg1.addChild(rightLeg2);
		rightFoot.addChild(RToeOut);
		rightFoot.addChild(RToeIn);
		leftLeg1.addChild(leftLeg2);
		leftFoot.addChild(LToeOut);
		leftFoot.addChild(LToeIn);
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel){
		super.render(entity,limbSwing,limbSwingAngle,entityTickTime,rotationYaw,rotationPitch,unitPixel);
		setRotationAngles(limbSwing,limbSwingAngle,entityTickTime,rotationYaw,rotationPitch,unitPixel,entity);
		
		rightLeg1.render(unitPixel);
		rightFoot.render(unitPixel);
		leftLeg1.render(unitPixel);
		leftFoot.render(unitPixel);
		body.render(unitPixel);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
	
	private static final float lx = 0.5235988F;
	private static final float ex = 1.047198F;
	
	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity){
		super.setRotationAngles(limbSwing,limbSwingAngle,entityTickTime,rotationYaw,rotationPitch,unitPixel,entity);

		float swing = MathHelper.sin(limbSwing*0.7F)*1.2F*limbSwingAngle;

		body.rotateAngleX = MathUtil.toRad(rotationPitch);
		rightLeg1.rotateAngleX = ex-swing;
		leftLeg1.rotateAngleX = ex+swing;
		rightLeg2.rotateAngleX = -lx-ex;
		leftLeg2.rotateAngleX = -lx-ex;
		rightFoot.rotateAngleX = -swing;
		leftFoot.rotateAngleX = +swing;
	}
}

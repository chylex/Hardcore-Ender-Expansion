package chylex.hee.render.model;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import chylex.hee.entity.mob.EntityMobEnderGuardian;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelEnderGuardian extends ModelBase{
	private final ModelRenderer rightHorn, leftHorn;
	private final ModelRenderer head, bodyTop, bodyBottom;
	private final ModelRenderer rightArm, leftArm;
	private final ModelRenderer rightLeg, leftLeg;

	public ModelEnderGuardian(){
		textureWidth = 128;
		textureHeight = 64;
		
		rightHorn = new ModelRenderer(this,63,32);
		rightHorn.addBox(-4F,-11F,-6F,2,1,6);
		rightHorn.setRotationPoint(0F,-18F,0F);
		rightHorn.setTextureSize(128,64);
		rightHorn.mirror = true;
		setRotation(rightHorn,0F,0F,0F);
		
		leftHorn = new ModelRenderer(this,62,32);
		leftHorn.addBox(2F,-11F,-6F,2,1,6);
		leftHorn.setRotationPoint(0F,-18F,0F);
		leftHorn.setTextureSize(128,64);
		leftHorn.mirror = true;
		setRotation(leftHorn,0F,0F,0F);

		head = new ModelRenderer(this,0,0);
		head.addBox(-5F,-6F,-8F,10,10,10);
		head.setRotationPoint(0F,-22F,0F);
		head.setTextureSize(128,64);
		head.mirror = true;
		setRotation(head,0F,0F,0F);
		
		bodyTop = new ModelRenderer(this,62,0);
		bodyTop.addBox(-8F,0F,-4F,16,19,12);
		bodyTop.setRotationPoint(0F,-18F,0F);
		bodyTop.setTextureSize(128,64);
		bodyTop.mirror = true;
		setRotation(bodyTop,0F,0F,0F);
		
		bodyBottom = new ModelRenderer(this,0,21);
		bodyBottom.addBox(-6F,17F,-3F,12,6,10);
		bodyBottom.setRotationPoint(0F,-16F,0F);
		bodyBottom.setTextureSize(128,64);
		bodyBottom.mirror = true;
		setRotation(bodyBottom,0F,0F,0F);
		
		rightArm = new ModelRenderer(this,45,0);
		rightArm.addBox(-4F,-2F,-2F,4,28,4);
		rightArm.setRotationPoint(-8F,-14F,2F);
		rightArm.setTextureSize(128,64);
		rightArm.mirror = true;
		setRotation(rightArm,0F,0F,0F);
		
		leftArm = new ModelRenderer(this,45,0);
		leftArm.addBox(0F,-2F,-2F,4,28,4);
		leftArm.setRotationPoint(8F,-14F,2F);
		leftArm.setTextureSize(128,64);
		leftArm.mirror = true;
		setRotation(leftArm,0F,0F,0F);
		
		rightLeg = new ModelRenderer(this,45,33);
		rightLeg.addBox(-2F,0F,-2F,4,17,4);
		rightLeg.setRotationPoint(-4F,7F,3F);
		rightLeg.setTextureSize(128,64);
		rightLeg.mirror = true;
		setRotation(rightLeg,0F,0F,0F);
		
		leftLeg = new ModelRenderer(this,45,33);
		leftLeg.addBox(-2F,0F,-2F,4,17,4);
		leftLeg.setRotationPoint(4F,7F,3F);
		leftLeg.setTextureSize(128,64);
		leftLeg.mirror = true;
		setRotation(leftLeg,0F,0F,0F);
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel){
		super.render(entity,limbSwing,limbSwingAngle,entityTickTime,rotationYaw,rotationPitch,unitPixel);
		setRotationAngles(limbSwing,limbSwingAngle,entityTickTime,rotationYaw,rotationPitch,unitPixel,entity);
		
		rightHorn.render(unitPixel);
		leftHorn.render(unitPixel);
		head.render(unitPixel);
		bodyTop.render(unitPixel);
		bodyBottom.render(unitPixel);
		rightArm.render(unitPixel);
		leftArm.render(unitPixel);
		rightLeg.render(unitPixel);
		leftLeg.render(unitPixel);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z){
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity){
		rightHorn.rotateAngleY = leftHorn.rotateAngleY = head.rotateAngleY = MathUtil.toRad(rotationYaw);
		rightHorn.rotateAngleX = leftHorn.rotateAngleX = head.rotateAngleX = MathUtil.toRad(rotationPitch);
	}
	
	@Override
	public void setLivingAnimations(EntityLivingBase entity, float limbSwing, float limbSwingAngle, float partialTickTime){
		float limbSwing1 = MathHelper.cos(limbSwing*0.6662F),
			  limbSwing2 = MathHelper.cos(limbSwing*0.6662F+(float)Math.PI);
		
		rightArm.rotateAngleX = limbSwing2*limbSwingAngle;
		leftArm.rotateAngleX = limbSwing1*limbSwingAngle;
		rightLeg.rotateAngleX = limbSwing1*1.4F*limbSwingAngle;
		leftLeg.rotateAngleX = limbSwing2*1.4F*limbSwingAngle;
		
		rightArm.rotateAngleX = (float)(rightArm.rotateAngleX*0.5D);
		leftArm.rotateAngleX = (float)(leftArm.rotateAngleX*0.5D);
		rightLeg.rotateAngleX = (float)(rightLeg.rotateAngleX*0.5D);
		leftLeg.rotateAngleX = (float)(leftLeg.rotateAngleX*0.5D);
		
		float animLimit = 0.5F;
		if (rightArm.rotateAngleX > animLimit)rightArm.rotateAngleX = animLimit;
		if (leftArm.rotateAngleX > animLimit)leftArm.rotateAngleX = animLimit;
		if (rightArm.rotateAngleX < -animLimit)rightArm.rotateAngleX = -animLimit;
		if (leftArm.rotateAngleX < -animLimit)leftArm.rotateAngleX = -animLimit;
		if (rightLeg.rotateAngleX > animLimit)rightLeg.rotateAngleX = animLimit;
		if (leftLeg.rotateAngleX > animLimit)leftLeg.rotateAngleX = animLimit;
		if (rightLeg.rotateAngleX < -animLimit)rightLeg.rotateAngleX = -animLimit;
		if (leftLeg.rotateAngleX < -animLimit)leftLeg.rotateAngleX = -animLimit;
		
		int attack = ((EntityMobEnderGuardian)entity).getAttackTimerClient();
		
		if (attack > 0){
			rightArm.rotateAngleX = (-2F+1.5F*adjustAnimation(attack-partialTickTime,8F))*0.5F;
			leftArm.rotateAngleX = (-2F+1.5F*adjustAnimation(attack-partialTickTime,8F))*0.5F;
		}
	}
	
	private float adjustAnimation(float value, float maxValue){
		return (Math.abs(value%maxValue-maxValue*0.5F)-maxValue*0.25F)/(maxValue*0.25F);
	}
}

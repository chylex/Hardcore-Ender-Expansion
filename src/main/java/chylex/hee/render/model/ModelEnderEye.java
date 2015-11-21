package chylex.hee.render.model;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import chylex.hee.entity.boss.EntityMiniBossEnderEye;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelEnderEye extends ModelBase{
	private ModelRenderer head;
	private ModelRenderer rightArm;
	private ModelRenderer leftArm;
	private float wakeupAngle, animationAngle;

	public ModelEnderEye(){
		textureWidth = 128;
		textureHeight = 64;
		
		head = new ModelRenderer(this,0,0);
		head.addBox(-9F,-8F,-9F,18,18,18);
		head.setRotationPoint(0F,0F,0F);
		head.setTextureSize(128,64);
		head.mirror = true;
		setRotation(head,0F,0F,0F);
		
		rightArm = new ModelRenderer(this,116,0);
		rightArm.addBox(-12F,-27F,-3F,3,30,3);
		rightArm.setRotationPoint(0F,0F,0F);
		rightArm.setTextureSize(128,64);
		rightArm.mirror = true;
		setRotation(rightArm,MathUtil.HALF_PI,0F,0F);
		
		leftArm = new ModelRenderer(this,116,0);
		leftArm.addBox(9F,-27F,-3F,3,30,3);
		leftArm.setRotationPoint(0F,0F,0F);
		leftArm.setTextureSize(128,64);
		leftArm.mirror = true;
		setRotation(leftArm,MathUtil.HALF_PI,0F,0F);
		leftArm.mirror = false;
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel){
		super.render(entity,limbSwing,limbSwingAngle,entityTickTime,rotationYaw,rotationPitch,unitPixel);
		setRotationAngles(limbSwing,limbSwingAngle,entityTickTime,rotationYaw,rotationPitch,unitPixel,entity);
		
		head.render(unitPixel);
		rightArm.render(unitPixel);
		leftArm.render(unitPixel);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z){
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
	
	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity){
		super.setRotationAngles(limbSwing,limbSwingAngle,entityTickTime,rotationYaw,rotationPitch,unitPixel,entity);
		
		float yawRad = MathUtil.toRad(rotationYaw),
			  pitchRad = -MathUtil.toRad(rotationPitch);
		
		EntityMiniBossEnderEye eye = (EntityMiniBossEnderEye)entity;
		byte anim = eye.getAttackAnimationTime();
		if (anim == 0 && animationAngle > 0F)animationAngle = Math.max(0F,animationAngle-0.10472F);
		else if (anim > 0)animationAngle = Math.min(MathUtil.HALF_PI,animationAngle+0.071399F);
		
		if (eye.isAsleep())wakeupAngle = Math.min(-pitchRad+MathUtil.HALF_PI,wakeupAngle+0.098175F);
		else if (wakeupAngle != 0F)wakeupAngle = Math.max(0F,wakeupAngle-0.098175F);

		head.rotateAngleY = yawRad;
		head.rotateAngleX = pitchRad;
		leftArm.rotateAngleY = yawRad;
		leftArm.rotateAngleX = pitchRad+1.570796F-animationAngle+wakeupAngle;
		rightArm.rotateAngleY = yawRad;
		rightArm.rotateAngleX = pitchRad+1.570796F-animationAngle+wakeupAngle;
	}
}

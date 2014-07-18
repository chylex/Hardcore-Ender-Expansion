package chylex.hee.render.model;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import chylex.hee.entity.boss.EntityMiniBossEnderEye;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelEnderEye extends ModelBase{
	ModelRenderer Head;
	ModelRenderer RightArm;
	ModelRenderer LeftArm;
	private float wakeupAngle,animationAngle;

	public ModelEnderEye(){
		textureWidth = 128;
		textureHeight = 64;
		
		Head = new ModelRenderer(this,0,0);
		Head.addBox(-9F,-8F,-9F,18,18,18);
		Head.setRotationPoint(0F,0F,0F);
		Head.setTextureSize(128,64);
		Head.mirror = true;
		setRotation(Head,0F,0F,0F);
		
		RightArm = new ModelRenderer(this,116,0);
		RightArm.addBox(-12F,-27F,-3F,3,30,3);
		RightArm.setRotationPoint(0F,0F,0F);
		RightArm.setTextureSize(128,64);
		RightArm.mirror = true;
		setRotation(RightArm,1.570796F,0F,0F);
		
		LeftArm = new ModelRenderer(this,116,0);
		LeftArm.addBox(9F,-27F,-3F,3,30,3);
		LeftArm.setRotationPoint(0F,0F,0F);
		LeftArm.setTextureSize(128,64);
		LeftArm.mirror = true;
		setRotation(LeftArm,1.570796F,0F,0F);
		LeftArm.mirror = false;
	}

	@Override
	public void render(Entity entity, float limbSwing, float prevLimbSwing, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel){
		super.render(entity,limbSwing,prevLimbSwing,entityTickTime,rotationYaw,rotationPitch,unitPixel);
		setRotationAngles(limbSwing,prevLimbSwing,entityTickTime,rotationYaw,rotationPitch,unitPixel,entity);
		
		Head.render(unitPixel);
		RightArm.render(unitPixel);
		LeftArm.render(unitPixel);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z){
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
	
	@Override
	public void setRotationAngles(float limbSwing, float prevLimbSwing, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity){
		super.setRotationAngles(limbSwing,prevLimbSwing,entityTickTime,rotationYaw,rotationPitch,unitPixel,entity);
		
		float yawRad = rotationYaw/(180F/(float)Math.PI),
			  pitchRad = (-rotationPitch)/(180F/(float)Math.PI);
		
		EntityMiniBossEnderEye eye = (EntityMiniBossEnderEye)entity;
		byte anim = eye.getAttackAnimationTime();
		if (anim == 0 && animationAngle > 0F)animationAngle = Math.max(0F,animationAngle-0.10472F);
		else if (anim > 0)animationAngle = Math.min(1.570796F,animationAngle+0.071399F);
		
		if (eye.isAsleep())wakeupAngle = Math.min(-pitchRad+1.570796F,wakeupAngle+0.098175F);
		else if (wakeupAngle != 0F)wakeupAngle = Math.max(0F,wakeupAngle-0.098175F);

		Head.rotateAngleY = yawRad;
		Head.rotateAngleX = pitchRad;
		LeftArm.rotateAngleY = yawRad;
		LeftArm.rotateAngleX = pitchRad+1.570796F-animationAngle+wakeupAngle;
		RightArm.rotateAngleY = yawRad;
		RightArm.rotateAngleX = pitchRad+1.570796F-animationAngle+wakeupAngle;
	}
}

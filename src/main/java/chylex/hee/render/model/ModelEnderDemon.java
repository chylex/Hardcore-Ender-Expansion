package chylex.hee.render.model;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.entity.boss.EntityBossEnderDemon;
import chylex.hee.system.util.MathUtil;

@SideOnly(Side.CLIENT)
public class ModelEnderDemon extends ModelBase{
	private final ModelRenderer head,body;
	private final ModelRenderer rightArm1,rightArm2,rightArm3,rightArm4;
	private final ModelRenderer leftArm1,leftArm2,leftArm3,leftArm4;
	
	private float limbSwingMp = 1F;

	public ModelEnderDemon(){
		textureWidth = 64;
		textureHeight = 64;
		
		head = new ModelRenderer(this,0,0);
		head.addBox(-4F,-8F,-8F,8,8,8);
		head.setRotationPoint(0F,-14F,0F);
		head.setTextureSize(64,64);
		head.mirror = true;
		setRotation(head,0F,0F,0F);
		
		body = new ModelRenderer(this,0,17);
		body.addBox(-3F,0F,-5F,6,20,7);
		body.setRotationPoint(0F,-14F,0F);
		body.setTextureSize(64,64);
		body.mirror = true;
		setRotation(body,0F,0F,0F);
		
		rightArm1 = new ModelRenderer(this,56,0);
		rightArm1.addBox(-1F,-2F,-1F,2,22,2);
		rightArm1.setRotationPoint(-2F,-12F,3F);
		rightArm1.setTextureSize(64,64);
		rightArm1.mirror = true;
		setRotation(rightArm1,0F,0F,2.044824F);
		
		leftArm1 = new ModelRenderer(this,56,0);
		leftArm1.addBox(-1F,-2F,-1F,2,22,2);
		leftArm1.setRotationPoint(2F,-12F,3F);
		leftArm1.setTextureSize(64,64);
		leftArm1.mirror = true;
		setRotation(leftArm1,0F,0F,-1.933288F);
		leftArm1.mirror = false;
		
		rightArm2 = new ModelRenderer(this,47,0);
		rightArm2.addBox(-1F,-1F,-1F,2,20,2);
		rightArm2.setRotationPoint(-2F,-7.6F,3F);
		rightArm2.setTextureSize(64,64);
		rightArm2.mirror = true;
		setRotation(rightArm2,0F,0F,1.784573F);
		
		leftArm2 = new ModelRenderer(this,47,0);
		leftArm2.addBox(-1F,-2F,-1F,2,20,2);
		leftArm2.setRotationPoint(2F,-7.6F,3F);
		leftArm2.setTextureSize(64,64);
		leftArm2.mirror = true;
		setRotation(leftArm2,0F,0F,-1.747395F);
		
		leftArm3 = new ModelRenderer(this,38,0);
		leftArm3.addBox(-1F,-2F,-1F,2,23,2);
		leftArm3.setRotationPoint(2F,-3F,3F);
		leftArm3.setTextureSize(64,64);
		leftArm3.mirror = true;
		setRotation(leftArm3,0F,0F,-1.561502F);
		
		rightArm3 = new ModelRenderer(this,38,0);
		rightArm3.addBox(-1F,-2F,-1F,2,23,2);
		rightArm3.setRotationPoint(-2F,-3F,3F);
		rightArm3.setTextureSize(64,64);
		rightArm3.mirror = true;
		setRotation(rightArm3,0F,0F,1.449966F);
		
		rightArm4 = new ModelRenderer(this,47,23);
		rightArm4.addBox(-1F,-2F,-1F,2,19,2);
		rightArm4.setRotationPoint(-2F,2F,3F);
		rightArm4.setTextureSize(64,64);
		rightArm4.mirror = true;
		setRotation(rightArm4,0F,0F,1.115358F);
		
		leftArm4 = new ModelRenderer(this,47,23);
		leftArm4.addBox(-1F,-2F,-1F,2,19,2);
		leftArm4.setRotationPoint(2F,2F,3F);
		leftArm4.setTextureSize(64,64);
		leftArm4.mirror = true;
		setRotation(leftArm4,0F,0F,-1.07818F);
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel){
		super.render(entity,limbSwing,limbSwingAngle,entityTickTime,rotationYaw,rotationPitch,unitPixel);
		setRotationAngles(limbSwing,limbSwingAngle,entityTickTime,rotationYaw,rotationPitch,unitPixel,entity);
		
		head.render(unitPixel);
		body.render(unitPixel);
		rightArm1.render(unitPixel);
		rightArm2.render(unitPixel);
		rightArm3.render(unitPixel);
		rightArm4.render(unitPixel);
		leftArm1.render(unitPixel);
		leftArm2.render(unitPixel);
		leftArm3.render(unitPixel);
		leftArm4.render(unitPixel);
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
			  pitchRad = MathUtil.toRad(-rotationPitch),
			  timer = entity.ticksExisted*0.1F;

		head.rotateAngleY = yawRad;
		head.rotateAngleX = pitchRad;
		
		body.rotateAngleY = yawRad;
		
		rightArm1.rotateAngleY = rightArm2.rotateAngleY = rightArm3.rotateAngleY = rightArm4.rotateAngleY = yawRad;
		leftArm1.rotateAngleY = leftArm2.rotateAngleY = leftArm3.rotateAngleY = leftArm4.rotateAngleY = yawRad;
		
		rightArm1.rotateAngleZ = 2F+MathHelper.cos(timer*0.6F+0.24F)*0.1F*limbSwingMp;
		rightArm2.rotateAngleZ = 1.8F+MathHelper.cos(timer*0.5F)*0.1F*limbSwingMp;
		rightArm3.rotateAngleZ = 1.5F+MathHelper.cos(timer*0.6F+0.09F)*0.1F*limbSwingMp;
		rightArm4.rotateAngleZ = 1.1F+MathHelper.cos(timer*0.65F+0.33F)*0.1F*limbSwingMp;
		
		leftArm1.rotateAngleZ = -2F+MathHelper.cos(timer*0.49F+0.24F)*0.1F*limbSwingMp;
		leftArm2.rotateAngleZ = -1.8F+MathHelper.cos(timer*0.52F)*0.1F*limbSwingMp;
		leftArm3.rotateAngleZ = -1.5F+MathHelper.cos(timer*0.6F+0.09F)*0.1F*limbSwingMp;
		leftArm4.rotateAngleZ = -1.1F+MathHelper.cos(timer*0.56F+0.33F)*0.1F*limbSwingMp;
		
		rightArm1.rotateAngleX = MathHelper.cos(timer*0.57F+0.09F)*0.05F*limbSwingMp;
		rightArm2.rotateAngleX = MathHelper.cos(timer*0.5F)*0.11F*limbSwingMp;
		rightArm3.rotateAngleX = MathHelper.cos(timer*0.48F+0.26F)*0.05F*limbSwingMp;
		rightArm4.rotateAngleX = MathHelper.cos(timer*0.62F)*0.05F*limbSwingMp;
		
		leftArm1.rotateAngleX = MathHelper.cos(timer*0.54F+0.27F)*0.05F*limbSwingMp;
		leftArm2.rotateAngleX = MathHelper.cos(timer*0.56F+0.1F)*0.05F*limbSwingMp;
		leftArm3.rotateAngleX = MathHelper.cos(timer*0.64F+0.06F)*0.05F*limbSwingMp;
		leftArm4.rotateAngleX = MathHelper.cos(timer*0.6F+0.3F)*0.05F*limbSwingMp;
		
		if (((EntityBossEnderDemon)entity).isDoingLightningAttack()){
			if (limbSwingMp > 0.05F)limbSwingMp *= 0.92F;
		}
		else if (limbSwingMp < 1F)limbSwingMp += 0.05F;
	}
}

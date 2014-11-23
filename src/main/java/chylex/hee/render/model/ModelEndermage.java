package chylex.hee.render.model;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelEndermage extends ModelBase{
	private final ModelRenderer crown, head, body;
	private final ModelRenderer rightArm, leftArm;
	private final ModelRenderer rightLeg, leftLeg;
	private final ModelRenderer staffRod, staffGem;
	private final ModelRenderer staffRightDeco, staffLeftDeco;

	public ModelEndermage(){
		textureWidth = 64;
		textureHeight = 64;

		crown = new ModelRenderer(this,28,32);
		crown.addBox(-4.5F,-9F,-4.5F,9,2,9);
		crown.setRotationPoint(0F,-10F,0F);
		crown.setTextureSize(64,64);
		crown.mirror = true;
		setRotation(crown,0F,0F,0F);
		
		head = new ModelRenderer(this,5,0);
		head.addBox(-4F,-8F,-4F,8,8,8);
		head.setRotationPoint(0F,-10F,0F);
		head.setTextureSize(64,64);
		head.mirror = true;
		setRotation(head,0F,0F,0F);
		
		body = new ModelRenderer(this,31,17);
		body.addBox(-4F,0F,-2F,8,10,4);
		body.setRotationPoint(0F,-10F,0F);
		body.setTextureSize(64,64);
		body.mirror = true;
		setRotation(body,0F,0F,0F);
		
		leftArm = new ModelRenderer(this,56,0);
		leftArm.addBox(-1F,-2F,-1F,2,27,2);
		leftArm.setRotationPoint(5F,-7F,0F);
		leftArm.setTextureSize(64,64);
		leftArm.mirror = false;
		setRotation(leftArm,0F,0F,0F);
		
		rightArm = new ModelRenderer(this,56,0);
		rightArm.addBox(-1F,-2F,-1F,2,25,2);
		rightArm.setRotationPoint(-5F,-7F,0F);
		rightArm.setTextureSize(64,64);
		rightArm.mirror = true;
		setRotation(rightArm,-0.0523599F,0F,0.122173F);
		
		leftLeg = new ModelRenderer(this,56,0);
		leftLeg.addBox(-1F,0F,-1F,2,29,2);
		leftLeg.setRotationPoint(2F,-1F,0F);
		leftLeg.setTextureSize(64,64);
		leftLeg.mirror = false;
		setRotation(leftLeg,0F,0F,0F);
		
		rightLeg = new ModelRenderer(this,56,0);
		rightLeg.addBox(-1F,0F,-1F,2,29,2);
		rightLeg.setRotationPoint(-2F,-1F,0F);
		rightLeg.setTextureSize(64,64);
		rightLeg.mirror = true;
		setRotation(rightLeg,0F,0F,0F);
		
		staffRod = new ModelRenderer(this,0,0);
		staffRod.addBox(-3F,-7F,-5.6F,1,33,1);
		staffRod.setTextureSize(64,64);
		staffRod.mirror = true;
		setRotation(staffRod,0.2337089F,0F,-0.122173F);
		
		staffGem = new ModelRenderer(this,38,0);
		staffGem.addBox(-4.5F,-11F,-7F,4,4,4);
		staffGem.setTextureSize(64,64);
		staffGem.mirror = true;
		setRotation(staffGem,0F,0F,0F);
		
		staffRightDeco = new ModelRenderer(this,38,9);
		staffRightDeco.addBox(-5.5F,-13F,-6F,1,5,2);
		staffRightDeco.setTextureSize(64,64);
		staffRightDeco.mirror = true;
		setRotation(staffRightDeco,0F,0F,0F);
		
		staffLeftDeco = new ModelRenderer(this,45,9);
		staffLeftDeco.addBox(-0.5F,-13F,-6F,1,5,2);
		staffLeftDeco.setTextureSize(64,64);
		staffLeftDeco.mirror = true;
		setRotation(staffLeftDeco,0F,0F,0F);
		
		rightArm.addChild(staffRod);
		staffRod.addChild(staffGem);
		staffGem.addChild(staffLeftDeco);
		staffGem.addChild(staffRightDeco);
	}
	
	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel){
		super.render(entity,limbSwing,limbSwingAngle,entityTickTime,rotationYaw,rotationPitch,unitPixel);
		setRotationAngles(limbSwing,limbSwingAngle,entityTickTime,rotationYaw,rotationPitch,unitPixel,entity);
		
	    crown.render(unitPixel);
		head.render(unitPixel);
	    body.render(unitPixel);
	    rightArm.render(unitPixel);
	    leftArm.render(unitPixel);
	    rightLeg.render(unitPixel);
	    leftLeg.render(unitPixel);
	}
	
	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity){
		super.setRotationAngles(limbSwing,limbSwingAngle,entityTickTime,rotationYaw,rotationPitch,unitPixel,entity);

		crown.rotateAngleY = head.rotateAngleY = MathUtil.toRad(rotationYaw);
		crown.rotateAngleX = head.rotateAngleX = MathUtil.toRad(rotationPitch);
		
		body.rotateAngleX = 0F;

		leftLeg.rotateAngleX = MathHelper.cos(limbSwing*0.6662F+(float)Math.PI)*1.4F*limbSwingAngle*0.5F;
		rightLeg.rotateAngleX = MathHelper.cos(limbSwing*0.6662F)*1.4F*limbSwingAngle*0.5F;
		
		leftLeg.rotateAngleY = rightLeg.rotateAngleY = 0F;
		leftArm.rotateAngleY = rightArm.rotateAngleY = 0F;
		
		leftArm.rotateAngleZ = -MathHelper.cos(entityTickTime*0.09F)*0.05F;
		rightArm.rotateAngleZ = 0.122173F+MathHelper.cos(entityTickTime*0.09F)*0.05F;

		leftArm.rotateAngleX = (MathHelper.cos(limbSwing*0.6662F)*2F*limbSwingAngle*0.5F-MathHelper.sin(entityTickTime*0.067F)*0.05F)*0.5F;
		rightArm.rotateAngleX = (MathHelper.cos(limbSwing*0.6662F+(float)Math.PI)*limbSwingAngle+MathHelper.sin(entityTickTime*0.067F)*0.05F)*0.5F;
		
		if (leftArm.rotateAngleX > 0.4F)leftArm.rotateAngleX = 0.4F;
		else if (leftArm.rotateAngleX < -0.4F)leftArm.rotateAngleX = -0.4F;
		
		if (rightArm.rotateAngleX > 0.4F)rightArm.rotateAngleX = 0.4F;
		else if (rightArm.rotateAngleX < -0.4F)rightArm.rotateAngleX = -0.4F;
		
		if (leftLeg.rotateAngleX > 0.4F)leftLeg.rotateAngleX = 0.4F;
		else if (leftLeg.rotateAngleX < -0.4F)leftLeg.rotateAngleX = -0.4F;
		
		if (rightLeg.rotateAngleX > 0.4F)rightLeg.rotateAngleX = 0.4F;
		else if (rightLeg.rotateAngleX < -0.4F)rightLeg.rotateAngleX = -0.4F;
	}

	private void setRotation(ModelRenderer model, float x, float y, float z){
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}

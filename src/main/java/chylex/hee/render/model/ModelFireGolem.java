package chylex.hee.render.model;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelFireGolem extends ModelBase{
	private final ModelRenderer headMain,headBrow,headHorn1,headHorn2,headLip,headJaw,headRTooth,headLTooth;
	private final ModelRenderer trunk,torso;
	private final ModelRenderer rightArm1,rightArm2,leftArm1,leftArm2;
	private final ModelRenderer rightLeg1,rightLeg2,rightLeg3,rightHoof,rightHoofIn,rightHoofOut;
	private final ModelRenderer leftLeg1,leftLeg2,leftLeg3,leftHoof,leftHoofIn,leftHoofOut;

	public ModelFireGolem(){
		textureWidth = 64;
		textureHeight = 64;
		
		headMain = new ModelRenderer(this,22,16);
		headMain.addBox(-2.5F,-2F,-6F,5,6,4);
		headMain.setRotationPoint(0F,3F,-4F);
		headMain.setTextureSize(64,64);
		headMain.mirror = true;
		setRotation(headMain,-0.5235988F,0F,0F);
		
		headBrow = new ModelRenderer(this,26,12);
		headBrow.addBox(-2F,-1F,-7F,4,2,1);
		headBrow.setRotationPoint(0F,3F,-4F);
		headBrow.setTextureSize(64,64);
		headBrow.mirror = true;
		setRotation(headBrow,-0.5235988F,0F,0F);
		
		headHorn1 = new ModelRenderer(this,28,8);
		headHorn1.addBox(-1F,2F,-7F,2,2,1);
		headHorn1.setRotationPoint(0F,3F,-4F);
		headHorn1.setTextureSize(64,64);
		headHorn1.mirror = true;
		setRotation(headHorn1,-0.5235988F,0F,0F);
		
		headHorn2 = new ModelRenderer(this,28,4);
		headHorn2.addBox(-0.5F,2.5F,-9F,1,1,2);
		headHorn2.setRotationPoint(0F,3F,-4F);
		headHorn2.setTextureSize(64,64);
		headHorn2.mirror = true;
		setRotation(headHorn2,-0.5235988F,0F,0F);
		
		headLip = new ModelRenderer(this,26,1);
		headLip.addBox(-2F,4F,-6F,4,1,1);
		headLip.setRotationPoint(0F,3F,-4F);
		headLip.setTextureSize(64,64);
		headLip.mirror = true;
		setRotation(headLip,-0.5235988F,0F,0F);
		
		headJaw = new ModelRenderer(this,0,4);
		headJaw.addBox(-2.5F,4F,-3F,5,2,1);
		headJaw.setRotationPoint(0F,3F,-4F);
		headJaw.setTextureSize(64,64);
		headJaw.mirror = true;
		setRotation(headJaw,-0.5235988F,0F,0F);
		
		headRTooth = new ModelRenderer(this,0,0);
		headRTooth.addBox(-2F,4F,-6F,1,1,2);
		headRTooth.setRotationPoint(0F,3F,-4F);
		headRTooth.setTextureSize(64,64);
		headRTooth.mirror = true;
		setRotation(headRTooth,-0.2617994F,0F,0F);
		
		headLTooth = new ModelRenderer(this,0,0);
		headLTooth.addBox(1F,4F,-6F,1,1,2);
		headLTooth.setRotationPoint(0F,3F,-4F);
		headLTooth.setTextureSize(64,64);
		headLTooth.mirror = true;
		setRotation(headLTooth,-0.2617994F,0F,0F);
		
		trunk = new ModelRenderer(this,16,27);
		trunk.addBox(-5F,0F,-2F,10,7,5);
		trunk.setRotationPoint(0F,1F,-6F);
		trunk.setTextureSize(64,64);
		trunk.mirror = true;
		setRotation(trunk,0.5235988F,0F,0F);
		
		torso = new ModelRenderer(this,19,40);
		torso.addBox(-4F,0F,-1F,8,9,4);
		torso.setRotationPoint(0F,6F,-3F);
		torso.setTextureSize(64,64);
		torso.mirror = true;
		setRotation(torso,0.2617994F,0F,0F);
		
		rightArm1 = new ModelRenderer(this,3,12);
		rightArm1.addBox(-3F,-1F,-1F,3,6,3);
		rightArm1.setRotationPoint(-5F,3F,-5F);
		rightArm1.setTextureSize(64,64);
		rightArm1.mirror = true;
		setRotation(rightArm1,1.047198F,0F,0F);
		
		rightArm2 = new ModelRenderer(this,3,22);
		rightArm2.addBox(-3F,1F,2F,3,9,3);
		rightArm2.setTextureSize(64,64);
		rightArm2.mirror = true;
		setRotation(rightArm2,-0.5235988F,0F,0F);
		
		leftArm1 = new ModelRenderer(this,47,12);
		leftArm1.addBox(0F,-1F,-1F,3,6,3);
		leftArm1.setRotationPoint(5F,3F,-5F);
		leftArm1.setTextureSize(64,64);
		leftArm1.mirror = true;
		setRotation(leftArm1,1.047198F,0F,0F);
		
		leftArm2 = new ModelRenderer(this,47,22);
		leftArm2.addBox(0F,1F,2F,3,9,3);
		leftArm2.setTextureSize(64,64);
		leftArm2.mirror = true;
		setRotation(leftArm2,-0.5235988F,0F,0F);
		
		rightLeg1 = new ModelRenderer(this,6,37);
		rightLeg1.addBox(-1.5F,-1F,-2F,3,6,3);
		rightLeg1.setRotationPoint(-4F,12F,0F);
		rightLeg1.setTextureSize(64,64);
		rightLeg1.mirror = true;
		setRotation(rightLeg1,-0.5235988F,0F,0F);
		
		rightLeg2 = new ModelRenderer(this,6,47);
		rightLeg2.addBox(-1.5F,1F,-5F,3,5,3);
		rightLeg2.setTextureSize(64,64);
		rightLeg2.mirror = true;
		setRotation(rightLeg2,1.047198F,0F,0F);
		
		rightLeg3 = new ModelRenderer(this,10,56);
		rightLeg3.addBox(-1F,5F,4F,2,4,2);
		rightLeg3.setTextureSize(64,64);
		rightLeg3.mirror = true;
		setRotation(rightLeg3,-0.5235988F,0F,0F);
		
		rightHoof = new ModelRenderer(this,24,54);
		rightHoof.addBox(-1.5F,9F,-2F,3,3,4);
		rightHoof.setRotationPoint(-4F,12F,0F);
		rightHoof.setTextureSize(64,64);
		rightHoof.mirror = true;
		setRotation(rightHoof,0F,0F,0F);
		rightHoof.mirror = false;
		
		rightHoofIn = new ModelRenderer(this,39,57);
		rightHoofIn.addBox(0.4F,9.2F,0.5F,1,3,1);
		rightHoofIn.setTextureSize(64,64);
		rightHoofIn.mirror = true;
		setRotation(rightHoofIn,-0.2617994F,0F,0F);
		rightHoofOut = new ModelRenderer(this,39,57);
		rightHoofOut.addBox(-1.4F,9.2F,0.5F,1,3,1);
		rightHoofOut.setTextureSize(64,64);
		rightHoofOut.mirror = true;
		setRotation(rightHoofOut,-0.2617994F,0F,0F);
		
		leftLeg1 = new ModelRenderer(this,44,37);
		leftLeg1.addBox(-1.5F,-1F,-2F,3,6,3);
		leftLeg1.setRotationPoint(4F,12F,0F);
		leftLeg1.setTextureSize(64,64);
		leftLeg1.mirror = true;
		setRotation(leftLeg1,-0.5235988F,0F,0F);
		
		leftLeg2 = new ModelRenderer(this,44,47);
		leftLeg2.addBox(-1.5F,1F,-5F,3,5,3);
		leftLeg2.setTextureSize(64,64);
		leftLeg2.mirror = true;
		setRotation(leftLeg2,1.047198F,0F,0F);
		
		leftLeg3 = new ModelRenderer(this,44,56);
		leftLeg3.addBox(-1F,5F,4F,2,4,2);
		leftLeg3.setTextureSize(64,64);
		leftLeg3.mirror = true;
		setRotation(leftLeg3,-0.5235988F,0F,0F);
		
		leftHoof = new ModelRenderer(this,24,54);
		leftHoof.addBox(-1.5F,9F,-2F,3,3,4);
		leftHoof.setRotationPoint(4F,12F,0F);
		leftHoof.setTextureSize(64,64);
		leftHoof.mirror = true;
		setRotation(leftHoof,0F,0F,0F);
		
		leftHoofIn = new ModelRenderer(this,39,57);
		leftHoofIn.addBox(-1.4F,9.2F,0.5F,1,3,1);
		leftHoofIn.setTextureSize(64,64);
		leftHoofIn.mirror = true;
		setRotation(leftHoofIn,-0.2617994F,0F,0F);
		
		leftHoofOut = new ModelRenderer(this,39,57);
		leftHoofOut.addBox(0.4F,9.2F,0.5F,1,3,1);
		leftHoofOut.setTextureSize(64,64);
		leftHoofOut.mirror = true;
		setRotation(leftHoofOut,-0.2617994F,0F,0F);
		
		rightArm1.addChild(rightArm2);
		leftArm1.addChild(leftArm2);
		rightLeg1.addChild(rightLeg2);
		rightLeg1.addChild(rightLeg3);
		rightHoof.addChild(rightHoofOut);
		rightHoof.addChild(rightHoofIn);
		leftLeg1.addChild(leftLeg2);
		leftLeg1.addChild(leftLeg3);
		leftHoof.addChild(leftHoofOut);
		leftHoof.addChild(leftHoofIn);
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel){
		super.render(entity,limbSwing,limbSwingAngle,entityTickTime,rotationYaw,rotationPitch,unitPixel);
		setRotationAngles(limbSwing,limbSwingAngle,entityTickTime,rotationYaw,rotationPitch,unitPixel,entity);
		
		headMain.render(unitPixel);
		headBrow.render(unitPixel);
		headHorn1.render(unitPixel);
		headHorn2.render(unitPixel);
		headLip.render(unitPixel);
		headJaw.render(unitPixel);
		headRTooth.render(unitPixel);
		headLTooth.render(unitPixel);
		trunk.render(unitPixel);
		torso.render(unitPixel);
		rightArm1.render(unitPixel);
		leftArm1.render(unitPixel);
		rightLeg1.render(unitPixel);
		rightHoof.render(unitPixel);
		leftLeg1.render(unitPixel);
		leftHoof.render(unitPixel);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z){
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
	
	private static final float x = 0.2617994F;
	private static final float lx = 0.5235988F;
	private static final float ex = 1.047198F;

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity){
		super.setRotationAngles(limbSwing,limbSwingAngle,entityTickTime,rotationYaw,rotationPitch,unitPixel,entity);
		
		float movcos1 = MathHelper.cos(limbSwing*0.7F)*0.3F*limbSwingAngle;
		float movsin1 = MathHelper.sin(limbSwing*0.7F)*1.2F*limbSwingAngle;
		float yawRad = MathUtil.toRad(rotationYaw);
		float pitchRad = MathUtil.toRad(rotationPitch)-lx;
		
		headMain.rotateAngleY = yawRad;
		headBrow.rotateAngleY = yawRad;
		headHorn1.rotateAngleY = yawRad;
		headHorn2.rotateAngleY = yawRad;
		headJaw.rotateAngleY = yawRad;
		headLip.rotateAngleY = yawRad;
		headLTooth.rotateAngleY = yawRad;
		headRTooth.rotateAngleY = yawRad;
		headMain.rotateAngleX = pitchRad;
		headBrow.rotateAngleX = pitchRad;
		headHorn1.rotateAngleX = pitchRad;
		headHorn2.rotateAngleX = pitchRad;
		headJaw.rotateAngleX = pitchRad;
		headLip.rotateAngleX = pitchRad;
		headLTooth.rotateAngleX = pitchRad+x;
		headRTooth.rotateAngleX = pitchRad+x;
		rightArm1.rotateAngleZ = -movcos1;
		leftArm1.rotateAngleZ = -movcos1;
		rightArm1.rotateAngleX = ex+movsin1;
		leftArm1.rotateAngleX = ex-movsin1;
		rightArm2.rotateAngleX = -lx-ex;
		leftArm2.rotateAngleX = -lx-ex;
		rightLeg1.rotateAngleX = -lx-movsin1;
		leftLeg1.rotateAngleX = -lx+movsin1;
		rightLeg2.rotateAngleX = ex+lx;
		leftLeg2.rotateAngleX = ex+lx;
		rightLeg3.rotateAngleX = 0F;
		leftLeg3.rotateAngleX = 0F;
		rightHoof.rotateAngleX = -movsin1;
		leftHoof.rotateAngleX = +movsin1;
	}
}

package chylex.hee.render.model;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelFireFiend extends ModelBase{
	ModelRenderer headMain,headBrow,headHorn1,headHorn2,headLip,headJaw,headRightTooth,headLeftTooth;
	ModelRenderer bodyTrunk,bodyTorso;
	ModelRenderer rightArm1,rightArm2,leftArm1,leftArm2;
	ModelRenderer rightLeg1,rightLeg2,rightLeg3,RHoof,RHoofIn,RHoofOut;
	ModelRenderer leftLeg1,leftLeg2,leftLeg3,LHoof,LHoofIn,LHoofOut;
	ModelRenderer leftWing1,leftWing2,rightWing1,rightWing2;

	public ModelFireFiend(){
		textureWidth = 64;
		textureHeight = 128;
		
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
		
		headRightTooth = new ModelRenderer(this,0,0);
		headRightTooth.addBox(-2F,4F,-6F,1,1,2);
		headRightTooth.setRotationPoint(0F,3F,-4F);
		headRightTooth.setTextureSize(64,64);
		headRightTooth.mirror = true;
		setRotation(headRightTooth,-0.2617994F,0F,0F);
		
		headLeftTooth = new ModelRenderer(this,0,0);
		headLeftTooth.addBox(1F,4F,-6F,1,1,2);
		headLeftTooth.setRotationPoint(0F,3F,-4F);
		headLeftTooth.setTextureSize(64,64);
		headLeftTooth.mirror = true;
		setRotation(headLeftTooth,-0.2617994F,0F,0F);
		
		bodyTrunk = new ModelRenderer(this,16,27);
		bodyTrunk.addBox(-5F,0F,-2F,10,7,5);
		bodyTrunk.setRotationPoint(0F,1F,-6F);
		bodyTrunk.setTextureSize(64,64);
		bodyTrunk.mirror = true;
		setRotation(bodyTrunk,0.5235988F,0F,0F);
		
		bodyTorso = new ModelRenderer(this,19,40);
		bodyTorso.addBox(-4F,0F,-1F,8,9,4);
		bodyTorso.setRotationPoint(0F,6F,-3F);
		bodyTorso.setTextureSize(64,64);
		bodyTorso.mirror = true;
		setRotation(bodyTorso,0.2617994F,0F,0F);
		
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
		
		RHoof = new ModelRenderer(this,24,54);
		RHoof.addBox(-1.5F,9F,-2F,3,3,4);
		RHoof.setRotationPoint(-4F,12F,0F);
		RHoof.setTextureSize(64,64);
		RHoof.mirror = true;
		setRotation(RHoof,0F,0F,0F);
		RHoof.mirror = false;
		
		RHoofIn = new ModelRenderer(this,39,57);
		RHoofIn.addBox(0.4F,9.2F,0.5F,1,3,1);
		RHoofIn.setTextureSize(64,64);
		RHoofIn.mirror = true;
		setRotation(RHoofIn,-0.2617994F,0F,0F);
		
		RHoofOut = new ModelRenderer(this,39,57);
		RHoofOut.addBox(-1.4F,9.2F,0.5F,1,3,1);
		RHoofOut.setTextureSize(64,64);
		RHoofOut.mirror = true;
		setRotation(RHoofOut,-0.2617994F,0F,0F);
		
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
		
		LHoof = new ModelRenderer(this,24,54);
		LHoof.addBox(-1.5F,9F,-2F,3,3,4);
		LHoof.setRotationPoint(4F,12F,0F);
		LHoof.setTextureSize(64,64);
		LHoof.mirror = true;
		setRotation(LHoof,0F,0F,0F);
		
		LHoofIn = new ModelRenderer(this,39,57);
		LHoofIn.addBox(-1.4F,9.2F,0.5F,1,3,1);
		LHoofIn.setTextureSize(64,64);
		LHoofIn.mirror = true;
		setRotation(LHoofIn,-0.2617994F,0F,0F);
		
		LHoofOut = new ModelRenderer(this,39,57);
		LHoofOut.addBox(0.4F,9.2F,0.5F,1,3,1);
		LHoofOut.setTextureSize(64,64);
		LHoofOut.mirror = true;
		setRotation(LHoofOut,-0.2617994F,0F,0F);
		
		leftWing1 = new ModelRenderer(this,28,71);
		leftWing1.addBox(-17F,-3F,-1F,17,6,1);
		leftWing1.setRotationPoint(2F,2F,-2F);
		leftWing1.setTextureSize(64,128);
		leftWing1.mirror = true;
		setRotation(leftWing1,-0.5235988F,2.617994F,-0.5235988F);
		
		leftWing2 = new ModelRenderer(this,40,79);
		leftWing2.addBox(-17F,3F,-1F,11,3,1);
		leftWing2.setRotationPoint(2F,2F,-2F);
		leftWing2.setTextureSize(64,128);
		leftWing2.mirror = true;
		setRotation(leftWing2,-0.5235988F,2.617994F,-0.5235988F);
		
		rightWing1 = new ModelRenderer(this,0,63);
		rightWing1.addBox(-17F,-3F,0F,17,6,1);
		rightWing1.setRotationPoint(-2F,2F,-2F);
		rightWing1.setTextureSize(64,128);
		rightWing1.mirror = true;
		setRotation(rightWing1,0.5235988F,0.5235988F,0.5235988F);
		
		rightWing2 = new ModelRenderer(this,0,71);
		rightWing2.addBox(-17F,3F,0F,11,3,1);
		rightWing2.setRotationPoint(-2F,2F,-2F);
		rightWing2.setTextureSize(64,128);
		rightWing2.mirror = true;
		setRotation(rightWing2,0.5235988F,0.5235988F,0.5235988F);
		
		rightArm1.addChild(rightArm2);
		leftArm1.addChild(leftArm2);
		rightLeg1.addChild(rightLeg2);
		rightLeg1.addChild(rightLeg3);
		RHoof.addChild(RHoofOut);
		RHoof.addChild(RHoofIn);
		leftLeg1.addChild(leftLeg2);
		leftLeg1.addChild(leftLeg3);
		LHoof.addChild(LHoofOut);
		LHoof.addChild(LHoofIn);
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
		headRightTooth.render(unitPixel);
		headLeftTooth.render(unitPixel);
		bodyTrunk.render(unitPixel);
		bodyTorso.render(unitPixel);
		rightArm1.render(unitPixel);
		leftArm1.render(unitPixel);
		rightLeg1.render(unitPixel);
		RHoof.render(unitPixel);
		leftLeg1.render(unitPixel);
		LHoof.render(unitPixel);
		leftWing1.render(unitPixel);
		leftWing2.render(unitPixel);
		rightWing1.render(unitPixel);
		rightWing2.render(unitPixel);
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
		
		float swing = MathHelper.sin(limbSwing*0.7F)*1.2F*limbSwingAngle;
		float yawRad = MathUtil.toRad(rotationYaw);
		float pitchRad = MathUtil.toRad(rotationPitch)-lx;
		
		headMain.rotateAngleY = yawRad;
		headBrow.rotateAngleY = yawRad;
		headHorn1.rotateAngleY = yawRad;
		headHorn2.rotateAngleY = yawRad;
		headJaw.rotateAngleY = yawRad;
		headLip.rotateAngleY = yawRad;
		headLeftTooth.rotateAngleY = yawRad;
		headRightTooth.rotateAngleY = yawRad;
		headMain.rotateAngleX = pitchRad;
		headBrow.rotateAngleX = pitchRad;
		headHorn1.rotateAngleX = pitchRad;
		headHorn2.rotateAngleX = pitchRad;
		headJaw.rotateAngleX = pitchRad;
		headLip.rotateAngleX = pitchRad;
		headLeftTooth.rotateAngleX = pitchRad+x;
		headRightTooth.rotateAngleX = pitchRad+x;
		rightArm1.rotateAngleX = ex;
		leftArm1.rotateAngleX = ex;
		rightArm2.rotateAngleX = -lx-ex;
		leftArm2.rotateAngleX = -lx-ex;
		rightLeg1.rotateAngleX = -lx;
		leftLeg1.rotateAngleX = -lx;
		rightLeg2.rotateAngleX = ex+lx;
		leftLeg2.rotateAngleX = ex+lx;
		rightLeg3.rotateAngleX = 0F;
		leftLeg3.rotateAngleX = 0F;
		leftWing1.rotateAngleY = swing+(float)Math.PI;
		leftWing2.rotateAngleY = swing+(float)Math.PI;
		rightWing1.rotateAngleY = -swing;
		rightWing2.rotateAngleY = -swing;
	}
}

package chylex.hee.render.model;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelLouse extends ModelBase{ // TODO review model
	private final ModelRenderer headMain,headFront,eyes;
	private final ModelRenderer antR,antL,mandible;
	private final ModelRenderer thx;
	private final ModelRenderer legBase1,legBase2,legBase3;
	private final ModelRenderer abMain,abSide,abBack;
	private final ModelRenderer rFLeg1,rFLeg2,rFLeg3;
	private final ModelRenderer rFLegClawOuter,rFLegClawInner;
	private final ModelRenderer rBLeg1,rBLeg2,rBLeg3;
	private final ModelRenderer rBLegClawOuter,rBLegClawInner;
	private final ModelRenderer lBLeg1,lBLeg2,lBLeg3;
	private final ModelRenderer lBLegClawOuter,lBLegClawInner;
	private final ModelRenderer lFLeg1,lFLeg2,lFLeg3;
	private final ModelRenderer lFLegClawOuter,lFLegClawInner;
	private final ModelRenderer rMLeg2,rMLeg1,rMLeg3;
	private final ModelRenderer rMLegClawOuter,rMLegClawInner;
	private final ModelRenderer lMLeg1,lMLeg2,lMLeg3;
	private final ModelRenderer lMLegClawOuter,lMLegClawInner;

	public ModelLouse(){
		textureWidth = 64;
		textureHeight = 128;

		headMain = new ModelRenderer(this,15,0);
		headMain.addBox(-2F,-1.5F,-4F,4,4,4);
		headMain.setRotationPoint(0F,19F,-10F);
		setRotation(headMain,0.0872665F,0F,0F);
		
		headFront = new ModelRenderer(this,39,0);
		headFront.addBox(-1.5F,-1F,-6F,3,3,2);
		setRotation(headFront,0F,0F,0F);
		
		eyes = new ModelRenderer(this,0,0);
		eyes.addBox(-2.5F,-1F,-3F,5,2,2);
		setRotation(eyes,0F,0F,0F);
		
		antR = new ModelRenderer(this,46,28);
		antR.addBox(-10F,0.5F,-2.5F,7,1,1);
		setRotation(antR,-0.0872665F,-0.8235988F,0F);
		
		antL = new ModelRenderer(this,0,28);
		antL.addBox(3F,0.5F,-2.5F,7,1,1);
		setRotation(antL,-0.0872665F,0.8235988F,0F);
		
		mandible = new ModelRenderer(this,52,0);
		mandible.addBox(-1F,0F,-7F,2,2,1);
		setRotation(mandible,0F,0F,0F);
		
		thx = new ModelRenderer(this,11,9);
		thx.addBox(-4F,-3F,-1F,8,6,12);
		thx.setRotationPoint(0F,19.5F,-9F);
		setRotation(thx,0F,0F,0F);
		
		legBase1 = new ModelRenderer(this,17,28);
		legBase1.addBox(-5F,-2F,0F,10,4,4);
		legBase1.setRotationPoint(0F,19.5F,-9F);
		setRotation(legBase1,0F,0F,0F);
		
		legBase2 = new ModelRenderer(this,16,37);
		legBase2.addBox(-5.5F,-2F,5F,11,4,4);
		legBase2.setRotationPoint(0F,19.5F,-9F);
		setRotation(legBase2,0F,0F,0F);
		
		legBase3 = new ModelRenderer(this,15,46);
		legBase3.addBox(-6F,-2F,10F,12,4,4);
		legBase3.setRotationPoint(0F,19.5F,-9F);
		setRotation(legBase3,0F,0F,0F);
		
		abMain = new ModelRenderer(this,6,55);
		abMain.addBox(-4.5F,-4.5F,4F,9,7,16);
		abMain.setRotationPoint(0F,20F,-2F);
		setRotation(abMain,-0.0872665F,0F,0F);
		
		abSide = new ModelRenderer(this,5,79);
		abSide.addBox(-7F,-2F,9F,14,4,12);
		abSide.setRotationPoint(0F,19F,-4F);
		setRotation(abSide,-0.0872665F,0F,0F);
		
		abBack = new ModelRenderer(this,23,96);
		abBack.addBox(-3F,-2F,13F,6,4,2);
		abBack.setRotationPoint(0F,20F,5F);
		setRotation(abBack,-0.0872665F,0F,0F);
		
		rFLeg1 = new ModelRenderer(this,25,103);
		rFLeg1.addBox(-1.033333F,-3F,-1F,2,4,2);
		rFLeg1.setRotationPoint(-5F,20F,-7F);
		setRotation(rFLeg1,0F,2.617994F,-0.8726646F);
		
		rFLeg2 = new ModelRenderer(this,25,110);
		rFLeg2.addBox(1F,-3F,-1.5F,6,2,3);
		setRotation(rFLeg2,0F,0F,-0.8726646F);
		
		rFLeg3 = new ModelRenderer(this,25,116);
		rFLeg3.addBox(2F,-7.5F,-1F,6,3,2);
		setRotation(rFLeg3,0F,0F,0F);
		
		rFLegClawOuter = new ModelRenderer(this,25,122);
		rFLegClawOuter.addBox(8F,-7.5F,-0.5F,3,1,1);
		setRotation(rFLegClawOuter,0F,0F,0F);
		
		rFLegClawInner = new ModelRenderer(this,35,122);
		rFLegClawInner.addBox(8F,-6F,-0.5F,2,1,1);
		setRotation(rFLegClawInner,0F,0F,0F);
		
		rBLeg1 = new ModelRenderer(this,25,103);
		rBLeg1.addBox(-1.033333F,-3F,-1F,2,4,2);
		rBLeg1.setRotationPoint(-6.5F,20F,3F);
		setRotation(rBLeg1,0F,-2.617994F,-0.8726646F);
		
		rBLeg2 = new ModelRenderer(this,25,110);
		rBLeg2.addBox(1F,-3F,-1.5F,6,2,3);
		setRotation(rBLeg2,0F,0F,-0.8726646F);
		
		rBLeg3 = new ModelRenderer(this,25,116);
		rBLeg3.addBox(2F,-7.5F,-1F,6,3,2);
		setRotation(rBLeg3,0F,0F,0F);
		
		rBLegClawOuter = new ModelRenderer(this,25,122);
		rBLegClawOuter.addBox(8F,-7.5F,-0.5F,3,1,1);
		setRotation(rBLegClawOuter,0F,0F,0F);
		
		rBLegClawInner = new ModelRenderer(this,35,122);
		rBLegClawInner.addBox(8F,-6F,-0.5F,2,1,1);
		setRotation(rBLegClawInner,0F,0F,0F);
		
		lBLeg1 = new ModelRenderer(this,25,103);
		lBLeg1.addBox(-1.033333F,-3F,-1F,2,4,2);
		lBLeg1.setRotationPoint(6.5F,20F,3F);
		setRotation(lBLeg1,0F,-0.5235988F,0.8726646F);
		
		lBLeg2 = new ModelRenderer(this,25,110);
		lBLeg2.addBox(1F,-3F,-1.5F,6,2,3);
		setRotation(lBLeg2,0F,0F,-0.8726646F);
		
		lBLeg3 = new ModelRenderer(this,25,116);
		lBLeg3.addBox(2F,-7.5F,-1F,6,3,2);
		setRotation(lBLeg3,0F,0F,0F);
		
		lBLegClawOuter = new ModelRenderer(this,25,122);
		lBLegClawOuter.addBox(8F,-7.5F,-0.5F,3,1,1);
		setRotation(lBLegClawOuter,0F,0F,0F);
		
		lBLegClawInner = new ModelRenderer(this,35,122);
		lBLegClawInner.addBox(8F,-6F,-0.5F,2,1,1);
		setRotation(lBLegClawInner,0F,0F,0F);
		
		lFLeg1 = new ModelRenderer(this,25,103);
		lFLeg1.addBox(-1F,-3F,-1F,2,4,2);
		lFLeg1.setRotationPoint(5F,20F,-7F);
		setRotation(lFLeg1,0F,0.5235988F,0.8726646F);
		
		lFLeg2 = new ModelRenderer(this,25,110);
		lFLeg2.addBox(1F,-3F,-1.5F,6,2,3);
		setRotation(lFLeg2,0F,0F,-0.8726646F);
		
		lFLeg3 = new ModelRenderer(this,25,116);
		lFLeg3.addBox(2F,-7.5F,-1F,6,3,2);
		setRotation(lFLeg3,0F,0F,0F);
		
		lFLegClawOuter = new ModelRenderer(this,25,122);
		lFLegClawOuter.addBox(8F,-7.5F,-0.5F,3,1,1);
		setRotation(lFLegClawOuter,0F,0F,0F);
		
		lFLegClawInner = new ModelRenderer(this,35,122);
		lFLegClawInner.addBox(8F,-6F,-0.5F,2,1,1);
		setRotation(lFLegClawInner,0F,0F,0F);
		
		rMLeg1 = new ModelRenderer(this,25,103);
		rMLeg1.addBox(-1.033333F,-3F,-1F,2,4,2);
		rMLeg1.setRotationPoint(-6F,20F,-2F);
		setRotation(rMLeg1,0F,3.141593F,-0.8726646F);
		
		rMLeg2 = new ModelRenderer(this,25,110);
		rMLeg2.addBox(1F,-3F,-1.5F,6,2,3);
		setRotation(rMLeg2,0F,0F,-0.8726646F);
		
		rMLeg3 = new ModelRenderer(this,25,116);
		rMLeg3.addBox(2F,-7.5F,-1F,6,3,2);
		setRotation(rMLeg3,0F,0F,0F);
		
		rMLegClawOuter = new ModelRenderer(this,25,122);
		rMLegClawOuter.addBox(8F,-7.5F,-0.5F,3,1,1);
		setRotation(rMLegClawOuter,0F,0F,0F);
		
		rMLegClawInner = new ModelRenderer(this,35,122);
		rMLegClawInner.addBox(8F,-6F,-0.5F,2,1,1);
		setRotation(rMLegClawInner,0F,0F,0F);
		
		lMLeg1 = new ModelRenderer(this,25,103);
		lMLeg1.addBox(-1F,-3F,-1F,2,4,2);
		lMLeg1.setRotationPoint(6F,20F,-2F);
		setRotation(lMLeg1,0F,0F,0.8726646F);
		
		lMLeg2 = new ModelRenderer(this,25,110);
		lMLeg2.addBox(1F,-3F,-1.5F,6,2,3);
		setRotation(lMLeg2,0F,0F,-0.8726646F);
		
		lMLeg3 = new ModelRenderer(this,25,116);
		lMLeg3.addBox(2F,-7.5F,-1F,6,3,2);
		setRotation(lMLeg3,0F,0F,0F);
		
		lMLegClawOuter = new ModelRenderer(this,25,122);
		lMLegClawOuter.addBox(8F,-7.5F,-0.5F,3,1,1);
		setRotation(lMLegClawOuter,0F,0F,0F);
		
		lMLegClawInner = new ModelRenderer(this,35,122);
		lMLegClawInner.addBox(8F,-6F,-0.5F,2,1,1);
		setRotation(lMLegClawInner,0F,0F,0F);

		rFLeg1.addChild(rFLeg2);
		rFLeg1.addChild(rFLeg3);
		rFLeg1.addChild(rFLegClawOuter);
		rFLeg1.addChild(rFLegClawInner);

		rMLeg1.addChild(rMLeg2);
		rMLeg1.addChild(rMLeg3);
		rMLeg1.addChild(rMLegClawOuter);
		rMLeg1.addChild(rMLegClawInner);

		rBLeg1.addChild(rBLeg2);
		rBLeg1.addChild(rBLeg3);
		rBLeg1.addChild(rBLegClawOuter);
		rBLeg1.addChild(rBLegClawInner);

		lFLeg1.addChild(lFLeg2);
		lFLeg1.addChild(lFLeg3);
		lFLeg1.addChild(lFLegClawOuter);
		lFLeg1.addChild(lFLegClawInner);

		lMLeg1.addChild(lMLeg2);
		lMLeg1.addChild(lMLeg3);
		lMLeg1.addChild(lMLegClawOuter);
		lMLeg1.addChild(lMLegClawInner);

		lBLeg1.addChild(lBLeg2);
		lBLeg1.addChild(lBLeg3);
		lBLeg1.addChild(lBLegClawOuter);
		lBLeg1.addChild(lBLegClawInner);

		headMain.addChild(headFront);
		headMain.addChild(eyes);
		headMain.addChild(antR);
		headMain.addChild(antL);

	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel){
		super.render(entity,limbSwing,limbSwingAngle,entityTickTime,rotationYaw,rotationPitch,unitPixel);
		setRotationAngles(limbSwing,limbSwingAngle,entityTickTime,rotationYaw,rotationPitch,unitPixel,entity);
		
		headMain.render(unitPixel);

		thx.render(unitPixel);
		legBase1.render(unitPixel);
		legBase2.render(unitPixel);
		legBase3.render(unitPixel);
		abMain.render(unitPixel);
		abSide.render(unitPixel);
		abBack.render(unitPixel);

		rFLeg1.render(unitPixel);
		rMLeg1.render(unitPixel);
		rBLeg1.render(unitPixel);

		lFLeg1.render(unitPixel);
		lMLeg1.render(unitPixel);
		lBLeg1.render(unitPixel);

	}

	private void setRotation(ModelRenderer model, float x, float y, float z){
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity){
		super.setRotationAngles(limbSwing,limbSwingAngle,entityTickTime,rotationYaw,rotationPitch,unitPixel,entity);
		float correction = 0.8726646F;
		float legMovement = MathHelper.cos(limbSwing*2.5F)*0.9F*limbSwingAngle;
		headMain.rotateAngleY = rotationYaw*0.0174533F;
		rFLeg1.rotateAngleX = legMovement-correction;
		rMLeg1.rotateAngleX = -legMovement;
		rBLeg1.rotateAngleX = legMovement+correction;
		lFLeg1.rotateAngleX = legMovement+correction;
		lMLeg1.rotateAngleX = -legMovement;
		lBLeg1.rotateAngleX = legMovement-correction;
	}
}

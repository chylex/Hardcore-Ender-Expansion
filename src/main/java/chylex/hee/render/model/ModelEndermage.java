package chylex.hee.render.model;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
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
		crown.addBox(-4.466667F,-9F,-4.5F,9,2,9);
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
		
		rightArm = new ModelRenderer(this,56,0);
		rightArm.addBox(-1F,-2F,-1F,2,25,2);
		rightArm.setRotationPoint(-5F,-7F,0F);
		rightArm.setTextureSize(64,64);
		rightArm.mirror = true;
		setRotation(rightArm,-0.0523599F,0F,0.122173F);
		
		leftArm = new ModelRenderer(this,56,0);
		leftArm.addBox(-1F,-2F,-1F,2,27,2);
		leftArm.setRotationPoint(5F,-7F,0F);
		leftArm.setTextureSize(64,64);
		leftArm.mirror = true;
		setRotation(leftArm,0F,0F,0F);
		
		leftArm.mirror = false;
		rightLeg = new ModelRenderer(this,56,0);
		rightLeg.addBox(-1F,0F,-1F,2,29,2);
		rightLeg.setRotationPoint(-2F,-1F,0F);
		rightLeg.setTextureSize(64,64);
		rightLeg.mirror = true;
		setRotation(rightLeg,0F,0F,0F);
		
		leftLeg = new ModelRenderer(this,56,0);
		leftLeg.addBox(-1F,0F,-1F,2,29,2);
		leftLeg.setRotationPoint(2F,-1F,0F);
		leftLeg.setTextureSize(64,64);
		leftLeg.mirror = true;
		setRotation(leftLeg,0F,0F,0F);
		
		leftLeg.mirror = false;
		staffRod = new ModelRenderer(this,0,0);
		staffRod.addBox(-3F,-7F,-2.6F,1,33,1);
		staffRod.setRotationPoint(-5F,-4F,-3F);
		staffRod.setTextureSize(64,64);
		staffRod.mirror = true;
		setRotation(staffRod,0.181349F,0F,0F);
		
		staffGem = new ModelRenderer(this,38,0);
		staffGem.addBox(-7.5F,-12F,-7F,4,4,4);
		staffGem.setRotationPoint(-2F,-3F,0F);
		staffGem.setTextureSize(64,64);
		staffGem.mirror = true;
		setRotation(staffGem,0.181349F,0F,0F);
		
		staffRightDeco = new ModelRenderer(this,38,9);
		staffRightDeco.addBox(-8.5F,-14F,-6F,1,5,2);
		staffRightDeco.setRotationPoint(-2F,-4F,0F);
		staffRightDeco.setTextureSize(64,64);
		staffRightDeco.mirror = true;
		setRotation(staffRightDeco,0.181349F,0F,0F);
		
		staffLeftDeco = new ModelRenderer(this,45,9);
		staffLeftDeco.addBox(-3.5F,-14F,-6F,1,5,2);
		staffLeftDeco.setRotationPoint(-2F,-4F,0F);
		staffLeftDeco.setTextureSize(64,64);
		staffLeftDeco.mirror = true;
		setRotation(staffLeftDeco,0.181349F,0F,0F);
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
	    staffRod.render(unitPixel);
	    staffGem.render(unitPixel);
	    staffRightDeco.render(unitPixel);
	    staffLeftDeco.render(unitPixel);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z){
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
}

package chylex.hee.render.model;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelHauntedMiner extends ModelBase{
	private ModelRenderer head, chest, torso, bottom;
	private ModelRenderer leftArm, leftHand, leftThumb;
	private ModelRenderer rightArm, rightHand, rightThumb;

	public ModelHauntedMiner(){
		textureWidth = 64;
		textureHeight = 64;

		head = new ModelRenderer(this,18,0);
		head.addBox(-3.5F,-6.5F,-4.5F,7,7,7);
		head.setRotationPoint(0F,-9F,-1F);
		setRotation(head,0F,0F,0F);
		
		chest = new ModelRenderer(this,11,15);
		chest.addBox(-6.5F,-8.5F,-4.5F,13,6,8);
		chest.setRotationPoint(0F,0F,0F);
		setRotation(chest,0F,0F,0F);
		
		torso = new ModelRenderer(this,16,30);
		torso.addBox(-4.5F,-2.5F,-3.5F,9,4,7);
		torso.setRotationPoint(0F,0F,0F);
		setRotation(torso,0F,0F,0F);
		
		bottom = new ModelRenderer(this,23,42);
		bottom.addBox(-2.5F,1.5F,-2F,5,2,4);
		bottom.setRotationPoint(0F,0F,0F);
		setRotation(bottom,0F,0F,0F);
		
		rightArm = new ModelRenderer(this,0,42);
		rightArm.addBox(-4.5F,-2.5F,-8.5F,5,5,11);
		rightArm.setRotationPoint(-7F,-5F,0F);
		setRotation(rightArm,0F,0F,0F);
		
		rightHand = new ModelRenderer(this,0,34);
		rightHand.addBox(-3.5F,-1.5F,-12.5F,1,3,4);
		setRotation(rightHand,0F,0F,0F);
		
		rightThumb = new ModelRenderer(this,0,29);
		rightThumb.addBox(-1.5F,-1.5F,-11.5F,1,1,3);
		setRotation(rightThumb,0F,0F,0F);
		
		leftArm = new ModelRenderer(this,32,42);
		leftArm.addBox(-0.5F,-2.5F,-8.5F,5,5,11);
		leftArm.setRotationPoint(7F,-5F,0F);
		setRotation(leftArm,0F,0F,0F);
		
		leftHand = new ModelRenderer(this,54,34);
		leftHand.addBox(2.5F,-1.5F,-12.5F,1,3,4);
		setRotation(leftHand,0F,0F,0F);
		
		leftThumb = new ModelRenderer(this,56,29);
		leftThumb.addBox(0.5F,-1.5F,-11.5F,1,1,3);
		setRotation(leftThumb,0F,0F,0F);
		
		chest.addChild(torso);
		chest.addChild(bottom);
		leftArm.addChild(leftHand);
		leftArm.addChild(leftThumb);
		rightArm.addChild(rightHand);
		rightArm.addChild(rightThumb);

	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel){
		GL11.glTranslatef(0F,1.3F,0F);
		super.render(entity,limbSwing,limbSwingAngle,entityTickTime,rotationYaw,rotationPitch,unitPixel);
		setRotationAngles(limbSwing,limbSwingAngle,entityTickTime,rotationYaw,rotationPitch,unitPixel,entity);
		
		head.render(unitPixel);
		chest.render(unitPixel);
		leftArm.render(unitPixel);
		rightArm.render(unitPixel);
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

		head.rotateAngleY = yawRad;
		head.rotateAngleX = pitchRad;
		leftArm.rotateAngleX = 0F;
		rightArm.rotateAngleX = 0F;
	}
}


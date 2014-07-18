package chylex.hee.render.model;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelEndermanHeadBiped extends ModelBiped{
	public ModelEndermanHeadBiped(){
		super();
		textureWidth = 64;
		textureHeight = 32;
		
		bipedHead = new ModelRenderer(this,0,0);
		bipedHead.addBox(-4F,-8F,-4F,8,8,8,0F);
		bipedHead.setRotationPoint(0F,0F,0F);
		
		bipedHead.showModel = true;
        bipedHeadwear.showModel = true;
        bipedBody.showModel = false;
        bipedRightArm.showModel = false;
        bipedLeftArm.showModel = false;
        bipedRightLeg.showModel = false;
        bipedLeftLeg.showModel = false;
	}
	
	@Override
	public void render(Entity entity, float limbSwing, float prevLimbSwing, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel){
		super.render(entity,limbSwing,prevLimbSwing,entityTickTime,rotationYaw,rotationPitch,unitPixel);
		setRotationAngles(limbSwing,prevLimbSwing,entityTickTime,rotationYaw,rotationPitch,unitPixel,entity);
		
		GL11.glPushMatrix();
		GL11.glScalef(1.05F,1.05F,1.05F);
		bipedHead.render(unitPixel);
		GL11.glPopMatrix();
	}

	@Override
	public void setRotationAngles(float limbSwing, float prevLimbSwing, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel, Entity entity){
		super.setRotationAngles(limbSwing,prevLimbSwing,entityTickTime,rotationYaw,rotationPitch,unitPixel,entity);
		
		bipedHead.rotateAngleY = MathUtil.toRad(rotationYaw);
		bipedHead.rotateAngleX = MathUtil.toRad(rotationPitch);
	}
}

package chylex.hee.render.model;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import chylex.hee.system.abstractions.GL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelTokenHolder extends ModelBase{
	private final ModelRenderer outside;
	private float tempRotation, tempCharge;
	
	public ModelTokenHolder(){
		textureWidth = 64;
		textureHeight = 32;
		
		outside = new ModelRenderer(this, 0, 0);
		outside.addBox(-8F, -8F, -8F, 16, 16, 16);
	}
	
	public void setRotation(float rotation){
		this.tempRotation = rotation;
	}
	
	public void setCharge(float charge){
		this.tempCharge = charge;
	}
	
	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel){
		GL.translate(0F, 0.65F, 0F);
		GL.pushMatrix();
		GL.scale(0.25F+0.25F*tempCharge, 0.25F+0.25F*tempCharge, 0.25F+0.25F*tempCharge);
		GL.rotate(tempRotation, 0F, 1F, 0F);
		GL.rotate(55F, 1F, 0F, 1F);
		GL.color(1F, 1F, 1F, 0.95F-0.25F*tempCharge);
		outside.render(unitPixel);
		GL.popMatrix();
	}
}

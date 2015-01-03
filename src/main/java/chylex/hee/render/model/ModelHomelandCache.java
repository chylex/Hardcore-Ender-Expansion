package chylex.hee.render.model;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelHomelandCache extends ModelBase{
	private final ModelRenderer outside;
	private float tempRotation;
	
	public ModelHomelandCache(){
		textureWidth = 64;
		textureHeight = 32;
		
		outside = new ModelRenderer(this,0,0);
		outside.addBox(-8F,-8F,-8F,16,16,16);
	}
	
	public void setRotation(float rotation){
		this.tempRotation = rotation;
	}
	
	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel){
		GL11.glTranslatef(0F,0.65F,0F);
		GL11.glPushMatrix();
		GL11.glScalef(0.5F,0.5F,0.5F);
		GL11.glRotatef(tempRotation,0F,1F,0F);
		GL11.glRotatef(60F,1F,0F,1F);
		GL11.glColor4f(1F,1F,1F,0.5F);
		outside.render(unitPixel);
		GL11.glPopMatrix();
	}
}

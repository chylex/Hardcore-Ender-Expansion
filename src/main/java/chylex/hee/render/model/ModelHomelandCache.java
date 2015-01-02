package chylex.hee.render.model;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelHomelandCache extends ModelBase{
	private final ModelRenderer outside;
	
	public ModelHomelandCache(){
		outside = new ModelRenderer(this);
		outside.setTextureOffset(0,0).addBox(-4F,-4F,-4F,8,8,8);
	}
	
	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAngle, float entityTickTime, float rotationYaw, float rotationPitch, float unitPixel){
		outside.render(unitPixel);
	}
}

package chylex.hee.render.model;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ModelBaconmanHead extends ModelRenderer{
	public ModelBaconmanHead(ModelBase base, int offsetX, int offsetY){
		super(base,offsetX,offsetY);
		addBox(-4F,-8F,-4F,8,8,8,0F);
		setRotationPoint(0F,0F,0F);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(float partialTickTime){
		rotationPointY = -13.25F;
		super.render(partialTickTime);
	}
}

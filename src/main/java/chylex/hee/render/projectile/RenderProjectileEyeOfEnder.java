package chylex.hee.render.projectile;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import chylex.hee.entity.projectile.EntityProjectileEyeOfEnder;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderProjectileEyeOfEnder extends RenderProjectileItem3D{
	public RenderProjectileEyeOfEnder(){
		super(Items.ender_eye);
	}
	
	@Override
	protected float getBob(Entity entity, float partialTickTime){
		return (float)Math.sin((((EntityProjectileEyeOfEnder)entity).timer+partialTickTime)*0.15D)*0.25F;
	}
	
	@Override
	protected float getRotation(Entity entity, float partialTickTime){
		return (((EntityProjectileEyeOfEnder)entity).timer+partialTickTime)*5F;
	}
}

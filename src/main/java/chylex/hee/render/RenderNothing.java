package chylex.hee.render;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderNothing extends Render{
	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTickTime){}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity){
		return null;
	}
}

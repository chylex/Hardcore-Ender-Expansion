package chylex.hee.render.block;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderTNTPrimed;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockEnhancedTNTPrimed extends RenderTNTPrimed{
	public RenderBlockEnhancedTNTPrimed(RenderManager renderManager){
		super(renderManager);
	}

	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTickTime){
		doRender((EntityTNTPrimed)entity,x,y+0.49D,z,yaw,partialTickTime);
	}
}

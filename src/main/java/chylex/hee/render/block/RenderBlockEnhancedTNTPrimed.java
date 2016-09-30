package chylex.hee.render.block;
import net.minecraft.client.renderer.entity.RenderTNTPrimed;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderBlockEnhancedTNTPrimed extends RenderTNTPrimed{
	@Override
	public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTickTime){
		doRender((EntityTNTPrimed)entity, x, y+0.49D, z, yaw, partialTickTime);
	}
}

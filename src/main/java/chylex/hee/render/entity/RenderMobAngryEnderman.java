package chylex.hee.render.entity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMobAngryEnderman extends AbstractRenderMobEnderman{
	public RenderMobAngryEnderman(RenderManager renderManager){
		super(renderManager);
	}
}
package chylex.hee.render.entity;
import java.util.Iterator;
import net.minecraft.client.renderer.entity.RenderEnderman;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.render.entity.layer.LayerEndermanEyes;

@SideOnly(Side.CLIENT)
public class RenderMobParalyzedEnderman extends RenderEnderman{
	public RenderMobParalyzedEnderman(RenderManager renderManager){
		super(renderManager);
		
		for(Iterator<Object> iter = layerRenderers.iterator(); iter.hasNext();){
			if (iter.next() instanceof net.minecraft.client.renderer.entity.layers.LayerEndermanEyes){
				iter.remove();
				break;
			}
		}
		
		addLayer(new LayerEndermanEyes(this));
	}
}
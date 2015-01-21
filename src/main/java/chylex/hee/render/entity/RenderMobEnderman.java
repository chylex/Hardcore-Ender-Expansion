package chylex.hee.render.entity;
import java.util.Iterator;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderEnderman;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.mechanics.misc.Baconizer;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.render.model.ModelBaconmanHead;

@SideOnly(Side.CLIENT)
public class RenderMobEnderman extends RenderEnderman{
	public RenderMobEnderman(RenderManager renderManager){
		super(renderManager);
		
		if (ModCommonProxy.hardcoreEnderbacon){
			((ModelBiped)mainModel).bipedHead = new ModelBaconmanHead(mainModel,0,0);
			
			for(Iterator<Object> iter = layerRenderers.iterator(); iter.hasNext();){
				if (iter.next() instanceof net.minecraft.client.renderer.entity.layers.LayerEndermanEyes){
					iter.remove();
					break;
				}
			}
		}
	}
	
	@Override
	protected ResourceLocation getEntityTexture(Entity entity){
		return Baconizer.mobTexture(this,super.getEntityTexture(entity));
	}
}

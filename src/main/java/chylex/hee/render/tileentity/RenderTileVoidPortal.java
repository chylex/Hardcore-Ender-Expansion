package chylex.hee.render.tileentity;
import chylex.hee.system.abstractions.Meta;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTileVoidPortal extends RenderTilePortalBase{
	@Override
	protected void generateColors(int layer){
		if (tile.getBlockMetadata() == Meta.voidPortalReturn){
			RenderTileEndPortal.setEndPortalColor(this,layer);
			return;
		}
		
		if (layer == 0){
			red = green = blue = 1F;
			return;
		}
		
		
	}
}

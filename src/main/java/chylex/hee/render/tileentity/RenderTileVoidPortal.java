package chylex.hee.render.tileentity;
import chylex.hee.block.BlockVoidPortal;
import chylex.hee.entity.technical.EntityTechnicalVoidPortal;
import chylex.hee.system.abstractions.Meta;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTileVoidPortal extends RenderTilePortalBase{
	private float alpha, translation;
	
	@Override
	protected void onRender(){
		EntityTechnicalVoidPortal data = BlockVoidPortal.getData(tile.getWorldObj(),tile.xCoord,tile.yCoord,tile.zCoord).orElse(null);
		alpha = data == null ? 0F : data.prevRenderAlpha+(data.renderAlpha-data.prevRenderAlpha)*ptt;
		translation = data == null ? 0F : data.prevRenderTranslation+(data.renderTranslation-data.prevRenderTranslation)*ptt;
	}
	
	@Override
	protected float getTranslation(){
		return tile.getBlockMetadata() == Meta.voidPortalReturn ? super.getTranslation() : translation;
	}
	
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
		
		colorMp *= alpha;
	}
}

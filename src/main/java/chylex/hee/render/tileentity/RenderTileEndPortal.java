package chylex.hee.render.tileentity;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.TileEntityEndPortalCustom;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderTileEndPortal extends RenderTilePortalBase{
	private float progress;
	
	@Override
	protected void onRender(){
		TileEntityEndPortalCustom te = getTile();
		progress = te.animate ? te.prevColorProgress+(te.colorProgress-te.prevColorProgress)*ptt : 1F;
	}
	
	@Override
	protected int getLayers(){
		return tile.getBlockMetadata() == Meta.endPortalActive || getTile().animate ? 16 : 2;
	}
	
	@Override
	protected void generateColors(int layer){
		if (layer == 0)red = green = blue = 0F;
		else if (rand.nextInt(3) != 0){
			red = rand.nextFloat()*0.15F+0.75F;
			green = rand.nextFloat()*0.15F+0.75F;
			blue = rand.nextFloat()*0.15F+0.45F;
		}
		else{
			red = rand.nextFloat()*0.1F+0.35F;
			green = rand.nextFloat()*0.1F+0.35F;
			blue = rand.nextFloat()*0.15F+0.85F;
		}
		
		if (progress < 1F && layer > 1){
			// this is cubic easing out with adjustment to kick off new layers just before the old one has full color
			colorMp *= MathUtil.clamp(-progress*(progress-2F)*14.5F-0.95F*(layer-1),0F,1F);
		}
	}
	
	private TileEntityEndPortalCustom getTile(){
		if (!(tile instanceof TileEntityEndPortalCustom))throw new IllegalStateException("Invalid End Portal tile entity passed to the renderer: "+tile.getClass().getName());
		return (TileEntityEndPortalCustom)tile;
	}
}

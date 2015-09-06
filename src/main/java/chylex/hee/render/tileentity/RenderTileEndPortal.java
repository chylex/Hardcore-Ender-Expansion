package chylex.hee.render.tileentity;
import chylex.hee.system.abstractions.Meta;

public class RenderTileEndPortal extends RenderTilePortalBase{
	@Override
	protected int getLayers(){
		return tile.getBlockMetadata() == Meta.endPortalActive ? 16 : 3;
	}
}

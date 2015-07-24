package chylex.hee.tileentity;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityEndPortalCustom extends TileEntityEndPortal{
	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		return AxisAlignedBB.getBoundingBox(xCoord,yCoord,zCoord,xCoord+1D,yCoord+1D,zCoord+1D);
	}
}

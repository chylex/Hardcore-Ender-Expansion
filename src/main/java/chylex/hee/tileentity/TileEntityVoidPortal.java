package chylex.hee.tileentity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityVoidPortal extends TileEntity{
	@Override
	public boolean canUpdate(){
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox(){
		return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord+1D, yCoord+1D, zCoord+1D);
	}
}

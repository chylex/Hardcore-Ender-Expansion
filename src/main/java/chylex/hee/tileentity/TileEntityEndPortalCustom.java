package chylex.hee.tileentity;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.AxisAlignedBB;
import chylex.hee.system.util.MathUtil;

public class TileEntityEndPortalCustom extends TileEntityEndPortal{
	public boolean animate = false;
	public float colorProgress = 1F, prevColorProgress = 1F;
	
	@Override
	public void updateEntity(){
		if (worldObj.isRemote && animate){
			if (MathUtil.floatEquals(prevColorProgress = colorProgress,1F)){
				animate = false;
				return;
			}
			else colorProgress = Math.min(colorProgress+0.0075F,1F);
		}
	}
	
	public void startAnimation(){
		if (!worldObj.isRemote)return;
		
		animate = true;
		colorProgress = prevColorProgress = 0F;
	}
	
	@Override
	public boolean canUpdate(){
		return true; // just in case something optimized it
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		return AxisAlignedBB.getBoundingBox(xCoord,yCoord,zCoord,xCoord+1D,yCoord+1D,zCoord+1D);
	}
}

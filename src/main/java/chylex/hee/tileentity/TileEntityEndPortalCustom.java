package chylex.hee.tileentity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.AxisAlignedBB;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.util.MathUtil;

public class TileEntityEndPortalCustom extends TileEntityEndPortal{
	private byte activationTimer = Byte.MIN_VALUE;
	
	public boolean animate = false;
	public float colorProgress = 1F, prevColorProgress = 1F;
	
	@Override
	public void updateEntity(){
		if (!worldObj.isRemote && activationTimer != Byte.MIN_VALUE){
			if (++activationTimer > 120){
				Pos.at(this).setMetadata(worldObj,Meta.endPortalActive);
				activationTimer = Byte.MIN_VALUE;
			}
		}
		
		if (worldObj.isRemote && animate){
			if (!MathUtil.floatEquals(prevColorProgress = colorProgress,1F))colorProgress = Math.min(colorProgress+0.0075F,1F);
			if (getBlockMetadata() == Meta.endPortalActive)animate = false;
		}
	}
	
	public void startActivating(){
		activationTimer = -15;
	}
	
	public void startAnimation(){
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
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		if (activationTimer != Byte.MIN_VALUE)nbt.setBoolean("activating",true);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		if (nbt.getBoolean("activating"))activationTimer = 120;
	}
}

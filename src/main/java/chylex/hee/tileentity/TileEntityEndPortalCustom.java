package chylex.hee.tileentity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.AxisAlignedBB;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityEndPortalCustom extends TileEntityEndPortal{
	private int activationTimer = -1;
	
	public boolean animate = false;
	public float colorProgress = 1F, prevColorProgress = 1F;
	
	@Override
	public void updateEntity(){
		if (!worldObj.isRemote && activationTimer != -1){
			if (++activationTimer > 135){
				Pos.at(this).setMetadata(worldObj,Meta.endPortalActive);
				activationTimer = -1;
			}
		}
		
		if (worldObj.isRemote && animate){
			if (!MathUtil.floatEquals(prevColorProgress = colorProgress,1F))colorProgress = Math.min(colorProgress+0.0075F,1F);
			if (getBlockMetadata() == Meta.endPortalActive)animate = false;
		}
	}
	
	public void startActivating(){
		activationTimer = 0;
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
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox(){
		return AxisAlignedBB.getBoundingBox(xCoord,yCoord,zCoord,xCoord+1D,yCoord+1D,zCoord+1D);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		if (activationTimer != -1)nbt.setBoolean("activating",true);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		if (nbt.getBoolean("activating"))activationTimer = 135;
	}
}

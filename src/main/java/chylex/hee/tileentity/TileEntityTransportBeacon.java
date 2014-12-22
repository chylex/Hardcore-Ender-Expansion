package chylex.hee.tileentity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.mechanics.energy.EnergyChunkData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityTransportBeacon extends TileEntityAbstractEnergyInventory{
	@Override
	protected byte getDrainTimer(){
		return 1;
	}

	@Override
	protected float getDrainAmount(){
		return EnergyChunkData.energyDrainUnit*5F;
	}

	@Override
	protected boolean isWorking(){
		return false;
	}

	@Override
	protected void onWork(){}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox(){
		return INFINITE_EXTENT_AABB;
	}
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side){
		return ArrayUtils.EMPTY_INT_ARRAY;
	}

	@Override
	public int getSizeInventory(){
		return 0;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is){
		return false;
	}

	@Override
	protected String getContainerDefaultName(){
		return "";
	}
}

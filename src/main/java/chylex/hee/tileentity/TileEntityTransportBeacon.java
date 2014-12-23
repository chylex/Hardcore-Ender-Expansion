package chylex.hee.tileentity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.mechanics.energy.EnergyChunkData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityTransportBeacon extends TileEntityAbstractEnergyInventory{
	private float beamAngle;
	
	@Override
	public void updateEntity(){
		if (worldObj.isRemote){
			beamAngle += 1.5F;
			
			EntityPlayer player = HardcoreEnderExpansion.proxy.getClientSidePlayer();
			
			if (player.getDistance(xCoord,yCoord,zCoord) < 8D && (Math.abs(player.lastTickPosX-player.posX) > 0.0001D || Math.abs(player.lastTickPosY-player.posY) > 0.0001D || Math.abs(player.lastTickPosZ-player.posZ) > 0.0001D)){
				worldObj.markBlockRangeForRenderUpdate(xCoord,yCoord,zCoord,xCoord,yCoord,zCoord);
			}
		}
	}
	
	/**
	 * Returns true if there was no tampering detected and the beacon can be used.
	 */
	public boolean checkIntegrity(){
		return true; // TODO
	}
	
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

	public float getBeamAngle(){
		return beamAngle;
	}
	
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
	@SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared(){
        return 16384D;
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

package chylex.hee.tileentity.base;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class TileEntityAbstractTable extends TileEntityAbstractEnergyInventory implements IInventoryInvalidateable{
	protected static final int totalTime = 1000;
	
	protected short time, timeStep;
	protected float storedEnergy;
	protected byte requiredStardust;
	private boolean postLoadInvalidate;
	
	/**
	 * Return true to reset time and required stardust.
	 */
	protected abstract boolean onWorkFinished();
	
	public int getHoldingStardust(){
		return 0;
	}
	
	public float getMaxStoredEnergy(){
		return 0F;
	}
	
	@Override
	public int getEnergyDrained(){
		return time < totalTime ? 1 : 0;
	}
	
	@Override
	public boolean isDraining(){
		return requiredStardust > 0 && getHoldingStardust() >= requiredStardust;
	}

	@Override
	public void onWork(){
		if ((time += timeStep) >= totalTime){
			if (onWorkFinished()){
				resetTable();
				updateComparatorStatus();
				markDirty();
				invalidateInventory();
			}
			else time = totalTime;
		}
	}
	
	@Override
	public void updateEntity(){
		super.updateEntity();
		
		if (worldObj != null && !worldObj.isRemote && postLoadInvalidate){
			postLoadInvalidate = false;
			invalidateInventory();
		}
	}
	
	public boolean isComparatorOn(){
		return isDraining();
	}
	
	protected final void updateComparatorStatus(){
		if (worldObj != null)worldObj.func_147453_f(xCoord, yCoord, zCoord, blockType);
	}
	
	protected final void resetTable(){
		time = timeStep = requiredStardust = 0;
	}
	
	public final int getTime(){
		return time;
	}
	
	public final int getRequiredStardust(){
		return requiredStardust;
	}
	
	public final float getStoredEnergy(){
		return storedEnergy;
	}
	
	@SideOnly(Side.CLIENT)
	public final void setRequiredStardustClient(int requiredStardust){
		this.requiredStardust = (byte)requiredStardust;
	}
	
	@SideOnly(Side.CLIENT)
	public final void setTimeClient(int time){
		this.time = (short)time;
	}

	@SideOnly(Side.CLIENT)
	public final int getScaledTimeClient(int scale){
		if (time == 0 && timeStep == 0)return -1;
		return MathUtil.ceil(time*(double)scale/totalTime);
	}
	
	@SideOnly(Side.CLIENT)
	public final void setStoredEnergyClient(float energy){
		this.storedEnergy = energy;
	}
	
	@SideOnly(Side.CLIENT)
	public final int getScaledStoredEnergyClient(int scale){
		float maxEnergy = getMaxStoredEnergy();
		return MathUtil.floatEquals(maxEnergy, 0F) ? -1 : MathUtil.ceil(storedEnergy*(double)scale/maxEnergy);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		if (!MathUtil.floatEquals(storedEnergy, 0F))nbt.setFloat("storedEng", storedEnergy);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		if (nbt.hasKey("storedEng"))storedEnergy = nbt.getFloat("storedEng");
		postLoadInvalidate = true;
	}
}

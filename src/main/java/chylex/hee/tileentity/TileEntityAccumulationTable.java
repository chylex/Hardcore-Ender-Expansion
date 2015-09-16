package chylex.hee.tileentity;
import net.minecraft.item.ItemStack;
import chylex.hee.item.ItemAbstractEnergyAcceptor;
import chylex.hee.mechanics.energy.EnergyValues;

public class TileEntityAccumulationTable extends TileEntityAbstractTable{
	private static final int[] slotsAll = new int[]{ 0 };
	private static final float maxStoredEnergy = EnergyValues.unit*50F;
	
	private byte channelCooldown;
	private boolean lastComparatorStatus;

	@Override
	public void invalidateInventory(){}
	
	@Override
	public void updateEntity(){
		super.updateEntity();
		
		if (!worldObj.isRemote && (channelCooldown == 0 || --channelCooldown == 0) && items[0] != null && storedEnergy >= EnergyValues.unit && items[0].getItem() instanceof ItemAbstractEnergyAcceptor){
			ItemAbstractEnergyAcceptor item = (ItemAbstractEnergyAcceptor)items[0].getItem();
			
			if (item.canAcceptEnergy(items[0])){
				if ((storedEnergy -= EnergyValues.unit) < EnergyValues.min)storedEnergy = 0F;
				item.acceptEnergy(items[0]);
				channelCooldown = 4;
				
				if (lastComparatorStatus == false){
					lastComparatorStatus = true;
					updateComparatorStatus();
				}
			}
		}
		
		if (lastComparatorStatus == true && (items[0] == null || !(items[0].getItem() instanceof ItemAbstractEnergyAcceptor) || !((ItemAbstractEnergyAcceptor)items[0].getItem()).canAcceptEnergy(items[0]))){
			lastComparatorStatus = false;
			updateComparatorStatus();
		}
	}
	
	@Override
	public boolean isComparatorOn(){
		return lastComparatorStatus;
	}
	
	@Override
	public float getMaxStoredEnergy(){
		return maxStoredEnergy;
	}
	
	@Override
	protected byte getDrainTimer(){
		return 5;
	}
	
	@Override
	protected int getDrainUnits(){
		return 1;
	}
	
	@Override
	public boolean isWorking(){
		return storedEnergy < maxStoredEnergy;
	}
	
	@Override
	protected void onWork(){
		storedEnergy += (float)getDrainUnits()/getDrainTimer();
	}

	@Override
	protected boolean onWorkFinished(){
		return false;
	}
	
	@Override
	public int getHoldingStardust(){
		return 0;
	}
	
	@Override
	public int getSizeInventory(){
		return 1;
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack is){
		super.setInventorySlotContents(slot,is);
		if (slot == 0)invalidateInventory();
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is){
		return slot == 0 && is.getItem() instanceof ItemAbstractEnergyAcceptor;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side){
		return slotsAll;
	}

	@Override
	protected String getContainerDefaultName(){
		return "container.accumulationTable";
	}
}

package chylex.hee.tileentity;
import net.minecraft.item.ItemStack;
import chylex.hee.item.ItemAbstractEnergyAcceptor;
import chylex.hee.mechanics.energy.EnergyChunkData;

public class TileEntityAccumulationTable extends TileEntityAbstractTable{
	private static final int[] slotsAll = new int[]{ 0 };
	private static final float maxStoredEnergy = EnergyChunkData.energyDrainUnit*50F;
	
	private byte channelCooldown;
	
	@Override
	public void invalidateInventory(){}
	
	@Override
	public void updateEntity(){
		super.updateEntity();
		
		if (!worldObj.isRemote && (channelCooldown == 0 || --channelCooldown == 0) && items[0] != null && storedEnergy >= EnergyChunkData.energyDrainUnit && items[0].getItem() instanceof ItemAbstractEnergyAcceptor){
			ItemAbstractEnergyAcceptor item = (ItemAbstractEnergyAcceptor)items[0].getItem();
			
			if (item.canAcceptEnergy(items[0])){
				if ((storedEnergy -= EnergyChunkData.energyDrainUnit) < EnergyChunkData.minSignificantEnergy)storedEnergy = 0F;
				item.onEnergyAccepted(items[0]);
				channelCooldown = 4;
			}
		}
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
	protected float getDrainAmount(){
		return EnergyChunkData.energyDrainUnit;
	}
	
	@Override
	public boolean isWorking(){
		return storedEnergy < maxStoredEnergy;
	}
	
	@Override
	protected void onWork(){
		storedEnergy += getDrainAmount()/getDrainTimer();
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

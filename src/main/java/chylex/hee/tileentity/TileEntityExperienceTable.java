package chylex.hee.tileentity;
import net.minecraft.item.ItemStack;

public class TileEntityExperienceTable extends TileEntityAbstractEnergyInventory{
	@Override
	protected byte getDrainTimer(){
		return 0;
	}

	@Override
	protected float getDrainAmount(){
		return 0;
	}

	@Override
	protected boolean isWorking(){
		return false;
	}

	@Override
	protected void onWork(){}

	@Override
	public int getSizeInventory(){
		return 0;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is){
		return false;
	}
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side){
		return null;
	}

	@Override
	protected String getContainerDefaultName(){
		return "container.experienceTable";
	}
}

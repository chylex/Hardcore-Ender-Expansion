package chylex.hee.tileentity;
import net.minecraft.item.ItemStack;

public class TileEntityExperienceTable extends TileEntityAbstractTable{
	@Override
	public void invalidateInventory(){}

	@Override
	protected void onWorkFinished(){}
	
	@Override
	public int getHoldingStardust(){
		return 0;
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
	public int[] getAccessibleSlotsFromSide(int side){
		return null;
	}

	@Override
	protected String getContainerDefaultName(){
		return "container.experienceTable";
	}
}

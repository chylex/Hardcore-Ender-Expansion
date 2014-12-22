package chylex.hee.tileentity;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import chylex.hee.item.ItemList;
import chylex.hee.system.util.MathUtil;

public class TileEntityExperienceTable extends TileEntityAbstractTable{
	private static final int[] slotsTop = new int[]{ 0 }, slotsSides = new int[]{ 1 }, slotsBottom = new int[]{ 2 };
	
	/**
	 * 0 = inactive, -1 = active but undecided (because of randomness in exp drop)
	 */
	private byte expAmount;
	
	@Override
	public void invalidateInventory(){
		if (worldObj != null && worldObj.isRemote)return;
		resetTable();
		expAmount = 0;
		
		if (items[0] != null && worldObj != null){
			Block block = Block.getBlockFromItem(items[0].getItem());
			
			if (block != null && block.getExpDrop(worldObj,items[0].getItemDamage(),0) > 0){
				expAmount = -1;
				timeStep = 12;
				requiredStardust = 4;
			}
		}
	}

	@Override
	protected boolean onWorkFinished(){
		if (expAmount == -1)expAmount = (byte)MathUtil.ceil(0.6D*Block.getBlockFromItem(items[0].getItem()).getExpDrop(worldObj,items[0].getItemDamage(),0));
		
		if (items[2] == null)items[2] = new ItemStack(ItemList.exp_bottle,expAmount);
		else if (items[2].stackSize+expAmount <= items[2].getMaxStackSize())items[2].stackSize += expAmount;
		else return false;
		
		if ((items[1].stackSize -= requiredStardust) <= 0)items[1] = null;
		
		if (--items[0].stackSize <= 0){
			items[0] = null;
			expAmount = 0;
		}
		
		return true;
	}
	
	@Override
	public int getHoldingStardust(){
		return items[1] == null ? 0 : items[1].stackSize;
	}
	
	@Override
	public int getSizeInventory(){
		return 3;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is){
		return slot == 1 ? is.getItem() == ItemList.stardust : slot == 0;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side){
		return side == 0 ? slotsBottom : side == 1 ? slotsTop : slotsSides;
	}

	@Override
	protected String getContainerDefaultName(){
		return "container.experienceTable";
	}
}

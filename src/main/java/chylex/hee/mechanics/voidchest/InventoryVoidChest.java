package chylex.hee.mechanics.voidchest;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import chylex.hee.tileentity.TileEntityVoidChest;

public class InventoryVoidChest extends InventoryBasic{
	private TileEntityVoidChest chest;

	public InventoryVoidChest(){
		super("container.voidChest",false,27);
	}
	
	public InventoryVoidChest setChest(TileEntityVoidChest chest){
		this.chest = chest;
		return this;
	}
	
	public void putItemRandomly(ItemStack is, Random rand){
		int size = getSizeInventory();
		
		if (is.isStackable()){
			boolean markDirty = false;
			
			for(int a = 0; a < size; a++){
				ItemStack slotIS = getStackInSlot(a);
				
				if (slotIS == null || slotIS.getItem() != is.getItem() || (slotIS.getItemDamage() != is.getItemDamage() && !slotIS.getHasSubtypes()) ||
					!ItemStack.areItemStackTagsEqual(slotIS,is) || is.getMaxStackSize() != slotIS.getMaxStackSize())continue;
				
				int combined = slotIS.stackSize+is.stackSize, max = is.getMaxStackSize();
				
				if (combined <= max){
					slotIS.stackSize = combined;
					is.stackSize = 0;
					markDirty();
					return;
				}
				else if (slotIS.stackSize < max){
					is.stackSize -= max-slotIS.stackSize;
					slotIS.stackSize = max;
					markDirty = true;
				}
			}
			
			if (markDirty)markDirty();
			if (is.stackSize == 0)return;
		}
		
		for(int attempt = 0; attempt < 3; attempt++){
			int slot = rand.nextInt(size);
			
			if (getStackInSlot(slot) == null){
				setInventorySlotContents(slot,is);
				break;
			}
		}
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player){
		return chest != null && !chest.canPlayerUse(player) ? false : super.isUseableByPlayer(player);
	}

	@Override
	public void openInventory(EntityPlayer player){
		if (chest != null)chest.addPlayerToOpenList();
		super.openInventory();
	}

	@Override
	public void closeInventory(EntityPlayer player){
		if (chest != null)chest.removePlayerFromOpenList();
		super.closeInventory();
		chest = null;
	}
}

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
		
		for(int a = 0; a < size; a++){
			ItemStack slotIS = getStackInSlot(a);
			if (slotIS == null)continue;
			
			// TODO find mergeable stacks
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
	public void openInventory(){
		if (chest != null)chest.addPlayerToOpenList();
		super.openInventory();
	}

	@Override
	public void closeInventory(){
		if (chest != null)chest.removePlayerFromOpenList();
		super.closeInventory();
		chest = null;
	}
}

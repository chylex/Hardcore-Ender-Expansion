package chylex.hee.mechanics.voidchest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import chylex.hee.tileentity.TileEntityVoidChest;

public class InventoryVoidChest extends InventoryBasic{
	private TileEntityVoidChest chest;

	public InventoryVoidChest(){
		super("container.voidchest",false,27);
	}
	
	public void setChest(TileEntityVoidChest chest){
		this.chest = chest;
	}
	
	public void putItemRandomly(ItemStack is){
		for(int attempt = 0; attempt < 3; attempt++){
			int slot = chest.getWorldObj().rand.nextInt(getSizeInventory());
			
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

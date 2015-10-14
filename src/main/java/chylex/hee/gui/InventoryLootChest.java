package chylex.hee.gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import chylex.hee.tileentity.TileEntityLootChest;
import com.google.common.base.Objects;

public class InventoryLootChest extends InventoryBasic{
	private final String playerID;
	private final TileEntityLootChest chest;
	
	public InventoryLootChest(String playerID, TileEntityLootChest chest){
		super("container.lootChest",false,27);
		this.playerID = playerID;
		this.chest = chest;
	}
	
	public InventoryLootChest(String playerID, TileEntityLootChest chest, InventoryLootChest sourceInventory){
		this(playerID,chest);
		
		for(int slot = 0; slot < sourceInventory.getSizeInventory(); slot++){
			ItemStack is = sourceInventory.getStackInSlot(slot);
			if (is != null)setInventorySlotContents(slot,is);
		}
	}
	
	public InventoryLootChest(TileEntityLootChest chest){
		this(null,chest);
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
	}
	
	@Override
	public String getInventoryName(){
		return Objects.firstNonNull(chest.getInventoryNameIfPresent(),super.getInventoryName());
	}
}

package chylex.hee.gui.slots;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotShowCase extends Slot{
	public SlotShowCase(IInventory inv, int id, int x, int z){
		super(inv,id,x,z);
	}
	
	@Override
	public boolean canTakeStack(EntityPlayer player){
		return false;
	}
	
	@Override
	public boolean isItemValid(ItemStack is){
		return false;
	}
}

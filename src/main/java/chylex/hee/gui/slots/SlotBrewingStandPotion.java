package chylex.hee.gui.slots;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;
import chylex.hee.mechanics.brewing.PotionTypes;
import chylex.hee.system.abstractions.nbt.NBT;

public class SlotBrewingStandPotion extends Slot{
	public SlotBrewingStandPotion(IInventory inv, int id, int x, int z){
		super(inv,id,x,z);
	}

	@Override
	public boolean isItemValid(ItemStack is){
		return canHoldPotion(is);
	}

	@Override
	public int getSlotStackLimit(){
		return 1;
	}

	@Override
	public void onPickupFromSlot(EntityPlayer player, ItemStack is){
		if (is.getItem() instanceof ItemPotion && is.getItemDamage() > 0){
			player.addStat(AchievementList.potion,1);
			NBT.item(is,false).removeTag("hasPotionChanged");
		}

		super.onPickupFromSlot(player,is);
	}

	public static boolean canHoldPotion(ItemStack is){
		return is != null && PotionTypes.isPotionItem(is.getItem());
	}
}
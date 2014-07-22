package chylex.hee.gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;

class SlotBrewingStandPotion extends Slot{
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
			if (is.stackTagCompound != null && is.stackTagCompound.hasKey("hasPotionChanged")){
				KnowledgeRegistrations.ENHANCED_BREWING_STAND.tryUnlockFragment(player,0.36F,new byte[]{ 0,1,2 });
				is.stackTagCompound.removeTag("hasPotionChanged");
			}
		}

		super.onPickupFromSlot(player,is);
	}

	public static boolean canHoldPotion(ItemStack is){
		return is != null && (is.getItem() instanceof ItemPotion || is.getItem() == Items.glass_bottle || is.getItem() == ItemList.potion_of_instability);
	}
}
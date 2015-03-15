package chylex.hee.item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemInfestationRemedy extends Item{
	@Override
	public int getMaxItemUseDuration(ItemStack is){
		return 32;
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack is){
		return EnumAction.drink;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		player.setItemInUse(is,getMaxItemUseDuration(is));
		return is;
	}
	
	@Override
	public ItemStack onEaten(ItemStack is, World world, EntityPlayer player){
		if (!player.capabilities.isCreativeMode)--is.stackSize;
		
		// TODO reduce causatum level
		
		if (is.stackSize <= 0)return new ItemStack(Items.glass_bottle);
		player.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
		return is;
	}
}

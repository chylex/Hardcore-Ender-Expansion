package chylex.hee.system.util;
import net.minecraft.item.ItemStack;
import chylex.hee.system.abstractions.nbt.NBT;
import chylex.hee.system.abstractions.nbt.NBTCompound;
import chylex.hee.system.abstractions.nbt.NBTList;

public final class ItemUtil{
	public static NBTCompound getTagSub(ItemStack is, String key, boolean create){
		NBTCompound root = NBT.item(is, create), sub = root.getCompound(key);
		if (!root.hasKey(key) && create)root.setCompound(key, sub);
		return sub;
	}
	
	public static void setName(ItemStack is, String name){
		ItemUtil.getTagSub(is, "display", true).setString("Name", name);
	}
	
	public static void addLore(ItemStack is, String lore){
		NBTCompound display = getTagSub(is, "display", true);
		NBTList loreTag = display.getList("Lore");
		
		if (lore == null)loreTag = new NBTList();
		else loreTag.appendString(lore);
		
		display.setList("Lore", loreTag);
	}
	
	public static void setArmorColor(ItemStack is, int color){
		getTagSub(is, "display", true).setInt("color", color);
	}
	
	public static boolean canAddOneItemTo(ItemStack is, ItemStack itemToAdd){
		return is.isStackable() && is.getItem() == itemToAdd.getItem() &&
			   (!is.getHasSubtypes() || is.getItemDamage() == itemToAdd.getItemDamage()) &&
			   ItemStack.areItemStackTagsEqual(is, itemToAdd) &&
			   is.stackSize+1 <= is.getMaxStackSize();
	}
}
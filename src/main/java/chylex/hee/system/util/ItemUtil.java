package chylex.hee.system.util;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants.NBT;

public final class ItemUtil{
	private static final NBTTagCompound dummyTag = new NBTTagCompoundDummy();
	
	public static NBTTagCompound getTagRoot(ItemStack is, boolean create){
		NBTTagCompound nbt = is.getTagCompound();
		
		if (nbt == null){
			if (create)is.setTagCompound(nbt = new NBTTagCompound());
			else return dummyTag;
		}
		
		return nbt;
	}
	
	public static NBTTagCompound getTagSub(ItemStack is, String key, boolean create){
		NBTTagCompound root = getTagRoot(is,create), sub = root.getCompoundTag(key);
		if (!root.hasKey(key) && create)root.setTag(key,sub);
		return sub;
	}
	
	public static void addLore(ItemStack is, String lore){
		NBTTagCompound display = getTagSub(is,"display",true);
		NBTTagList loreTag = display.getTagList("Lore",NBT.TAG_STRING);
		
		if (lore == null)loreTag = new NBTTagList();
		else loreTag.appendTag(new NBTTagString(lore));
		
		display.setTag("Lore",loreTag);
	}
	
	public static void setArmorColor(ItemStack is, int color){
		getTagSub(is,"display",true).setInteger("color",color);
	}
	
	public static boolean canAddOneItemTo(ItemStack is, ItemStack itemToAdd){
		return is.isStackable() && is.getItem() == itemToAdd.getItem() &&
			   (!is.getHasSubtypes() || is.getItemDamage() == itemToAdd.getItemDamage()) &&
			   ItemStack.areItemStackTagsEqual(is,itemToAdd) &&
			   is.stackSize+1 <= is.getMaxStackSize();
	}
	
	public static final class NBTTagCompoundDummy extends NBTTagCompound{
		private NBTTagCompoundDummy(){}
		@Override public void setBoolean(String key, boolean value){}
		@Override public void setByte(String key, byte value){}
		@Override public void setByteArray(String key, byte[] value){}
		@Override public void setDouble(String key, double value){}
		@Override public void setFloat(String key, float value){}
		@Override public void setIntArray(String key, int[] value){}
		@Override public void setInteger(String key, int value){}
		@Override public void setLong(String key, long value){}
		@Override public void setShort(String key, short value){}
		@Override public void setString(String key, String value){}
		@Override public void setTag(String key, NBTBase value){}
		@Override public void removeTag(String key){}
	}
}
package chylex.hee.system.util;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants.NBT;

public final class ItemUtil{
	private static final NBTTagCompound dummyTag = new NBTTagCompoundDummy();
	
	public static NBTTagCompound getNBT(ItemStack is, boolean create){
		NBTTagCompound nbt = is.getTagCompound();
		
		if (nbt == null){
			if (create)is.setTagCompound(nbt = new NBTTagCompound());
			else return dummyTag;
		}
		
		return nbt;
	}
	
	public static void addLore(ItemStack is, String lore){
		NBTTagCompound display = is.getSubCompound("display",true);
		NBTTagList loreTag = display.getTagList("Lore",NBT.TAG_STRING);
		
		if (lore == null)loreTag = new NBTTagList();
		else loreTag.appendTag(new NBTTagString(lore));
		
		display.setTag("Lore",loreTag);
	}
	
	public static void setArmorColor(ItemStack is, int color){
		is.getSubCompound("display",true).setInteger("color",color);
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
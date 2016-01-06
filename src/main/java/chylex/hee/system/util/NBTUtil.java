package chylex.hee.system.util;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTBase.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants.NBT;

public final class NBTUtil{
	public static <T extends NBTBase> NBTTagList writeList(Stream<T> elementStream){
		NBTTagList tag = new NBTTagList();
		elementStream.forEach(ele -> tag.appendTag(ele));
		return tag;
	}

	public static <T extends NBTBase> void writeList(NBTTagCompound parent, String key, Stream<T> elementStream){
		parent.setTag(key,writeList(elementStream));
	}
	
	public static <T extends NBTBase> Stream<T> readList(NBTTagList list){
		return list.tagList.stream();
	}
	
	public static Stream<String> readStringList(NBTTagCompound parent, String key){
		return ((List<NBTTagString>)parent.getTagList(key,NBT.TAG_STRING).tagList).stream().map(tag -> tag.func_150285_a_());
	}
	
	public static Stream<NBTPrimitive> readNumericList(NBTTagCompound parent, String key){
		return parent.hasKey(key) ? ((List<NBTPrimitive>)((NBTTagList)parent.getTag(key)).tagList).stream() : Stream.empty();
	}
	
	public static Stream<NBTTagCompound> readCompoundList(NBTTagCompound parent, String key){
		return ((List<NBTTagCompound>)parent.getTagList(key,NBT.TAG_COMPOUND).tagList).stream();
	}
	
	public static NBTTagList writeInventory(IInventory inv){
		NBTTagList list = new NBTTagList();
		
		for(int slot = 0; slot < inv.getSizeInventory(); slot++){
			ItemStack is = inv.getStackInSlot(slot);
			
			if (is != null){
				NBTTagCompound itemTag = is.writeToNBT(new NBTTagCompound());
				itemTag.setByte("_",(byte)slot);
				list.appendTag(itemTag);
			}
		}
		
		return list;
	}
	
	public static void readInventory(NBTTagList list, IInventory inv){
		for(int slot = 0; slot < list.tagCount(); slot++){
			NBTTagCompound itemTag = list.getCompoundTagAt(slot);
			inv.setInventorySlotContents(itemTag.getByte("_"),ItemStack.loadItemStackFromNBT(itemTag));
		}
	}
	
	public static NBTTagCompound createCallbackTag(Runnable callback){
		return new NBTTagCompoundCallback(callback);
	}
	
	private static final class NBTTagCompoundCallback extends NBTTagCompound{
		private final Runnable callback;
		
		NBTTagCompoundCallback(Runnable callback){
			this.callback = callback;
		}
		
		@Override public void setTag(String key, NBTBase value){ super.setTag(key,value); callback.run(); }
		@Override public void setByte(String key, byte value){ super.setByte(key,value); callback.run(); }
		@Override public void setShort(String key, short value){ super.setShort(key,value); callback.run(); }
		@Override public void setInteger(String key, int value){ super.setInteger(key,value); callback.run(); }
		@Override public void setLong(String key, long value){ super.setLong(key,value); callback.run(); }
		@Override public void setFloat(String key, float value){ super.setFloat(key,value); callback.run(); }
		@Override public void setDouble(String key, double value){ super.setDouble(key,value); callback.run(); }
		@Override public void setString(String key, String value){ super.setString(key,value); callback.run(); }
		@Override public void setByteArray(String key, byte[] value){ super.setByteArray(key,value); callback.run(); }
		@Override public void setIntArray(String key, int[] value){ super.setIntArray(key,value); callback.run(); }
	}
	
	private NBTUtil(){}
}

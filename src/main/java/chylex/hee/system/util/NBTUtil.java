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
	
	private NBTUtil(){}
}

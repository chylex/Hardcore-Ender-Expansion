package chylex.hee.system.abstractions.nbt;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import chylex.hee.system.abstractions.nbt.NBT.IStringIntConsumer;
import chylex.hee.system.abstractions.nbt.NBT.IStringLongConsumer;

public class NBTCompound{
	private final NBTTagCompound tag;
	
	public NBTCompound(NBTTagCompound tag){
		this.tag = tag == null ? new NBTTagCompound() : tag;
	}
	
	public NBTCompound(){
		this(new NBTTagCompound());
	}
	
	public NBTTagCompound getUnderlyingTag(){
		return tag;
	}
	
	// NEW HANDLING
	
	public Set<String> keySet(){
		return tag.func_150296_c();
	}
	
	public int size(){
		return tag.func_150296_c().size();
	}
	
	public void forEach(BiConsumer<NBTCompound, String> consumer){
		for(String key:keySet())consumer.accept(this, key);
	}
	
	public void forEachInt(IStringIntConsumer consumer){
		for(String key:keySet())consumer.accept(key, getInt(key));
	}
	
	public void forEachLong(IStringLongConsumer consumer){
		for(String key:keySet())consumer.accept(key, getLong(key));
	}
	
	public void forEachCompound(BiConsumer<String, NBTCompound> consumer){
		for(String key:keySet())consumer.accept(key, getCompound(key));
	}
	
	// CUSTOM WRITERS AND READERS
	
	public void writeInventory(String key, IInventory inventory){
		NBTTagList list = new NBTTagList();
		
		for(int slot = 0; slot < inventory.getSizeInventory(); slot++){
			ItemStack is = inventory.getStackInSlot(slot);
			
			if (is != null){
				NBTTagCompound itemTag = is.writeToNBT(new NBTTagCompound());
				itemTag.setByte("_", (byte)slot);
				list.appendTag(itemTag);
			}
		}
		
		setTag(key, list);
	}
	
	public void readInventory(String key, IInventory inventory){
		NBTList list = getList(key);
		
		for(int slot = 0; slot < list.size(); slot++){
			NBTCompound itemTag = list.getCompound(slot);
			inventory.setInventorySlotContents(itemTag.getByte("_"), ItemStack.loadItemStackFromNBT(itemTag.getUnderlyingTag()));
		}
	}
	
	public <T extends NBTBase> void writeList(String key, Stream<T> stream){
		NBTTagList tag = new NBTTagList();
		stream.forEach(tag::appendTag);
		setTag(key, tag);
	}
	
	public void writeList(String key, IntStream stream){
		writeList(key, stream.mapToObj(NBTTagInt::new));
	}
	
	public void writeList(String key, LongStream stream){
		writeList(key, stream.mapToObj(NBTTagLong::new));
	}
	
	public void writeList(String key, DoubleStream stream){
		writeList(key, stream.mapToObj(NBTTagDouble::new));
	}
	
	// DELEGATE SETTERS

	public void setTag(String key, NBTBase value){
		tag.setTag(key, value);
	}

	public void setBool(String key, boolean value){
		tag.setBoolean(key, value);
	}

	public void setByte(String key, byte value){
		tag.setByte(key, value);
	}

	public void setShort(String key, short value){
		tag.setShort(key, value);
	}

	public void setInt(String key, int value){
		tag.setInteger(key, value);
	}

	public void setLong(String key, long value){
		tag.setLong(key, value);
	}

	public void setFloat(String key, float value){
		tag.setFloat(key, value);
	}

	public void setDouble(String key, double value){
		tag.setDouble(key, value);
	}

	public void setString(String key, String value){
		tag.setString(key, value);
	}

	public void setByteArray(String key, byte[] value){
		tag.setByteArray(key, value);
	}

	public void setIntArray(String key, int[] value){
		tag.setIntArray(key, value);
	}
	
	public void setCompound(String key, NBTTagCompound value){
		tag.setTag(key, value);
	}
	
	public void setCompound(String key, NBTCompound value){
		tag.setTag(key, value.getUnderlyingTag());
	}
	
	public void setList(String key, NBTTagList value){
		tag.setTag(key, value);
	}
	
	public void setList(String key, NBTList value){
		tag.setTag(key, value.getUnderlyingTag());
	}
	
	// DELEGATE GETTERS
	
	public NBTBase getTag(String key){
		return tag.getTag(key);
	}

	public boolean getBool(String key){
		return tag.getBoolean(key);
	}

	public byte getByte(String key){
		return tag.getByte(key);
	}

	public short getShort(String key){
		return tag.getShort(key);
	}

	public int getInt(String key){
		return tag.getInteger(key);
	}

	public long getLong(String key){
		return tag.getLong(key);
	}

	public float getFloat(String key){
		return tag.getFloat(key);
	}

	public double getDouble(String key){
		return tag.getDouble(key);
	}

	public String getString(String key){
		return tag.getString(key);
	}

	public byte[] getByteArray(String key){
		return tag.getByteArray(key);
	}

	public int[] getIntArray(String key){
		return tag.getIntArray(key);
	}

	public NBTCompound getCompound(String key){
		return new NBTCompound(tag.getCompoundTag(key));
	}
	
	public NBTList getList(String key){
		return new NBTList((NBTTagList)tag.getTag(key));
	}
	
	// MISC DELEGATES
	
	public boolean hasKey(String key){
		return tag.hasKey(key);
	}
	
	public boolean isEmpty(){
		return tag.hasNoTags();
	}
	
	public void removeTag(String key){
		tag.removeTag(key);
	}

	@Override
	public String toString(){
		return tag.toString();
	}
}

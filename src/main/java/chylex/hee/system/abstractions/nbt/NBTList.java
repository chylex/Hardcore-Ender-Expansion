package chylex.hee.system.abstractions.nbt;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTBase.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagString;

public class NBTList{
	private final NBTTagList tag;

	public NBTList(NBTTagList tag){
		this.tag = tag;
	}
	
	// APPENDING
	
	public void appendTag(NBTBase value){
		tag.appendTag(value);
	}
	
	public void appendString(String value){
		tag.appendTag(new NBTTagString(value));
	}
	
	public void appendInt(int value){
		tag.appendTag(new NBTTagInt(value));
	}
	
	public void appendLong(long value){
		tag.appendTag(new NBTTagLong(value));
	}
	
	public void appendDouble(double value){
		tag.appendTag(new NBTTagDouble(value));
	}
	
	// READING
	
	public Stream<NBTPrimitive> readPrimitives(){
		return ((List<NBTPrimitive>)tag.tagList).stream();
	}
	
	public IntStream readInts(){
		return readPrimitives().mapToInt(NBTPrimitive::func_150287_d);
	}
	
	public LongStream readLongs(){
		return readPrimitives().mapToLong(NBTPrimitive::func_150291_c);
	}
	
	public DoubleStream readDoubles(){
		return readPrimitives().mapToDouble(NBTPrimitive::func_150286_g);
	}
	
	public Stream<String> readStrings(){
		return ((List<NBTTagString>)tag.tagList).stream().map(NBTTagString::func_150285_a_);
	}
	
	public Stream<NBTCompound> readCompounds(){
		return ((List<NBTTagCompound>)tag.tagList).stream().map(NBTCompound::new);
	}
	
	// DELEGATES
	
	public int size(){
		return tag.tagCount();
	}
	
	@Override
	public String toString(){
		return tag.toString();
	}
}

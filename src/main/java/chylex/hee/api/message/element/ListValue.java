package chylex.hee.api.message.element;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import chylex.hee.api.message.element.base.Precondition;

public class ListValue<T> extends Precondition<List<T>>{
	public static final ListValue<String> strings(Precondition<String> condition){
		return new ListValue<>(NBT.TAG_STRING,condition);
	}
	
	public static final ListValue<NBTTagCompound> tags(Precondition<NBTTagCompound> condition){
		return new ListValue<>(NBT.TAG_COMPOUND,condition);
	}
	
	private final byte tagType;
	private final Precondition<T> condition;
	
	ListValue(int arrayType, Precondition<T> condition){
		this.tagType = (byte)arrayType;
		this.condition = condition;
	}
	
	@Override
	public boolean checkType(NBTBase tag){
		return tag != null && tag.getId() == NBT.TAG_LIST && ((NBTTagList)tag).func_150303_d() == tagType;
	}

	@Override
	public boolean checkValue(NBTBase tag){
		for(NBTBase element:(List<NBTBase>)((NBTTagList)tag).tagList){
			if (!condition.checkType(element) || !condition.checkValue(element))return false;
		}
		
		return true;
	}

	@Override
	public List<T> getValue(NBTBase tag){
		List<T> elements = new ArrayList<>();
		for(NBTBase element:(List<NBTBase>)((NBTTagList)tag).tagList)elements.add(condition.getValue(tag));
		return elements;
	}
}

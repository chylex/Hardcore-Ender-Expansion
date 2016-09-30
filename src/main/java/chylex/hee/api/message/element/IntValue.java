package chylex.hee.api.message.element;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTBase.NBTPrimitive;
import net.minecraftforge.common.util.Constants.NBT;
import chylex.hee.api.message.element.base.Precondition;

public class IntValue extends Precondition<Integer>{
	public static final IntValue any(){
		return new IntValue(Integer.MIN_VALUE, Integer.MAX_VALUE);
	}
	
	public static final IntValue positive(){
		return new IntValue(1, Integer.MAX_VALUE);
	}
	
	public static final IntValue negative(){
		return new IntValue(Integer.MIN_VALUE, -1);
	}
	
	public static final IntValue positiveOrZero(){
		return new IntValue(0, Integer.MAX_VALUE);
	}
	
	public static final IntValue negativeOrZero(){
		return new IntValue(Integer.MIN_VALUE, 0);
	}
	
	public static final IntValue range(int minValue, int maxValue){
		return new IntValue(minValue, maxValue);
	}
	
	private int minValue, maxValue;
	
	private IntValue(int minValue, int maxValue){
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	@Override
	public boolean checkType(NBTBase tag){
		return tag != null && (tag.getId() == NBT.TAG_INT || tag.getId() == NBT.TAG_BYTE || tag.getId() == NBT.TAG_SHORT || tag.getId() == NBT.TAG_LONG);
	}
	
	@Override
	public boolean checkValue(NBTBase tag){
		int value = ((NBTPrimitive)tag).func_150287_d();
		return value >= minValue && value <= maxValue;
	}
	
	@Override
	public Integer getValue(NBTBase tag){
		return ((NBTPrimitive)tag).func_150287_d();
	}
}

package chylex.hee.api.message.element;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraftforge.common.util.Constants.NBT;
import chylex.hee.api.message.element.base.Precondition;

public abstract class IntArrayValue extends Precondition<int[]>{
	public static final IntArrayValue any(){
		return new IntArrayValueAny();
	}
	
	public static final IntArrayValue condition(Precondition<Integer> condition){
		return new IntArrayValueCondition(condition, IntValue.any());
	}
	
	public static final IntArrayValue condition(Precondition<Integer> condition, Precondition<Integer> elementCount){
		return new IntArrayValueCondition(condition, elementCount);
	}
	
	IntArrayValue(){}
	
	@Override
	public final boolean checkType(NBTBase tag){
		return tag != null && tag.getId() == NBT.TAG_INT_ARRAY;
	}

	@Override
	public final int[] getValue(NBTBase tag){
		return ((NBTTagIntArray)tag).func_150302_c();
	}
	
	private static class IntArrayValueAny extends IntArrayValue{
		IntArrayValueAny(){}
		
		@Override
		public boolean checkValue(NBTBase tag){
			return true;
		}
	}
	
	private static class IntArrayValueCondition extends IntArrayValue{
		private final Precondition<Integer> condition;
		private final Precondition<Integer> elementCount;
		
		IntArrayValueCondition(Precondition<Integer> condition, Precondition<Integer> elementCount){
			this.condition = condition;
			this.elementCount = elementCount;
		}
		
		@Override
		public boolean checkValue(NBTBase tag){
			if (!elementCount.checkValue(new NBTTagInt(getValue(tag).length)))return false;
			
			for(int value:getValue(tag)){
				if (!condition.checkValue(new NBTTagInt(value)))return false;
			}
			
			return true;
		}
	}
}

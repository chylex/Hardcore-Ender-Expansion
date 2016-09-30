package chylex.hee.api.message.element;
import java.util.Arrays;
import java.util.function.Predicate;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants.NBT;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.api.message.element.base.Precondition;

public abstract class StringValue extends Precondition<String>{
	public static StringValue any(){
		return new StringValueAny();
	}
	
	public static StringValue one(String...validValues){
		return new StringValueArray(validValues);
	}
	
	public static StringValue function(Predicate<String> checkFunction){
		return new StringValueFunction(checkFunction);
	}
	
	private StringValue(){}
	
	@Override
	public final boolean checkType(NBTBase tag){
		return tag != null && tag.getId() == NBT.TAG_STRING;
	}
	
	@Override
	public final String getValue(NBTBase tag){
		return ((NBTTagString)tag).func_150285_a_();
	}
	
	private static class StringValueAny extends StringValue{
		StringValueAny(){}
		
		@Override
		public boolean checkValue(NBTBase tag){
			return true;
		}
	}
	
	private static class StringValueArray extends StringValue{
		private final String[] values;
		
		private StringValueArray(String[] values){
			this.values = Arrays.copyOf(values, values.length);
		}
		
		@Override
		public boolean checkValue(NBTBase tag){
			return ArrayUtils.contains(values, getValue(tag));
		}
	}
	
	private static class StringValueFunction extends StringValue{
		private final Predicate<String> checkFunction;
		
		private StringValueFunction(Predicate<String> checkFunction){
			this.checkFunction = checkFunction;
		}
		
		@Override
		public boolean checkValue(NBTBase tag){
			return checkFunction.test(getValue(tag));
		}
	}
}

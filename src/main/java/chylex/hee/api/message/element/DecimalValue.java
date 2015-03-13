package chylex.hee.api.message.element;
import chylex.hee.api.message.element.base.Precondition;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTBase.NBTPrimitive;
import net.minecraftforge.common.util.Constants.NBT;

public class DecimalValue extends Precondition<Double>{
	public static final DecimalValue any(){
		return new DecimalValue(Double.MIN_VALUE,Double.MAX_VALUE);
	}
	
	public static final DecimalValue positiveOrZero(){
		return new DecimalValue(0,Double.MAX_VALUE);
	}
	
	public static final DecimalValue negativeOrZero(){
		return new DecimalValue(Double.MIN_VALUE,0);
	}
	
	private double minValue, maxValue;
	
	private DecimalValue(double minValue, double maxValue){
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	@Override
	public boolean checkType(NBTBase tag){
		return tag != null && (tag.getId() == NBT.TAG_INT || tag.getId() == NBT.TAG_BYTE || tag.getId() == NBT.TAG_SHORT || tag.getId() == NBT.TAG_LONG || tag.getId() == NBT.TAG_DOUBLE || tag.getId() == NBT.TAG_FLOAT);
	}
	
	@Override
	public boolean checkValue(NBTBase tag){
		double value = ((NBTPrimitive)tag).func_150286_g();
		return value >= minValue && value <= maxValue;
	}
	
	@Override
	public Double getValue(NBTBase tag){
		return ((NBTPrimitive)tag).func_150286_g();
	}
}

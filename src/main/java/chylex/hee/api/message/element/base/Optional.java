package chylex.hee.api.message.element.base;
import net.minecraft.nbt.NBTBase;

public class Optional<T> extends Precondition<T>{
	public static final <T> Optional<T> of(Precondition<T> precondition, T defaultValue){
		return new Optional<>(precondition,defaultValue);
	}
	
	private final Precondition<T> precondition;
	private final T defaultValue;
	
	private Optional(Precondition<T> precondition, T defaultValue){
		this.precondition = precondition;
		this.defaultValue = defaultValue;
	}
	
	@Override
	public boolean checkType(NBTBase tag){
		return tag == null || precondition.checkType(tag);
	}
	
	@Override
	public boolean checkValue(NBTBase tag){
		return tag == null || precondition.checkValue(tag);
	}

	@Override
	public T getValue(NBTBase tag){
		return tag == null ? defaultValue : precondition.getValue(tag);
	}
}

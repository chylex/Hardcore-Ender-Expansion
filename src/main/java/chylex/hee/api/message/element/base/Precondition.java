package chylex.hee.api.message.element.base;
import net.minecraft.nbt.NBTBase;

public abstract class Precondition<T>{
	/**
	 * Checks whether a tag is valid. The tag can be null if it doesn't exist!
	 */
	public abstract boolean checkType(NBTBase tag);
	
	/**
	 * Checks the contents of a tag. This only runs if checkType returns true.
	 */
	public abstract boolean checkValue(NBTBase tag);
	
	public abstract T getValue(NBTBase tag);
}

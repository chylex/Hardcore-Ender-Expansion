package chylex.hee.system.collections;
import java.util.EnumSet;

public final class EmptyEnumSet{
	private enum UselessEnum{}
	
	/**
	 * This works, don't fucking touch it, and don't do any writes into the EnumSet because things will explode.
	 */
	public static <T extends Enum<T>> EnumSet<T> get(){
		return (EnumSet<T>)EnumSet.noneOf(UselessEnum.class);
	}
	
	private EmptyEnumSet(){}
}

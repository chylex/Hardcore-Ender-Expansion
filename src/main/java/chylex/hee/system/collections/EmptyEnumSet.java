package chylex.hee.system.collections;
import java.util.EnumSet;

public final class EmptyEnumSet{
	private enum UselessEnum{}
	
	private static final EnumSet iAmTheOneAndOnly = EnumSet.noneOf(UselessEnum.class);
	
	/**
	 * This works, don't fucking touch it, and don't do any writes into the EnumSet because things will explode.
	 */
	public static <T extends Enum<T>> EnumSet<T> get(){
		return iAmTheOneAndOnly;
	}
	
	private EmptyEnumSet(){}
}

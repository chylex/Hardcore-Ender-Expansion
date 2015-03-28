package chylex.hee.system.test;
import chylex.hee.system.util.MathUtil;
import com.google.common.base.Objects;

public final class Assert{
	/**
	 * Always throws an IllegalStateException.
	 */
	public static void fail(String message){
		throw new IllegalStateException(message);
	}
	
	/**
	 * Throws an IllegalStateException if the provided value is false.
	 */
	public static void state(boolean value, String message){
		if (!value)throw new IllegalStateException(message);
	}
	
	/**
	 * Throws an IllegalStateException if the object is not null.
	 * Use $ to put the string value of the object into the message.
	 */
	public static void isNull(Object value, String message){
		if (value != null)throw new IllegalStateException(message.replace("$",value.toString()));
	}
	
	/**
	 * Throws an IllegalStateException if the object is null.
	 */
	public static void notNull(Object value, String message){
		if (value == null)throw new IllegalStateException(message);
	}
	
	/**
	 * Throws an IllegalStateException if the objects are not equal (number objects are checked in a special way).
	 * Use $1 to substitute first value and $2 to substitute second value in the message.
	 */
	public static void equal(Object value1, Object value2, String message){
		if (!Objects.equal(value1,value2) && !areNumbersEqual(value1,value2))throw new IllegalStateException(message.replace("$1",value1 == null ? "null" : value1.toString()).replace("$2",value2 == null ? "null" : value2.toString()));
	}
	
	private static boolean areNumbersEqual(Object value1, Object value2){
		if (!(value1 instanceof Number && value2 instanceof Number))return false;
		
		if (value1 instanceof Double || value1 instanceof Float)return MathUtil.floatEquals(((Number)value1).floatValue(),((Number)value2).floatValue());
		else return ((Number)value1).longValue() == ((Number)value2).longValue();
	}
	
	private Assert(){}
}

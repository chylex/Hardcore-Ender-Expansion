package chylex.hee.system.test;
import chylex.hee.system.util.MathUtil;
import com.google.common.base.Objects;

public final class Assert{
	@FunctionalInterface
	public static interface AssertionSuccess{
		void call();
	}

	@FunctionalInterface
	public static interface AssertionFailure{
		void call(IllegalStateException ex);
	}
	
	private static AssertionSuccess onSuccess = () -> {};
	private static AssertionFailure onFail = (ex) -> { throw ex; };
	
	public static void setSuccessCallback(AssertionSuccess callback){
		Assert.onSuccess = callback;
	}
	
	public static void setFailCallback(AssertionFailure callback){
		Assert.onFail = callback;
	}
	
	/**
	 * Always fails.
	 */
	public static void fail(){
		fail("Triggered failure.");
	}
	
	/**
	 * Always fails.
	 */
	public static void fail(String message){
		onFail.call(new IllegalStateException(message));
	}
	
	/**
	 * Fails if the provided value is not true.
	 */
	public static void isTrue(boolean value){
		isTrue(value,"Expected value to be true.");
	}
	
	/**
	 * Fails if the provided value is not true.
	 */
	public static void isTrue(boolean value, String message){
		if (value)onSuccess.call();
		else onFail.call(new IllegalStateException(message));
	}
	
	/**
	 * Fails if the provided value is not false.
	 */
	public static void isFalse(boolean value){
		isFalse(value,"Expected value to be false.");
	}
	
	/**
	 * Fails if the provided value is not false.
	 */
	public static void isFalse(boolean value, String message){
		if (!value)onSuccess.call();
		else onFail.call(new IllegalStateException(message));
	}
	
	/**
	 * Fails if the provided value is not null.
	 */
	public static void isNull(Object value){
		isNull(value,"Expected value to be null, got $ instead.");
	}
	
	/**
	 * Fails if the provided value is not null.
	 * Use $ to put the string value of the object into the message.
	 */
	public static void isNull(Object value, String message){
		if (value == null)onSuccess.call();
		else onFail.call(new IllegalStateException(message.replace("$",value.toString())));
	}
	
	/**
	 * Fails if the provided value is null.
	 */
	public static void notNull(Object value){
		notNull(value,"Expected value to not be null.");
	}
	
	/**
	 * Fails if the provided value is null.
	 */
	public static void notNull(Object value, String message){
		if (value != null)onSuccess.call();
		else onFail.call(new IllegalStateException(message));
	}
	
	/**
	 * Fails if the value is not an instance of targetClass.
	 */
	public static void instanceOf(Object value, Class<?> targetClass){
		instanceOf(value,targetClass,"Expected value to be instance of $2, instead the class is $1.");
	}
	
	/**
	 * Fails if the value is not an instance of targetClass.
	 * Use $1 to put the current object class into the message, and $2 for the target class.
	 */
	public static void instanceOf(Object value, Class<?> targetClass, String message){
		if (value != null && targetClass.isAssignableFrom(value.getClass()))onSuccess.call();
		else onFail.call(new IllegalStateException(message.replace("$1",value == null ? "<null>" : value.getClass().getName()).replace("$2",targetClass.getName())));
	}
	
	/**
	 * Fails if the value is not equal to target (supports wrapped primitives).
	 */
	public static void equal(Object value, Object target){
		equal(value,target,"Expected value to be equal to $2, got $1 instead.");
	}
	
	/**
	 * Fails if the value is not equal to target (supports wrapped primitives).
	 * Use $1 to substitute value and $2 to substitute the target in the message.
	 */
	public static void equal(Object value, Object target, String message){
		if (Objects.equal(value,target) || arePrimitivesEqual(value,target))onSuccess.call();
		else onFail.call(new IllegalStateException(message.replace("$1",value == null ? "null" : value.toString()).replace("$2",target == null ? "null" : target.toString())));
	}
	
	/**
	 * Compares two objects using their primitive values.
	 */
	private static boolean arePrimitivesEqual(Object value1, Object value2){
		if (value1 instanceof Number && value2 instanceof Number){
			if (value1 instanceof Double || value1 instanceof Float)return MathUtil.floatEquals(((Number)value1).floatValue(),((Number)value2).floatValue());
			else return ((Number)value1).longValue() == ((Number)value2).longValue();
		}
		else if (value1 instanceof Boolean && value2 instanceof Boolean){
			return ((Boolean)value1).booleanValue() == ((Boolean)value2).booleanValue();
		}
		else if (value1 instanceof Character && value2 instanceof Character){
			return ((Character)value1).charValue() == ((Character)value2).charValue();
		}
		else return false;
	}
	
	private Assert(){}
}

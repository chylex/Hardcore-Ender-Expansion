package chylex.hee.system.test;
import com.google.common.base.Objects;

public final class Assert{
	public static void state(boolean value, String message){
		if (!value)throw new IllegalStateException(message);
	}
	
	public static void equal(Object value1, Object value2, String message){
		if (!Objects.equal(value1,value2))throw new IllegalStateException(message.replace("$1",value1 == null ? "null" : value1.toString()).replace("$2",value2 == null ? "null" : value2.toString()));
	}
	
	private Assert(){}
}

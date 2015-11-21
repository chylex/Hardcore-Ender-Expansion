package chylex.hee.system.util;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class Unfinalizer{
	private static final Field modifiersField;
	
	static{
		try{
			modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
		}catch(NoSuchFieldException | SecurityException e){
			throw new RuntimeException("Could not load Unfinalizer!",e);
		}
	}
	
	public static void unfinalizeField(Field field) throws IllegalArgumentException, IllegalAccessException{
		try{
			modifiersField.setInt(field,field.getModifiers() & ~Modifier.FINAL);
		}catch(IllegalArgumentException | IllegalAccessException e){
			throw e;
		}
	}
	
	private Unfinalizer(){}
}

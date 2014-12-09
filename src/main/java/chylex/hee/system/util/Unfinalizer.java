package chylex.hee.system.util;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class Unfinalizer{
	private static Field modifiersField;
	
	public static void unfinalizeField(Field field) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		try{
			if (modifiersField == null){
				modifiersField = Field.class.getDeclaredField("modifiers");
				modifiersField.setAccessible(true);
			}
			
			modifiersField.setInt(field,field.getModifiers() & ~Modifier.FINAL);
		}catch(NoSuchFieldException | IllegalArgumentException | IllegalAccessException e){
			throw e;
		}
	}
	
	private Unfinalizer(){}
}

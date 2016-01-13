package chylex.hee.test;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UnitTest{
	RunTime runTime() default RunTime.LOADCOMPLETE;
	String trigger() default "";
	
	public enum RunTime{
		CONSTRUCT, PREINIT, LOADCOMPLETE, INGAME
	}
}

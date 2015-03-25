package chylex.hee.system.test;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.system.logging.Log;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

public final class UnitTester{
	private static final Multimap<RunTime,Method> registryPrep = HashMultimap.create();
	private static final Multimap<RunTime,Method> registryTests = HashMultimap.create();
	
	public static void load(){
		String[] data = System.getProperty("hee").split(",");
		if (!ArrayUtils.contains(data,"unit"))return;
		
		Log.debug("Loading unit tests!");
		
		try{
			for(ClassInfo clsInfo:ClassPath.from(UnitTester.class.getClassLoader()).getTopLevelClassesRecursive("chylex.hee.system.test.list")){
				Class<?> cls = clsInfo.load();
				Constructor<?> constr = null;
				
				try{
					constr = cls.getConstructor();
				}catch(Exception e){}
				
				if (constr == null){
					Log.error("Error registering unit test class $0, a no-arg constructor is required!",cls.getSimpleName());
					continue;
				}
					
				for(Method method:cls.getMethods()){
					UnitTest test = method.getAnnotation(UnitTest.class);
					
					if (test != null){
						if ((method.getModifiers()&Modifier.STATIC) == Modifier.STATIC){
							Log.error("Error registering unit test method $0.$1, the test methods cannot be static!",cls.getSimpleName(),method.getName());
							continue;
						}
						
						if (test.runTime() != RunTime.INGAME && !test.trigger().isEmpty()){
							Log.error("Error registering unit test method $0.$1, cannot use $2 run time with a trigger!",cls.getSimpleName(),method.getName(),test.runTime());
							continue;
						}
						
						if (test.type() == MethodType.PREPARATION)registryPrep.put(test.runTime(),method);
						else registryTests.put(test.runTime(),method);
						
						Log.debug("Registered unit test method $0.$1",cls.getSimpleName(),method.getName());
					}
				}
			}
		}catch(IOException e){
			Log.throwable(e,"Error loading unit tests!");
		}
	}
	
	public static void trigger(RunTime time){
		trigger(time,"");
	}
	
	public static void trigger(RunTime time, String trigger){
		List<Method> methods = new ArrayList<>();
		int tests = 0;
		
		for(Method prep:registryPrep.get(time)){
			if (trigger.equals(prep.getAnnotation(UnitTest.class).trigger()))methods.add(prep);
		}
		
		for(Method test:registryTests.get(time)){
			if (trigger.equals(test.getAnnotation(UnitTest.class).trigger())){
				methods.add(test);
				++tests;
			}
		}
		
		Log.debug("Running $0 unit test(s)...",tests);
		
		Map<Class<?>,Object> objects = new HashMap<>();
		int succeeded = 0, failed = 0;
		
		for(Method method:methods){
			try{
				Class<?> cls = method.getDeclaringClass();
				Object obj = objects.get(cls);
				if (obj == null)objects.put(cls,obj = cls.newInstance());
				
				try{
					method.invoke(obj);
					++succeeded;
				}catch(Throwable t){
					Log.throwable(t,"Unit test failed.");
					++failed;
				}
			}catch(Exception e){
				Log.throwable(e,"Error running a unit test!");
			}
		}
		
		Log.debug("Finished unit tests: $0 succeeded, $1 failed.",succeeded,failed);
	}
	
	private UnitTester(){}
}

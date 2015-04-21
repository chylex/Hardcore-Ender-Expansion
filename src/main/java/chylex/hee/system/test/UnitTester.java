package chylex.hee.system.test;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraftforge.common.ForgeVersion;
import chylex.hee.system.logging.Log;
import chylex.hee.system.test.data.MethodType;
import chylex.hee.system.test.data.RunTime;
import chylex.hee.system.test.data.UnitTest;
import chylex.hee.system.util.DragonUtil;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import cpw.mods.fml.common.FMLCommonHandler;

public final class UnitTester{
	private static final String prefix = "[HEE-UNIT] ";
	private static final Multimap<RunTime,Method> registryPrep = HashMultimap.create();
	private static final Multimap<RunTime,Method> registryTests = HashMultimap.create();
	
	private static boolean isLoaded;
	private static int menuDisplayOk, menuDisplayFailed;
	
	public static void load(){
		if (!DragonUtil.checkSystemProperty("unit"))return;
		
		Log.debug(prefix+"Loading unit tests!");
		
		try{
			String basePackage = "chylex.hee.system.test.list";
			
			for(ClassInfo clsInfo:ClassPath.from(UnitTester.class.getClassLoader()).getTopLevelClassesRecursive(basePackage)){
				Class<?> cls = clsInfo.load();
				Constructor<?> constr = null;
				
				String clsName = clsInfo.getName().substring(basePackage.length()+1).replace('.','/');
				
				try{
					constr = cls.getConstructor();
				}catch(Exception e){}
				
				if (constr == null){
					Log.error(prefix+"Error registering unit test class $0, a no-arg constructor is required!",clsName);
					continue;
				}
					
				for(Method method:cls.getMethods()){
					UnitTest test = method.getAnnotation(UnitTest.class);
					
					if (test != null){
						if ((method.getModifiers()&Modifier.STATIC) == Modifier.STATIC){
							Log.error(prefix+"Error registering unit test method $0.$1, the test methods cannot be static!",clsName,method.getName());
							continue;
						}
						
						if (test.runTime() != RunTime.INGAME && !test.trigger().isEmpty()){
							Log.error(prefix+"Error registering unit test method $0.$1, cannot use $2 run time with a trigger!",clsName,method.getName(),test.runTime());
							continue;
						}
						
						if (test.type() == MethodType.PREPARATION)registryPrep.put(test.runTime(),method);
						else registryTests.put(test.runTime(),method);
						
						Log.debug(prefix+"Registered a $2 method: $0.$1",clsName,method.getName(),test.type() == MethodType.PREPARATION ? "prep" : "test");
					}
				}
			}

			isLoaded = true;
		}catch(IOException e){
			Log.throwable(e,prefix+"Error loading unit tests!");
		}
	}
	
	public static void trigger(RunTime time){
		trigger(time,"");
	}
	
	public static void trigger(RunTime time, String trigger){
		if (!isLoaded)return;
		
		Set<Method> prepList = new HashSet<>(), testList = new HashSet<>();
		
		for(Method prep:registryPrep.get(time)){
			if (trigger.equals(prep.getAnnotation(UnitTest.class).trigger()))prepList.add(prep);
		}
		
		for(Method test:registryTests.get(time)){
			if (trigger.equals(test.getAnnotation(UnitTest.class).trigger()))testList.add(test);
		}
		
		if (prepList.isEmpty() && testList.isEmpty())return;
		
		Log.debug(prefix+"Running $0 prep method(s) and $1 unit test(s)...",prepList.size(),testList.size());
		
		Map<Class<?>,Object> objects = new HashMap<>();
		int succeeded = 0, failed = 0;
		
		for(Method method:Iterables.concat(prepList,testList)){
			try{
				Class<?> cls = method.getDeclaringClass();
				Object obj = objects.get(cls);
				if (obj == null)objects.put(cls,obj = cls.newInstance());
				
				boolean isTest = testList.contains(method);
				
				try{
					method.invoke(obj);
					
					if (isTest){
						++succeeded;
						++menuDisplayOk;
					}
				}catch(InvocationTargetException e){
					if (!isTest)Log.throwable(e.getCause(),prefix+"Failed preparing a test!");
					else{
						Throwable cause = e.getCause();
						Log.error(prefix+"Unit test ($0.java:$2)~$1 failed: $3",cls.getSimpleName(),method.getName(),cause.getStackTrace()[1].getLineNumber(),cause.getMessage());
						++failed;
						++menuDisplayFailed;
					}
				}
			}catch(Exception e){
				Log.throwable(e,prefix+"Error running a unit test!");
			}
		}
		
		Log.debug(prefix+"Finished unit tests: $0 succeeded, $1 failed.",succeeded,failed);
	}
	
	public static void finalizeEventTests() throws Throwable{
		if (!isLoaded)return;
		
		Builder<String> unitDataList = ImmutableList.builder();
		unitDataList.add("[Hardcore Ender Expansion - test mode]");
		unitDataList.add("Event tests: "+menuDisplayOk+" succeeded, "+menuDisplayFailed+" failed");
		
		Field field = FMLCommonHandler.class.getDeclaredField("brandings");
		field.setAccessible(true);
		field.set(FMLCommonHandler.instance(),unitDataList.build());
		
		field = ForgeVersion.class.getDeclaredField("status");
		field.setAccessible(true);
		field.set(null,ForgeVersion.Status.UP_TO_DATE);
	}
	
	private UnitTester(){}
}

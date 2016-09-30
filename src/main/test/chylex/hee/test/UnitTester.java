package chylex.hee.test;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraftforge.common.ForgeVersion;
import chylex.hee.system.logging.Log;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.test.UnitTest.RunTime;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Multimap;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import cpw.mods.fml.common.FMLCommonHandler;

public final class UnitTester{
	private static final String prefix = "[UNIT] ";
	private static final Multimap<RunTime, Method> registryTests = HashMultimap.create();
	
	private static boolean isLoaded, ingameTestsOnly;
	private static int menuDisplayOk, menuDisplayFailed;
	
	public static void load(){
		if (!DragonUtil.checkSystemProperty("unit") && !(ingameTestsOnly = DragonUtil.checkSystemProperty("unitgame")))return;
		
		Log.debug(prefix+"Loading unit tests!");
		
		try{
			String basePackage = "chylex.hee.test.list";
			
			for(ClassInfo clsInfo:ClassPath.from(UnitTester.class.getClassLoader()).getTopLevelClassesRecursive(basePackage)){
				Class<?> cls = clsInfo.load();
				String clsName = clsInfo.getName().substring(basePackage.length()+1).replace('.', '/');
				
				try{
					cls.getConstructor();
				}catch(Exception e){
					Log.error(prefix+"Error registering unit test class $0, a no-arg constructor is required!", clsName);
					continue;
				}
					
				for(Method method:cls.getMethods()){
					UnitTest test = method.getAnnotation(UnitTest.class);
					
					if (test != null){
						if ((method.getModifiers()&Modifier.STATIC) == Modifier.STATIC){
							Log.error(prefix+"Error registering unit test method $0.$1, the test methods cannot be static!", clsName, method.getName());
							continue;
						}
						
						if (test.runTime() != RunTime.INGAME && !test.trigger().isEmpty()){
							Log.error(prefix+"Error registering unit test method $0.$1, cannot use $2 run time with a trigger!", clsName, method.getName(), test.runTime());
							continue;
						}
						
						registryTests.put(test.runTime(), method);
						
						Log.debug(prefix+"Registered a test method: $0.$1", clsName, method.getName());
					}
				}
			}

			isLoaded = true;
		}catch(IOException e){
			Log.throwable(e, prefix+"Error loading unit tests!");
		}
	}
	
	public static void trigger(RunTime time){
		trigger(time, "");
	}
	
	public static void trigger(RunTime time, String trigger){
		if (!isLoaded || (ingameTestsOnly && time != RunTime.INGAME))return;
		
		Set<Method> tests = registryTests.get(time).stream().filter(test -> trigger.equals(test.getAnnotation(UnitTest.class).trigger())).collect(Collectors.toSet());
		if (tests.isEmpty())return;
		
		Log.debug(prefix+"Running $0 unit test(s)...", tests.size());
		
		Map<Class<?>, Object> objects = new HashMap<>();
		int[] data = new int[]{ 0, 0 };
		
		Assert.setSuccessCallback(() -> {
			++menuDisplayOk;
			++data[0];
		});
		
		for(Method method:tests){
			try{
				final Class<?> cls = method.getDeclaringClass();
				Object obj = objects.get(cls);
				if (obj == null)objects.put(cls, obj = cls.newInstance());
				
				Assert.setFailCallback(ex -> {
					String methodName = method.getName();
					int line = Arrays.stream(ex.getStackTrace()).filter(ele -> ele.getMethodName().equals(methodName)).findFirst().map(ele -> ele.getLineNumber()).orElse(0);
					
					Log.error(prefix+"Unit test ($0.java:$2)~$1 failed: $3", cls.getSimpleName(), methodName, line, ex.getMessage());
					++menuDisplayFailed;
					++data[1];
				});
				
				try{
					method.invoke(obj);
				}catch(Exception e){
					Log.throwable(e, prefix+"Error running a unit test!");
					++menuDisplayFailed;
					++data[1];
				}
			}catch(Exception e){
				Log.throwable(e, prefix+"Error running a unit test!");
			}
		}
		
		if (time == RunTime.INGAME)Log.reportedDebug(prefix+"Finished unit tests: $0 succeeded, $1 failed.", data[0], data[1]);
		else Log.debug(prefix+"Finished unit tests: $0 succeeded, $1 failed.", data[0], data[1]);
	}
	
	public static void finalizeEventTests() throws Throwable{
		if (!isLoaded)return;
		
		Builder<String> unitDataList = ImmutableList.builder();
		unitDataList.add("[Hardcore Ender Expansion - test mode]");
		
		if (ingameTestsOnly)unitDataList.add("Event tests: none");
		else unitDataList.add("Event tests: "+menuDisplayOk+" succeeded, "+menuDisplayFailed+" failed");
		
		Field field = FMLCommonHandler.class.getDeclaredField("brandings");
		field.setAccessible(true);
		field.set(FMLCommonHandler.instance(), unitDataList.build());
		
		field = FMLCommonHandler.class.getDeclaredField("brandingsNoMC");
		field.setAccessible(true);
		field.set(FMLCommonHandler.instance(), unitDataList.build());
		
		field = ForgeVersion.class.getDeclaredField("status");
		field.setAccessible(true);
		field.set(null, ForgeVersion.Status.UP_TO_DATE);
	}
	
	private UnitTester(){}
}

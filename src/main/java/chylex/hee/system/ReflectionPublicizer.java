package chylex.hee.system;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import chylex.hee.system.logging.Log;
import chylex.hee.system.logging.Stopwatch;

public final class ReflectionPublicizer{	
	public static Method entityLivingBaseGetExperiencePoints;

	public static void load(){
		Stopwatch.time("ReflectionPublicizer");
		
		for(Method method:EntityLivingBase.class.getDeclaredMethods()){
			if ((method.getModifiers()&Modifier.PROTECTED) == Modifier.PROTECTED && method.getReturnType() == Integer.TYPE){
				Class<?>[] params = method.getParameterTypes();
				if (params.length == 1 && EntityPlayer.class.isAssignableFrom(params[0])){
					method.setAccessible(true);
					entityLivingBaseGetExperiencePoints = method;
					Log.debug("ReflectionPublicizer - entityLivingBaseGetExperiencePoints: $0",method.getName());
					break;
				}
			}
		}

		Stopwatch.finish("ReflectionPublicizer");
	}
	
	public static Object invoke(Method method, Object o, Object...params){
		try{
			return method.invoke(o,params);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
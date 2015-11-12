package chylex.hee.system;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import joptsimple.internal.Objects;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import chylex.hee.system.logging.Log;
import chylex.hee.system.logging.Stopwatch;

public final class ReflectionPublicizer{
	private static Field entityEndermanCarriable;
	private static Method entityLivingBaseGetExperiencePoints;

	public static void load(){
		Stopwatch.time("ReflectionPublicizer");
		
		try{
			entityEndermanCarriable = EntityEnderman.class.getDeclaredField("carriable");
			entityEndermanCarriable.setAccessible(true);
		}catch(Exception e){
			e.printStackTrace();
		}
		
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

		Objects.ensureNotNull(entityEndermanCarriable);
		Objects.ensureNotNull(entityLivingBaseGetExperiencePoints);

		Stopwatch.finish("ReflectionPublicizer");
	}
	
	public static IdentityHashMap<Block,Boolean> f__carriable__EntityEnderman(){
		return (IdentityHashMap<Block,Boolean>)get(entityEndermanCarriable,null);
	}
	
	public static void f__carriable__EntityEnderman(IdentityHashMap<Block,Boolean> newValue){
		set(entityEndermanCarriable,null,newValue);
	}
	
	public static int m__getExperiencePoints__EntityLivingBase(EntityLivingBase entity, EntityPlayer attackingPlayer){
		return (int)invoke(entityLivingBaseGetExperiencePoints,entity,attackingPlayer);
	}
	
	private static Object get(Field field, Object o){
		try{
			return field.get(o);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	private static void set(Field field, Object o, Object newValue){
		try{
			field.set(o,newValue);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private static Object invoke(Method method, Object o, Object...params){
		try{
			return method.invoke(o,params);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
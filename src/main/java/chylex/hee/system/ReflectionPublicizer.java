package chylex.hee.system;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.IntHashMap;
import net.minecraft.world.gen.ChunkProviderEnd;
import chylex.hee.system.logging.Log;
import chylex.hee.system.logging.Stopwatch;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class ReflectionPublicizer{
	public static Field entityGhastTarget;
	public static Field entityLivingBaseLastDamage;
	public static Field entityFire;
	public static Field netHandlerPlayServerFloatingTicks;
	public static Field[] netHandlerPlayServerLastPos;
	public static Field chunkProviderEndRandom;
	
	public static Field renderGlobalMapSoundPositions;
	public static Field guiContainerXSize;
	public static Field guiContainerYSize;
	
	public static Method entityLivingBaseGetExperiencePoints;

	public static void load(){
		Stopwatch.time("ReflectionPublicizer");

		for(Field field:EntityGhast.class.getDeclaredFields()){
			if (Entity.class.isAssignableFrom(field.getType())){
				field.setAccessible(true);
				entityGhastTarget = field;
				Log.debug("ReflectionPublicizer - entityGhastTarget: $0",field.getName());
				break;
			}
		}

		Field[] fields = Entity.class.getDeclaredFields();
		for(int a = 35; a < fields.length; a++){
			if (Random.class.isAssignableFrom(fields[a].getType())){
				fields[a+3].setAccessible(true);
				entityFire = fields[a+3];
				Log.debug("ReflectionPublicizer - entityFire: $0",fields[a+3].getName());
				break;
			}
		}
		
		fields = EntityLivingBase.class.getDeclaredFields();
		for(int a = 35; a < fields.length-3; a++){
			if (fields[a].getType() == int.class && fields[a+1].getType() == float.class && fields[a+2].getType() == boolean.class){
				fields[a+1].setAccessible(true);
				entityLivingBaseLastDamage = fields[a+1];
				Log.debug("ReflectionPublicizer - entityLivingBaseLastDamage: $0",fields[a+1].getName());
			}
		}

		fields = NetHandlerPlayServer.class.getDeclaredFields();
		for(int a = 1; a < fields.length; a++){
			if (EntityPlayerMP.class.isAssignableFrom(fields[a].getType()) && netHandlerPlayServerFloatingTicks == null){
				fields[a+2].setAccessible(true);
				netHandlerPlayServerFloatingTicks = fields[a+2];
				Log.debug("ReflectionPublicizer - netHandlerPlayServerFloatingTicks: $0",fields[a+2].getName());
			}
			else if (IntHashMap.class.isAssignableFrom(fields[a].getType())){
				netHandlerPlayServerLastPos = new Field[]{
					fields[a+1], fields[a+2], fields[a+3]
				};
				
				for(Field field:netHandlerPlayServerLastPos)field.setAccessible(true);
				Log.debug("ReflectionPublicizer - netHandlerPlayServerLastPos: $0, $1, $2",fields[a+1].getName(),fields[a+2].getName(),fields[a+3].getName());
				break;
			}
		}
		
		for(Field field:ChunkProviderEnd.class.getDeclaredFields()){
			if (Random.class.isAssignableFrom(field.getType())){
				field.setAccessible(true);
				chunkProviderEndRandom = field;
				break;
			}
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

		Stopwatch.finish("ReflectionPublicizer");
	}
	
	@SideOnly(Side.CLIENT)
	public static void loadClient(){
		Stopwatch.time("ReflectionPublicizerClient");
		
		Field[] fields = RenderGlobal.class.getDeclaredFields();
		for(int a = 28; a < fields.length; a++){
			if (Map.class.isAssignableFrom(fields[a].getType())){
				fields[a+1].setAccessible(true);
				renderGlobalMapSoundPositions = fields[a+1];
				Log.debug("ReflectionPublicizer/client - renderGlobalMapSoundPositions: $0",fields[a+1].getName());
				break;
			}
		}
		
		fields = GuiContainer.class.getDeclaredFields();
		for(int a = 0; a < fields.length-2; a++){
			if (fields[a].getType() == int.class && fields[a+1].getType() == int.class){
				fields[a].setAccessible(true);
				fields[a+1].setAccessible(true);
				guiContainerXSize = fields[a];
				guiContainerYSize = fields[a+1];
				Log.debug("ReflectionPublicizer/client - guiContainerXYSize: $0, $1",fields[a].getName(),fields[a+1].getName());
				break;
			}
		}

		Stopwatch.finish("ReflectionPublicizerClient");
	}

	public static Object get(Field field, Object o){
		try{
			return field.get(o);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public static void set(Field field, Object o, Object newValue){
		try{
			field.set(o,newValue);
		}catch(Exception e){
			e.printStackTrace();
		}
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
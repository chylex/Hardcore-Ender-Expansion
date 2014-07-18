package chylex.hee.system;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Random;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.util.IntHashMap;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.TimeMeasurement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class ReflectionPublicizer{
	public static Field entityGhastTarget;
	public static Field entityLivingExperienceValue;
	public static Field entityLivingBaseRecentlyHit;
	public static Field entityFire;
	public static Field netHandlerPlayServerFloatingTicks;
	public static Field[] netHandlerPlayServerLastPos;
	public static Field renderGlobalMapSoundPositions;

	public static void load(){
		TimeMeasurement.start("ReflectionPublicizer");

		for(Field field:EntityGhast.class.getDeclaredFields()){
			if (Entity.class.isAssignableFrom(field.getType())){
				field.setAccessible(true);
				entityGhastTarget = field;
				DragonUtil.info("ReflectionPublicizer - entityGhastTarget: "+field.getName());
				break;
			}
		}

		for(Field field:EntityLiving.class.getDeclaredFields()){
			if ((field.getModifiers()&Modifier.PROTECTED) == Modifier.PROTECTED && field.getType().isPrimitive()){
				field.setAccessible(true);
				entityLivingExperienceValue = field;
				DragonUtil.info("ReflectionPublicizer - entityLivingExperienceValue: "+field.getName());
				break;
			}
		}

		Field[] fields = EntityLivingBase.class.getDeclaredFields();
		for(int a = 28; a < fields.length; a++){
			if (EntityPlayer.class.isAssignableFrom(fields[a].getType())){
				fields[a+1].setAccessible(true);
				entityLivingBaseRecentlyHit = fields[a+1];
				DragonUtil.info("ReflectionPublicizer - entityLivingBaseRecentlyHit: "+fields[a+1].getName());
				break;
			}
		}

		fields = Entity.class.getDeclaredFields();
		for(int a = 35; a < fields.length; a++){
			if (Random.class.isAssignableFrom(fields[a].getType())){
				fields[a+3].setAccessible(true);
				entityFire = fields[a+3];
				DragonUtil.info("ReflectionPublicizer - entityFire: "+fields[a+3].getName());
				break;
			}
		}

		fields = NetHandlerPlayServer.class.getDeclaredFields();
		for(int a = 1; a < fields.length; a++){
			if (EntityPlayerMP.class.isAssignableFrom(fields[a].getType()) && netHandlerPlayServerFloatingTicks == null){
				fields[a+2].setAccessible(true);
				netHandlerPlayServerFloatingTicks = fields[a+2];
				DragonUtil.info("ReflectionPublicizer - netHandlerPlayServerFloatingTicks: "+fields[a+2].getName());
			}
			else if (IntHashMap.class.isAssignableFrom(fields[a].getType())){
				netHandlerPlayServerLastPos = new Field[]{
					fields[a+1], fields[a+2], fields[a+3]
				};
				for(Field field:netHandlerPlayServerLastPos)field.setAccessible(true);
				DragonUtil.info("ReflectionPublicizer - netHandlerPlayServerLastPos: "+fields[a+1].getName()+", "+fields[a+2].getName()+", "+fields[a+3].getName());
				break;
			}
		}

		TimeMeasurement.finish("ReflectionPublicizer");
	}
	
	@SideOnly(Side.CLIENT)
	public static void loadClient(){
		Field[] fields = RenderGlobal.class.getDeclaredFields();
		for(int a = 28; a < fields.length; a++){
			if (Map.class.isAssignableFrom(fields[a].getType())){
				fields[a+1].setAccessible(true);
				renderGlobalMapSoundPositions = fields[a+1];
				DragonUtil.info("ReflectionPublicizer/client - renderGlobalMapSoundPositions: "+fields[a+1].getName());
				break;
			}
		}
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
}

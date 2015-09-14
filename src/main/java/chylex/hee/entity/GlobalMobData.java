package chylex.hee.entity;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;

public final class GlobalMobData{
	public static interface IIgnoreEnderGoo{}
	public static interface ITolerateCorruptedEnergy{}
	
	private static final Set<String> enderGooTolerant = new HashSet<>();
	private static final Set<String> corruptedEnergyTolerant = new HashSet<>();
	
	public static boolean setEnderGooTolerant(String mobName){
		return enderGooTolerant.add(mobName);
	}
	
	public static boolean setCorruptedEnergyTolerant(String mobName){
		return corruptedEnergyTolerant.add(mobName);
	}
	
	public static boolean isEnderGooTolerant(EntityLivingBase entity){
		return entity instanceof IIgnoreEnderGoo || enderGooTolerant.contains(EntityList.getEntityString(entity));
	}
	
	public static boolean isCorruptedEnergyTolerant(EntityLivingBase entity){
		return entity instanceof ITolerateCorruptedEnergy || corruptedEnergyTolerant.contains(EntityList.getEntityString(entity));
	}
	
	private GlobalMobData(){}
}

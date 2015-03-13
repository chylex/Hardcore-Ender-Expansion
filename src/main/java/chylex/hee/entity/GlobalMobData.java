package chylex.hee.entity;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;

public final class GlobalMobData{
	public static interface IIgnoreEnderGoo{}
	
	private static final Set<String> enderGooTolerant = new HashSet<>();
	
	public static boolean setEnderGooTolerant(String mobName){
		return enderGooTolerant.add(mobName);
	}
	
	public static boolean isEnderGooTolerant(EntityLivingBase entity){
		return entity instanceof IIgnoreEnderGoo || enderGooTolerant.contains(EntityList.getEntityString(entity));
	}
	
	static{
		setEnderGooTolerant("Silverfish");
	}
	
	private GlobalMobData(){}
}

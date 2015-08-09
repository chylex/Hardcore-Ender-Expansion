package chylex.hee.mechanics.enhancements;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.util.EnumChatFormatting;
import chylex.hee.system.logging.Log;

public final class EnhancementEnumHelper{
	public static String getName(Enum enhancementEnum, EnumChatFormatting color){
		return new StringBuilder().
			append(EnumChatFormatting.RESET).append(color).
			append(enhancementEnum.name().substring(0,1).toUpperCase(Locale.ENGLISH)).
			append(enhancementEnum.name().substring(1).toLowerCase(Locale.ENGLISH).replace('_',' ')).
			toString();
	}
	
	public static String serialize(List<Enum> list){
		StringBuilder build = new StringBuilder();
		for(Enum enhancement:list)build.append(enhancement.name()).append(',');
		return (build.length() > 0 ? build.deleteCharAt(build.length()-1) : build).toString();
	}
	
	public static List<Enum> deserialize(String str, Class<? extends Enum> enumClass){
		List<Enum> types = new ArrayList<>();
		if (str.isEmpty())return types;
		
		for(String s:str.split(",")){
			try{
				types.add(Enum.valueOf(enumClass,s));
			}catch(IllegalArgumentException e){
				Log.error("Unknown enhancement ("+enumClass.getSimpleName()+") entry: "+s+" (saved data: "+str+")");
			}
		}
		
		return types;
	}
	
	private EnhancementEnumHelper(){}
}

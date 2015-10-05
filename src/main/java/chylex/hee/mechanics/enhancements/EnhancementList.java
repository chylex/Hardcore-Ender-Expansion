package chylex.hee.mechanics.enhancements;
import java.util.EnumMap;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.commons.lang3.EnumUtils;
import chylex.hee.system.util.DragonUtil;
import com.google.common.base.Splitter;

public class EnhancementList<T extends Enum<T>>{
	private final Class<T> enumCls;
	private final EnumMap<T,Byte> map;
	
	public EnhancementList(Class<T> enumCls){
		this.enumCls = enumCls;
		this.map = new EnumMap<>(enumCls);
	}
	
	public String serialize(){
		return map.entrySet().stream().map(entry -> entry.getKey().name()+":"+entry.getValue()).collect(Collectors.joining(";"));
	}
	
	public void deserialize(String str){
		map.clear();
		
		for(Entry<String,String> entry:Splitter.on(';').withKeyValueSeparator(':').split(str).entrySet()){
			T enh = EnumUtils.getEnum(enumCls,entry.getKey());
			byte lvl = (byte)DragonUtil.tryParse(entry.getValue(),0);
			
			if (enh != null && lvl > 0)map.put(enh,Byte.valueOf(lvl));
		}
	}
}

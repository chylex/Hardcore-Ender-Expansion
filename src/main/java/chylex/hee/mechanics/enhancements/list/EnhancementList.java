package chylex.hee.mechanics.enhancements.list;
import java.util.EnumMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import org.apache.commons.lang3.EnumUtils;
import chylex.hee.mechanics.enhancements.EnhancementRegistry;
import chylex.hee.system.util.DragonUtil;
import com.google.common.base.Splitter;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EnhancementList<T extends Enum<T>>{
	private final Class<T> enumCls;
	private final EnumMap<T,Byte> map;
	
	public EnhancementList(Class<T> enumCls){
		this.enumCls = enumCls;
		this.map = new EnumMap<>(enumCls);
	}
	
	public boolean isEmpty(){
		return map.isEmpty();
	}
	
	public boolean has(T enhancement){
		return map.containsKey(enhancement);
	}
	
	public int get(T enhancement){
		return map.getOrDefault(enhancement,(byte)0);
	}
	
	public void set(T enhancement, int level){
		map.put(enhancement,(byte)level);
	}
	
	public void upgrade(T enhancement){
		map.merge(enhancement,(byte)1,(prev, set) -> (byte)(prev+1));
	}
	
	public void replace(EnhancementList<T> replacement){
		map.clear();
		for(Entry<T,Byte> entry:replacement.map.entrySet())map.put(entry.getKey(),entry.getValue());
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
	
	@SideOnly(Side.CLIENT)
	public void addTooltip(List<String> tooltipList, EnumChatFormatting color){
		if (map.isEmpty())tooltipList.add(EnumChatFormatting.GRAY+"No Enhancements"); // TODO translate
		else{
			for(Entry<T,Byte> entry:map.entrySet()){
				tooltipList.add(color+EnhancementRegistry.getEnhancementName(entry.getKey())+" "+StatCollector.translateToLocal("enchantment.level."+entry.getValue()));
			}
		}
	}
}

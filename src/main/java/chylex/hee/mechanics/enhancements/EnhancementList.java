package chylex.hee.mechanics.enhancements;
import java.util.EnumMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import org.apache.commons.lang3.EnumUtils;
import chylex.hee.system.abstractions.nbt.NBT;
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
		
		for(Entry<String,String> entry:Splitter.on(';').omitEmptyStrings().withKeyValueSeparator(':').split(str).entrySet()){
			T enh = EnumUtils.getEnum(enumCls,entry.getKey());
			byte lvl = (byte)DragonUtil.tryParse(entry.getValue(),0);
			
			if (enh != null && lvl > 0)map.put(enh,Byte.valueOf(lvl));
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void addTooltip(List<String> tooltipList, EnumChatFormatting color){
		if (map.isEmpty())tooltipList.add(EnumChatFormatting.GRAY+I18n.format("enhancements.none"));
		else{
			for(Entry<T,Byte> entry:map.entrySet()){
				tooltipList.add(color+EnhancementRegistry.getEnhancementName(entry.getKey())+" "+StatCollector.translateToLocal("enchantment.level."+entry.getValue()));
			}
		}
	}
	
	public static final class LinkedItemStack<T extends Enum<T>> extends EnhancementList<T>{
		private final ItemStack linkedIS;
		
		public LinkedItemStack(Class<T> enumCls, ItemStack linkedIS){
			super(enumCls);
			this.linkedIS = linkedIS;
			
			String enhancementData = NBT.item(linkedIS,false).getString("enhancements2");
			if (!enhancementData.isEmpty())deserialize(enhancementData);
		}
		
		@Override
		public void set(T enhancement, int level){
			super.set(enhancement,level);
			NBT.item(linkedIS,true).setString("enhancements2",serialize());
			linkedIS.func_150996_a(EnhancementRegistry.getItemTransformation(linkedIS.getItem()));
		}
		
		@Override
		public void upgrade(T enhancement){
			super.upgrade(enhancement);
			NBT.item(linkedIS,true).setString("enhancements2",serialize());
			linkedIS.func_150996_a(EnhancementRegistry.getItemTransformation(linkedIS.getItem()));
		}
		
		@Override
		public void replace(EnhancementList<T> replacement){
			super.replace(replacement);
			NBT.item(linkedIS,true).setString("enhancements2",serialize());
			linkedIS.func_150996_a(EnhancementRegistry.getItemTransformation(linkedIS.getItem()));
		}
	}
}

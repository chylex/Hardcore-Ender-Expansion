package chylex.hee.mechanics.enhancements;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import chylex.hee.mechanics.enhancements.types.SpatialDashGemEnhancements;

public final class EnhancementRegistry{
	private static final Map<Item,EnhancementData> registry = new HashMap<>(16);
	
	public static void init(){
		SpatialDashGemEnhancements.register();
	}
	
	public static <T extends Enum<T>> EnhancementData registerEnhancement(Block[] blocks, Class<T> enumCls){
		EnhancementData data = new EnhancementData(enumCls);
		for(Block block:blocks)registry.put(Item.getItemFromBlock(block),data);
		return data;
	}
	
	public static <T extends Enum<T>> EnhancementData registerEnhancement(Item[] items, Class<T> enumCls){
		EnhancementData data = new EnhancementData(enumCls);
		for(Item item:items)registry.put(item,data);
		return data;
	}
	
	public static boolean canEnhanceBlock(Block block){
		return registry.containsKey(Item.getItemFromBlock(block));
	}
	
	public static boolean canEnhanceItem(Item item){
		return registry.containsKey(item);
	}
	
	public static Item getItemTransformation(Item item){
		return registry.get(item).getTransformationItem(item);
	}
	
	public static Enum[] listEnhancements(Item item){
		return registry.get(item).listEnhancements();
	}
	
	public static String getEnhancementName(Enum enhancementEnum){
		return new StringBuilder().append(enhancementEnum.name().substring(0,1).toUpperCase(Locale.ENGLISH)).append(enhancementEnum.name().substring(1).toLowerCase(Locale.ENGLISH).replace('_',' ')).toString();
	}
	
	private EnhancementRegistry(){}
}

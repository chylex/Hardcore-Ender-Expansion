package chylex.hee.mechanics.enhancements;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import chylex.hee.mechanics.enhancements.list.EnhancementList;
import chylex.hee.mechanics.enhancements.list.ItemStackEnhancementList;
import chylex.hee.mechanics.enhancements.types.BrewingStandEnhancements;
import chylex.hee.mechanics.enhancements.types.EnderPearlEnhancements;
import chylex.hee.mechanics.enhancements.types.EssenceAltarEnhancements;
import chylex.hee.mechanics.enhancements.types.SacredWandEnhancements;
import chylex.hee.mechanics.enhancements.types.SpatialDashGemEnhancements;
import chylex.hee.mechanics.enhancements.types.TNTEnhancements;
import chylex.hee.mechanics.enhancements.types.TransferenceGemEnhancements;

public final class EnhancementRegistry{
	private static final Map<Item,EnhancementData> registry = new HashMap<>(16);
	
	public static void init(){
		BrewingStandEnhancements.register();
		EnderPearlEnhancements.register();
		EssenceAltarEnhancements.register();
		SacredWandEnhancements.register();
		SpatialDashGemEnhancements.register();
		TNTEnhancements.register();
		TransferenceGemEnhancements.register();
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
	
	public static <T extends Enum<T>> EnhancementList<T> getEnhancementList(ItemStack is){
		return new ItemStackEnhancementList<>(registry.get(is.getItem()).getEnhancementClass(),is);
	}
	
	public static String getEnhancementName(Enum enhancementEnum){
		return enhancementEnum.name().substring(0,1).toUpperCase(Locale.ENGLISH)+enhancementEnum.name().substring(1).toLowerCase(Locale.ENGLISH).replace('_',' ');
	}
	
	private EnhancementRegistry(){}
}

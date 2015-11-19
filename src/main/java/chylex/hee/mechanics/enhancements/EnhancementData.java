package chylex.hee.mechanics.enhancements;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import chylex.hee.init.ItemList;
import chylex.hee.system.util.IItemSelector.IRepresentativeItemSelector;
import chylex.hee.system.util.IItemSelector.IRepresentativeItemSelector.SimpleItemSelector;
import com.google.common.collect.ImmutableList;

public class EnhancementData<T extends Enum<T>>{
	private final Class<T> enumCls;
	private final EnumMap<T,EnhancementInfo> infoMap;
	private ImmutableList<EnhancementInfo> infoImmutableList;
	private Item transform;
	
	public EnhancementData(Class<T> enumCls){
		this.enumCls = enumCls;
		this.infoMap = new EnumMap<>(enumCls);
		this.infoImmutableList = ImmutableList.of();
	}
	
	public EnhancementInfo register(T enhancement){
		EnhancementInfo info = new EnhancementInfo(enhancement);
		infoMap.put(enhancement,info);
		infoImmutableList = ImmutableList.copyOf(infoMap.values());
		return info;
	}
	
	public EnhancementInfo getEnhancementInfo(T enhancement){
		return infoMap.get(enhancement);
	}
	
	public void setTransformationItem(Item transform){
		this.transform = transform;
	}
	
	public Item getTransformationItem(Item current){
		return transform == null ? current : transform;
	}
	
	public Class<T> getEnhancementClass(){
		return enumCls;
	}
	
	public ImmutableList<EnhancementInfo> listEnhancementInfo(){
		return infoImmutableList;
	}
	
	public class EnhancementInfo{
		private final T enhancement;
		private final List<EnhancementIngredient> ingredients = new ArrayList<>(3);
		private int maxLevel = 1;
		
		EnhancementInfo(T enhancement){
			this.enhancement = enhancement;
		}
		
		public T getEnhancement(){
			return enhancement;
		}
		
		public EnhancementInfo addPowder(int baseAmount, IIngredientAmount amountFunc){
			ingredients.add(new EnhancementIngredient(new SimpleItemSelector(ItemList.end_powder),baseAmount,amountFunc,1));
			return this;
		}
		
		public EnhancementInfo addIngredient(Block block, int baseAmount, IIngredientAmount amountFunc){
			return addIngredient(new SimpleItemSelector(block),baseAmount,amountFunc,1);
		}
		
		public EnhancementInfo addIngredient(Block block, int baseAmount, IIngredientAmount amountFunc, int minLevel){
			return addIngredient(new SimpleItemSelector(block),baseAmount,amountFunc,minLevel);
		}
		
		public EnhancementInfo addIngredient(Item item, int baseAmount, IIngredientAmount amountFunc){
			return addIngredient(new SimpleItemSelector(item),baseAmount,amountFunc,1);
		}
		
		public EnhancementInfo addIngredient(Item item, int baseAmount, IIngredientAmount amountFunc, int minLevel){
			return addIngredient(new SimpleItemSelector(item),baseAmount,amountFunc,minLevel);
		}
		
		public EnhancementInfo addIngredient(IRepresentativeItemSelector selector, int baseAmount, IIngredientAmount amountFunc, int minLevel){
			ingredients.add(new EnhancementIngredient(selector,baseAmount,amountFunc,minLevel));
			return this;
		}
		
		public List<EnhancementIngredient> getIngredients(int level, int stackSize){
			return level <= maxLevel ? ingredients.stream().filter(ingredient -> ingredient.getAmount(level,stackSize) > 0).collect(Collectors.toList()) : new ArrayList<>(0);
		}
		
		public EnhancementInfo setMaxLevel(int maxLevel){
			this.maxLevel = maxLevel;
			return this;
		}
		
		public int getMaxLevel(){
			return maxLevel;
		}
		
		public String getName(){
			return EnhancementRegistry.getEnhancementName(enhancement);
		}
	}
}

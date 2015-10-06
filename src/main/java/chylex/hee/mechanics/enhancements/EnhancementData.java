package chylex.hee.mechanics.enhancements;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import chylex.hee.init.ItemList;

public class EnhancementData<T extends Enum<T>>{
	private final Class<T> enumCls;
	private final EnumMap<T,EnhancementInfo> infoMap;
	private Item transform;
	
	public EnhancementData(Class<T> enumCls){
		this.enumCls = enumCls;
		this.infoMap = new EnumMap<>(enumCls);
	}
	
	public EnhancementInfo register(T enhancement){
		EnhancementInfo info = new EnhancementInfo();
		infoMap.put(enhancement,info);
		return info;
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
	
	public T[] listEnhancements(){
		return enumCls.getEnumConstants();
	}
	
	public class EnhancementInfo{
		private final List<EnhancementIngredient> ingredients = new ArrayList<>(3);
		private byte maxLevel = 1;
		
		EnhancementInfo(){}
		
		public EnhancementInfo addPowder(int baseAmount, IIngredientAmount amountFunc){
			ingredients.add(new EnhancementIngredient(ItemList.end_powder,baseAmount,amountFunc,1));
			return this;
		}
		
		public EnhancementInfo addIngredient(Block block, int baseAmount, IIngredientAmount amountFunc){
			ingredients.add(new EnhancementIngredient(Item.getItemFromBlock(block),baseAmount,amountFunc,1));
			return this;
		}
		
		public EnhancementInfo addIngredient(Block block, int baseAmount, IIngredientAmount amountFunc, int minLevel){
			ingredients.add(new EnhancementIngredient(Item.getItemFromBlock(block),baseAmount,amountFunc,minLevel));
			return this;
		}
		
		public EnhancementInfo addIngredient(Item item, int baseAmount, IIngredientAmount amountFunc){
			ingredients.add(new EnhancementIngredient(item,baseAmount,amountFunc,1));
			return this;
		}
		
		public EnhancementInfo addIngredient(Item item, int baseAmount, IIngredientAmount amountFunc, int minLevel){
			ingredients.add(new EnhancementIngredient(item,baseAmount,amountFunc,minLevel));
			return this;
		}
		
		public EnhancementInfo setMaxLevel(int maxLevel){
			this.maxLevel = (byte)maxLevel;
			return this;
		}
		
		public byte getMaxLevel(){
			return maxLevel;
		}
	}
}

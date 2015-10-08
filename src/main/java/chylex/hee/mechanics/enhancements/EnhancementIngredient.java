package chylex.hee.mechanics.enhancements;
import chylex.hee.system.util.IItemSelector.IRepresentativeItemSelector;
import chylex.hee.system.util.MathUtil;

public class EnhancementIngredient{
	public final IRepresentativeItemSelector selector;
	private final byte baseAmount;
	private final IIngredientAmount amountFunc;
	private final byte minLevel;
	
	EnhancementIngredient(IRepresentativeItemSelector selector, int baseAmount, IIngredientAmount amountFunc, int minLevel){
		this.selector = selector;
		this.baseAmount = (byte)baseAmount;
		this.amountFunc = amountFunc;
		this.minLevel = (byte)minLevel;
	}
	
	public int getAmount(int level, int stackSize){
		return MathUtil.ceil(getAmountFloat(level)*Math.pow(stackSize,0.9D));
	}
	
	private float getAmountFloat(int level){
		return level < minLevel ? 0F : level == minLevel ? baseAmount : amountFunc.process(getAmountFloat(level-1));
	}
}

package chylex.hee.mechanics.enhancements;
import net.minecraft.item.Item;
import chylex.hee.system.util.MathUtil;

public class EnhancementIngredient{
	public final Item item;
	private final byte baseAmount;
	private final IIngredientAmount amountFunc;
	private final byte minLevel;
	
	EnhancementIngredient(Item item, int baseAmount, IIngredientAmount amountFunc, int minLevel){
		this.item = item;
		this.baseAmount = (byte)baseAmount;
		this.amountFunc = amountFunc;
		this.minLevel = (byte)minLevel;
	}
	
	public int getAmount(int level){
		return MathUtil.ceil(getAmountFloat(level));
	}
	
	private float getAmountFloat(int level){
		return level < minLevel ? 0F : level == minLevel ? baseAmount : amountFunc.process(getAmount(level-1));
	}
}

package chylex.hee.world.end;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.collections.EmptyEnumSet;
import chylex.hee.system.collections.weight.WeightedMap;
import chylex.hee.world.end.tick.ITerritoryBehavior;
import chylex.hee.world.util.IRangeGenerator;
import chylex.hee.world.util.IRangeGenerator.RangeGenerator;
import chylex.hee.world.util.RandomAmount;

public abstract class TerritoryProperties<T extends Enum<T>>{
	public static final TerritoryProperties defaultProperties = new TerritoryProperties(){
		@Override public void setupBehaviorList(List list, EndTerritory territory, EnumSet variations, Pos centerPos, boolean isRare){}
	};
	
	private final @Nullable Class<T> variationClass;
	private final WeightedMap<T> variationsCommon = new WeightedMap<>(4);
	private final WeightedMap<T> variationsRare = new WeightedMap<>(4);
	
	private @Nullable IRangeGenerator variationAmountCommon;
	private @Nullable IRangeGenerator variationAmountRare;
	
	private TerritoryProperties(){
		this.variationClass = null;
	}
	
	public TerritoryProperties(Class<T> variationClass){
		this.variationClass = variationClass;
	}
	
	// SETUP
	
	protected final void addCommonVariation(T variation, int weight){
		this.variationsCommon.add(variation,weight);
	}
	
	protected final void addRareVariation(T variation, int weight){
		this.variationsRare.add(variation,weight);
	}
	
	protected final void setCommonAmount(IRangeGenerator rangeGenerator){
		if (variationsCommon.isEmpty())throw new IllegalStateException("Cannot set the amount generator with 0 variations!");
		this.variationAmountCommon = rangeGenerator;
	}
	
	protected final void setCommonAmount(RandomAmount amountAlgo){
		setCommonAmount(new RangeGenerator(0,variationsCommon.size(),amountAlgo));
	}
	
	protected final void setCommonAmount(RandomAmount amountAlgo, int max){
		setCommonAmount(new RangeGenerator(0,max,amountAlgo));
	}
	
	protected final void setRareAmount(IRangeGenerator rangeGenerator){
		if (variationsRare.isEmpty())throw new IllegalStateException("Cannot set the amount generator with 0 variations!");
		this.variationAmountRare = rangeGenerator;
	}
	
	protected final void setRareAmount(RandomAmount amountAlgo){
		setRareAmount(new RangeGenerator(0,variationsRare.size(),amountAlgo));
	}
	
	protected final void setRareAmount(RandomAmount amountAlgo, int max){
		setRareAmount(new RangeGenerator(0,max,amountAlgo));
	}
	
	// HANDLING
	
	public abstract void setupBehaviorList(List<ITerritoryBehavior> list, EndTerritory territory, EnumSet<T> variations, Pos centerPos, boolean isRare);
	
	public int generateVariationsSerialized(Random rand, boolean isRare){
		if (variationClass == null || (isRare && variationAmountRare == null) || (!isRare && variationAmountCommon == null))return 0;
		
		final WeightedMap<T> sourceMap = new WeightedMap<>(isRare ? variationsRare : variationsCommon);
		final int amount = isRare ? variationAmountRare.next(rand) : variationAmountCommon.next(rand);
		
		int bits = 0;
		
		for(int a = 0; a < amount; a++){
			T ele = sourceMap.removeRandomItem(rand);
			if (ele != null)bits |= 1<<ele.ordinal();
		}
		
		return bits;
	}
	
	public final int serialize(Collection<T> collection){
		int bits = 0;
		for(T ele:collection)bits |= 1<<ele.ordinal();
		return bits;
	}
	
	public final EnumSet<T> deserialize(int bits){
		if (variationClass == null)return EmptyEnumSet.get();
		
		final T[] elements = variationClass.getEnumConstants();
		EnumSet<T> set = EnumSet.noneOf(variationClass);
		
		for(int bit = 0; bit < elements.length; bit++){
			if ((bits&(1<<bit)) != 0)set.add(elements[bit]);
		}
		
		return set;
	}
}

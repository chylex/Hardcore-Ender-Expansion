package chylex.hee.world.feature.blobs;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.tuple.Pair;
import chylex.hee.system.weight.ObjectWeightPair;
import chylex.hee.system.weight.WeightedList;

public final class BlobPattern{
	private final WeightedList<ObjectWeightPair<BlobGenerator>> generators = new WeightedList<>();
	private final WeightedList<ObjectWeightPair<BlobPopulator>> populators = new WeightedList<>();
	private IPopulatorAmountProvider populatorAmountProvider;
	
	public BlobPattern(){}
	
	public BlobPattern addGenerator(BlobGenerator generator, int weight){
		generators.add(ObjectWeightPair.of(generator,weight));
		return this;
	}
	
	public BlobPattern addPopulator(BlobPopulator populator, int weight){
		populators.add(ObjectWeightPair.of(populator,weight));
		return this;
	}
	
	public BlobPattern setPopulatorAmountProvider(IPopulatorAmountProvider provider){
		this.populatorAmountProvider = provider;
		return this;
	}
	
	public Pair<BlobGenerator,List<BlobPopulator>> generatePattern(Random rand){
		List<BlobPopulator> chosenPopulators = new ArrayList<>();
		
		for(int a = 0, amount = populatorAmountProvider.getPopulatorAmount(rand,populators.size()); a < amount; a++){
			chosenPopulators.add(populators.getRandomItem(rand).getObject());
		}
		
		return Pair.of(generators.getRandomItem(rand).getObject(),chosenPopulators);
	}
	
	public static interface IPopulatorAmountProvider{
		int getPopulatorAmount(Random rand, int totalAmount);
	}
}

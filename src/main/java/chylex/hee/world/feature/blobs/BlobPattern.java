package chylex.hee.world.feature.blobs;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.tuple.Pair;
import chylex.hee.system.weight.IWeightProvider;
import chylex.hee.system.weight.WeightedList;
import chylex.hee.world.util.IRandomAmount;

public final class BlobPattern implements IWeightProvider{
	private final int weight;
	private final WeightedList<BlobGenerator> generators = new WeightedList<>();
	private final WeightedList<BlobPopulator> populators = new WeightedList<>();
	private IRandomAmount populatorAmount;
	
	public BlobPattern(int weight){
		this.weight = weight;
	}
	
	public BlobPattern addGenerators(BlobGenerator[] generator){
		generators.addAll(generator);
		return this;
	}
	
	public BlobPattern addPopulators(BlobPopulator[] populator){
		populators.addAll(populator);
		return this;
	}
	
	public BlobPattern setPopulatorAmountProvider(IRandomAmount populatorAmount){
		this.populatorAmount = populatorAmount;
		return this;
	}
	
	public Pair<BlobGenerator,List<BlobPopulator>> generatePattern(Random rand){
		List<BlobPopulator> chosenPopulators = new ArrayList<>();
		
		if (populatorAmount != null && !populators.isEmpty()){
			for(int a = 0, amount = populatorAmount.generate(rand,1,populators.size()); a < amount; a++){
				chosenPopulators.add(populators.getRandomItem(rand));
			}
		}
		
		return Pair.of(generators.getRandomItem(rand),chosenPopulators);
	}
	
	@Override
	public int getWeight(){
		return weight;
	}
}

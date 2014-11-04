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
	private IRandomAmount populatorAmountGen;
	private int minPopulatorAmount, maxPopulatorAmount;
	
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
	
	public BlobPattern setPopulatorAmountProvider(IRandomAmount populatorAmountGen, int minPopulatorAmount, int maxPopulatorAmount){
		this.populatorAmountGen = populatorAmountGen;
		this.minPopulatorAmount = minPopulatorAmount;
		this.maxPopulatorAmount = maxPopulatorAmount;
		return this;
	}
	
	public Pair<BlobGenerator,List<BlobPopulator>> generatePattern(Random rand){
		List<BlobPopulator> chosenPopulators = new ArrayList<>();
		
		if (populatorAmountGen != null && !populators.isEmpty()){
			for(int a = 0, amount = populatorAmountGen.generate(rand,minPopulatorAmount,maxPopulatorAmount); a < amount; a++){
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

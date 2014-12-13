package chylex.hee.world.feature.blobs;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.tuple.Pair;
import chylex.hee.system.collections.WeightedList;
import chylex.hee.system.collections.weight.IWeightProvider;
import chylex.hee.world.util.IRandomAmount;

public final class BlobPattern implements IWeightProvider{
	private final int weight;
	private final WeightedList<BlobGenerator> generators = new WeightedList<>();
	private final WeightedList<BlobPopulator> populators = new WeightedList<>();
	private IRandomAmount populatorAmountGen;
	private int minPopulatorAmount, maxPopulatorAmount;
	
	private final Comparator<BlobPopulator> populatorSorter = new Comparator<BlobPopulator>(){
		@Override
		public int compare(BlobPopulator pop1, BlobPopulator pop2){
			return populators.indexOf(pop1) < populators.indexOf(pop2) ? -1 : 1; // never equal
		}
	};
	
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
			WeightedList<BlobPopulator> blobPopulators = new WeightedList<>();
			blobPopulators.addAll(populators);
			
			for(int a = 0, amount = populatorAmountGen.generate(rand,minPopulatorAmount,maxPopulatorAmount); a < amount && !blobPopulators.isEmpty(); a++){
				BlobPopulator populator = blobPopulators.getRandomItem(rand);
				chosenPopulators.add(populator);
				blobPopulators.remove(populator);
			}
			
			Collections.sort(chosenPopulators,populatorSorter);
		}
		
		return Pair.of(generators.getRandomItem(rand),chosenPopulators);
	}
	
	@Override
	public int getWeight(){
		return weight;
	}
}

package chylex.hee.world.feature.blobs;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import chylex.hee.system.collections.weight.IWeightProvider;
import chylex.hee.system.collections.weight.WeightedList;
import chylex.hee.world.feature.blobs.generators.BlobGenerator;
import chylex.hee.world.feature.blobs.populators.BlobPopulator;
import chylex.hee.world.util.IRangeGenerator;
import chylex.hee.world.util.IRangeGenerator.RangeGenerator;
import chylex.hee.world.util.RandomAmount;

public final class BlobPattern implements IWeightProvider{
	private final WeightedList<BlobGenerator> generators = new WeightedList<>();
	private final WeightedList<BlobPopulator> populators = new WeightedList<>();
	private final int weight;
	
	private IRangeGenerator populatorAmount = null;
	
	public BlobPattern(int weight){
		this.weight = weight;
	}
	
	public BlobPattern addGenerator(BlobGenerator generator){
		this.generators.add(generator);
		return this;
	}
	
	public BlobPattern addGenerators(BlobGenerator[] generators){
		this.generators.add(generators);
		return this;
	}
	
	public BlobPattern addPopulator(BlobPopulator populator){
		this.populators.add(populator);
		return this;
	}
	
	public BlobPattern addPopulators(BlobPopulator[] populators){
		this.populators.add(populators);
		return this;
	}
	
	public BlobPattern setPopulatorAmount(IRangeGenerator amount){
		this.populatorAmount = amount;
		return this;
	}
	
	public BlobPattern setPopulatorAmount(int amount){
		this.populatorAmount = new RangeGenerator(amount,amount,RandomAmount.exact);
		return this;
	}
	
	public Optional<BlobGenerator> selectGenerator(Random rand){
		return generators.tryGetRandomItem(rand);
	}
	
	public List<BlobPopulator> selectPopulators(Random rand){
		int amount = (populatorAmount == null ? 0 : populatorAmount.next(rand));
		
		List<BlobPopulator> selected = new ArrayList<>(amount);
		if (amount == 0)return selected;
		
		WeightedList<BlobPopulator> available = new WeightedList<>(populators);
		
		for(int a = 0; a < amount; a++){
			BlobPopulator nextPopulator = available.removeRandomItem(rand);
			
			if (nextPopulator != null)selected.add(nextPopulator);
			else break;
		}
		
		Collections.sort(selected,(p1, p2) -> populators.indexOf(p1) < populators.indexOf(p2) ? -1 : 1);
		return selected;
	}
	
	@Override
	public int getWeight(){
		return weight;
	}
}

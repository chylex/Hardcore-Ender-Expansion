package chylex.hee.world.feature.blobs;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import chylex.hee.system.collections.weight.IWeightProvider;
import chylex.hee.system.collections.weight.WeightedList;
import chylex.hee.world.feature.blobs.generators.BlobGenerator;
import chylex.hee.world.feature.blobs.populators.BlobPopulator;
import chylex.hee.world.util.RangeGenerator;

public final class BlobPattern implements IWeightProvider{
	private final WeightedList<BlobGenerator> generators = new WeightedList<>();
	private final WeightedList<BlobPopulator> populators = new WeightedList<>();
	private final int weight;
	
	private RangeGenerator populatorAmount = null;
	
	public BlobPattern(int weight){
		this.weight = weight;
	}
	
	public void addGenerator(BlobGenerator generator){
		this.generators.add(generator);
	}
	
	public void addGenerators(BlobGenerator[] generators){
		this.generators.add(generators);
	}
	
	public void addPopulator(BlobPopulator populator){
		this.populators.add(populator);
	}
	
	public void addPopulators(BlobPopulator[] populators){
		this.populators.add(populators);
	}
	
	public void setPopulatorAmount(RangeGenerator amount){
		this.populatorAmount = amount;
	}
	
	public void addPopulators(BlobPopulator[] populators, RangeGenerator amount){
		this.populators.add(populators);
		this.populatorAmount = amount;
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
		
		return selected;
	}
	
	@Override
	public int getWeight(){
		return weight;
	}
}

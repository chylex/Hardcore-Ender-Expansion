package chylex.hee.system.collections.weight;
import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;

public class WeightedMap<T> implements IWeightedCollection<T>{
	private final TObjectIntHashMap<T> items;
	private int totalWeight;
	private boolean dirty;
	
	// Constructors
	
	public WeightedMap(){
		items = new TObjectIntHashMap<>();
	}
	
	public WeightedMap(int initialCapacity){
		items = new TObjectIntHashMap<>(initialCapacity);
	}
	
	public WeightedMap(Consumer<WeightedMap<T>> callback){
		items = new TObjectIntHashMap<>();
		callback.accept(this);
	}
	
	public WeightedMap(int initialCapacity, Consumer<WeightedMap<T>> callback){
		items = new TObjectIntHashMap<>(initialCapacity);
		callback.accept(this);
	}
	
	// Map methods
	
	public void add(T item, int weight){
		items.put(item,weight);
	}
	
	@Override
	public boolean isEmpty(){
		return items.isEmpty();
	}

	@Override
	public int size(){
		return items.size();
	}
	
	// Weight methods
	
	@Override
	public void setDirty(){
		dirty = true;
	}

	@Override
	public int getTotalWeight(){
		if (dirty){
			totalWeight = Arrays.stream(items.values()).sum();
			dirty = false;
		}
		
		return totalWeight;
	}

	@Override
	public T getRandomItem(Random rand){
		if (getTotalWeight() == 0)return null;
		int i = rand.nextInt(getTotalWeight());
		
		for(TObjectIntIterator<T> iter = items.iterator(); iter.hasNext();){
			iter.advance();
			
			i -= iter.value();
			if (i < 0)return iter.key();
		}
		
		return null;
	}

	@Override
	public T removeRandomItem(Random rand){
		T item = getRandomItem(rand);
		if (item != null)items.remove(item);
		return item;
	}
}

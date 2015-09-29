package chylex.hee.system.collections.weight;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.stream.Stream;

public class WeightedList<T extends IWeightProvider> implements IWeightedCollection<T>, Iterable<T>{
	private final ArrayList<T> items;
	private int totalWeight;
	private boolean dirty;
	
	// Constructors
	
	public WeightedList(){
		items = new ArrayList<>();
	}
	
	public WeightedList(T...itemArray){
		items = new ArrayList<>(itemArray.length);
		for(T item:itemArray)items.add(item);
		dirty = true;
	}
	
	public WeightedList(WeightedList<T> itemCollection){
		items = new ArrayList<>(itemCollection.size());
		for(T item:itemCollection)items.add(item);
		dirty = true;
	}

	public WeightedList(Collection<T> itemCollection){
		items = new ArrayList<>(itemCollection);
		dirty = true;
	}
	
	public WeightedList(int initialCapacity){
		items = new ArrayList<>(initialCapacity);
	}
	
	// List methods
	
	public void add(T item){
		items.add(item);
		dirty = true;
	}
	
	public void add(T[] itemArray){
		for(T item:itemArray)items.add(item);
		dirty = true;
	}
	
	public void add(WeightedList<T> itemCollection){
		for(T item:itemCollection)items.add(item);
		dirty = true;
	}
	
	public void add(Collection<T> itemCollection){
		for(T item:itemCollection)items.add(item);
		dirty = true;
	}
	
	public boolean remove(T item){
		if (items.remove(item)){
			dirty = true;
			return true;
		}
		else return false;
	}
	
	public int indexOf(T item){
		return items.indexOf(item);
	}
	
	@Override
	public Iterator<T> iterator(){
		return items.iterator();
	}
	
	@Override
	public boolean isEmpty(){
		return items.isEmpty();
	}
	
	@Override
	public int size(){
		return items.size();
	}
	
	public Stream<T> stream(){
		return items.stream();
	}
	
	// Weight methods
	
	@Override
	public void setDirty(){
		dirty = true;
	}
	
	@Override
	public int getTotalWeight(){
		if (dirty){
			totalWeight = items.stream().mapToInt(obj -> obj.getWeight()).sum();
			dirty = false;
		}
		
		return totalWeight;
	}
	
	@Override
	public T getRandomItem(Random rand){
		if (getTotalWeight() == 0)return null;
		int i = rand.nextInt(getTotalWeight());
		
		for(T obj:this){
			i -= obj.getWeight();
			if (i < 0)return obj;
		}
		
		return null;
	}
	
	@Override
	public T removeRandomItem(Random rand){
		T item = getRandomItem(rand);
		
		if (item != null){
			items.remove(item);
			dirty = true;
		}
		
		return item;
	}
	
	// Object methods
	
	@Override
	public boolean equals(Object obj){
		return obj instanceof WeightedList && ((WeightedList)obj).items.equals(items);
	}
	
	@Override
	public int hashCode(){
		return items.hashCode();
	}
	
	@Override
	public String toString(){
		return items.toString();
	}
}
package chylex.hee.system.collections;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.ToDoubleFunction;

public final class CollectionUtil{
	public static <K, V extends Comparable<? super V>> SortedSet<Entry<K, V>> sortMapByValueAsc(Map<K, V> map){
		SortedSet<Entry<K, V>> sorted = new TreeSet<>(
			new Comparator<Entry<K, V>>(){
				@Override public int compare(Entry<K, V> e1, Entry<K, V> e2){
					int r = e1.getValue().compareTo(e2.getValue());
					return r == 0 ? 1 : r;
				}
			}
		);
		
		for(Entry<K, V> entry:map.entrySet())sorted.add(new SimpleEntry<>(entry));
		return sorted;
	}

	public static <K, V extends Comparable<? super V>> SortedSet<Entry<K, V>> sortMapByValueDesc(Map<K, V> map){
		SortedSet<Entry<K, V>> sorted = new TreeSet<>(
			new Comparator<Entry<K, V>>(){
				@Override public int compare(Entry<K, V> e1, Entry<K, V> e2){
					int r = e2.getValue().compareTo(e1.getValue());
					return r == 0 ? 1 : r;
				}
			}
		);
		
		for(Entry<K, V> entry:map.entrySet())sorted.add(new SimpleEntry<>(entry));
		return sorted;
	}
	
	public static <T> Optional<T> min(List<T> list, ToDoubleFunction<T> toDouble){
		T minObj = null;
		double minValue = Double.MAX_VALUE;
		
		for(T obj:list){
			double value = toDouble.applyAsDouble(obj);
			
			if (value < minValue){
				minObj = obj;
				minValue = value;
			}
		}
		
		return Optional.ofNullable(minObj);
	}
	
	public static <T> Optional<T> get(T[] array, int index){
		return index >= 0 && index < array.length ? Optional.ofNullable(array[index]) : Optional.empty();
	}
	
	public static <T> Optional<T> get(List<T> list, int index){
		return index >= 0 && index < list.size() ? Optional.ofNullable(list.get(index)) : Optional.empty();
	}
	
	public static <T> T getClamp(T[] array, int index){
		return array[Math.min(Math.max(index, 0), array.length-1)];
	}
	
	public static <T> T getClamp(List<T> list, int index){
		return list.get(Math.min(Math.max(index, 0), list.size()-1));
	}
	
	public static <T> ArrayList<T> newList(T...elements){
		ArrayList<T> list = new ArrayList<>(elements.length);
		Collections.addAll(list, elements);
		return list;
	}
	
	public static <T> ArrayList<T> newList(int capacity, T...elements){
		ArrayList<T> list = new ArrayList<>(capacity);
		Collections.addAll(list, elements);
		return list;
	}
	
	public static <T> HashSet<T> newSet(T...elements){
		HashSet<T> list = new HashSet<>(elements.length);
		Collections.addAll(list, elements);
		return list;
	}
	
	public static <T> RandomList<T> shuffled(List<T> list, Random rand){
		return new RandomList<>(list, rand);
	}
	
	public static <T> Optional<T> random(List<T> list, Random rand){
		return list.isEmpty() ? Optional.empty() : Optional.ofNullable(list.get(rand.nextInt(list.size())));
	}
	
	public static <T> T randomOrNull(List<T> list, Random rand){
		return list.isEmpty() ? null : list.get(rand.nextInt(list.size()));
	}
	
	private CollectionUtil(){}
}

package chylex.hee.system.util;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;

public final class CollectionUtil{
	public static <K,V extends Comparable<? super V>> SortedSet<Entry<K,V>> sortMapByValueAsc(Map<K,V> map){
		SortedSet<Entry<K,V>> sorted = new TreeSet<>(
			new Comparator<Entry<K,V>>(){
				@Override public int compare(Entry<K,V> e1, Entry<K,V> e2){
					int r = e1.getValue().compareTo(e2.getValue());
					return r == 0 ? 1 : r;
				}
			}
		);
		
		for(Entry<K,V> entry:map.entrySet())sorted.add(new SimpleEntry(entry));
		return sorted;
	}

	public static <K,V extends Comparable<? super V>> SortedSet<Entry<K,V>> sortMapByValueDesc(Map<K,V> map){
		SortedSet<Entry<K,V>> sorted = new TreeSet<>(
			new Comparator<Entry<K,V>>(){
				@Override public int compare(Entry<K,V> e1, Entry<K,V> e2){
					int r = e2.getValue().compareTo(e1.getValue());
					return r == 0 ? 1 : r;
				}
			}
		);
		
		for(Entry<K,V> entry:map.entrySet())sorted.add(new SimpleEntry(entry));
		return sorted;
	}
	
	public static <T> ArrayList<T> newList(T...elements){
		ArrayList<T> list = new ArrayList<>(elements.length);
		Collections.addAll(list,elements);
		return list;
	}
	
	public static <T> ArrayList<T> newList(int capacity, T...elements){
		ArrayList<T> list = new ArrayList<>(capacity);
		Collections.addAll(list,elements);
		return list;
	}
	
	public static <T> HashSet<T> newSet(T...elements){
		HashSet<T> list = new HashSet<>(elements.length);
		Collections.addAll(list,elements);
		return list;
	}
	
	public static <T> List<T> shuffleMe(List<T> list, Random rand){
		Collections.shuffle(list,rand);
		return list;
	}
	
	private CollectionUtil(){}
}

package chylex.hee.system.util;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

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
	
	private CollectionUtil(){}
}

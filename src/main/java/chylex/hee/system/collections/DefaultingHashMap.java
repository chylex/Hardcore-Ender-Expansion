package chylex.hee.system.collections;
import java.util.HashMap;

public class DefaultingHashMap<K,V> extends HashMap<K,V>{
	private final V defaultValue;
	
	public DefaultingHashMap(V defaultValue){
		this.defaultValue = defaultValue;
	}
	
	public DefaultingHashMap(int initialCapacity, V defaultValue){
		super(initialCapacity);
		this.defaultValue = defaultValue;
	}
	
	/**
	 * Returns the value to which the specified key is mapped, or the default value if this map contains no mapping for the key.
	 */
	@Override
	public V get(Object key){
		V value = super.get(key);
		return value == null ? defaultValue : value;
	}
}

package chylex.hee.system.collections;
import gnu.trove.map.hash.TObjectLongHashMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * A set where entries expire a set amount of time after being added. The implementation uses a trove hash map to store the data.
 */
public class ExpiringSet<E> extends AbstractSet<E>{
	private static final long toMillis(long nanos){
		return TimeUnit.NANOSECONDS.toMillis(nanos);
	}
	
	private final TObjectLongHashMap<E> internal;
	private long expireAfterMillis = -1L;
	private long oldestEntry = Long.MAX_VALUE;
	
	public ExpiringSet(){
		this.internal = new TObjectLongHashMap<>();
	}
	
	public ExpiringSet(int internalCapacity){
		this.internal = new TObjectLongHashMap<>(internalCapacity);
	}
	
	public void setExpiryTime(long millis){
		this.expireAfterMillis = millis;
	}
	
	public void setExpireNever(){
		this.expireAfterMillis = -1L;
	}
	
	private void refresh(){
		if (toMillis(System.nanoTime()-oldestEntry) < expireAfterMillis || expireAfterMillis == -1L)return;
		
		oldestEntry = Long.MAX_VALUE;
		long now = System.nanoTime();
		
		for(Iterator<E> iter = iterator(); iter.hasNext();){
			long time = internal.get(iter.next());
			
			if (toMillis(now-time) >= expireAfterMillis)iter.remove();
			else if (time < oldestEntry)oldestEntry = time;
		}
	}
	
	/**
	 * Returns true if the element is present and has expired.
	 */
	public boolean expired(E element){
		if (expireAfterMillis == -1L)return false;
		
		long time = internal.get(element);
		return time != internal.getNoEntryValue() && toMillis(System.nanoTime()-time) >= expireAfterMillis;
	}
	
	/**
	 * If the element is missing, it is added to the set and the function returns true.
	 * If the element is present but expired, the time is reset and the function returns true.
	 * Otherwise, the function returns false.
	 */
	public boolean update(E element){
		if (!internal.contains(element) || expired(element)){
			add(element);
			return true;
		}
		else return false;
	}
	
	@Override
	public boolean add(E e){
		long time = System.nanoTime();
		if (time < oldestEntry)oldestEntry = time;
		if (expired(e))internal.remove(e);
		return internal.put(e,time) == internal.getNoEntryValue();
	}

	@Override
	public boolean contains(Object o){
		refresh();
		return internal.containsKey(o);
	}

	@Override
	public boolean remove(Object o){
		refresh();
		return internal.remove(o) != internal.getNoEntryValue();
	}

	@Override
	public Iterator<E> iterator(){
		refresh();
		return internal.keySet().iterator();
	}

	@Override
	public int size(){
		refresh();
		return internal.size();
	}

	@Override
	public void clear(){
		internal.clear();
	}
	
	@Override
	public boolean equals(Object obj){
		refresh();
		return obj instanceof ExpiringSet && ((ExpiringSet)obj).internal.keySet().equals(internal.keySet());
	}

	@Override
	public int hashCode(){
		refresh();
		return super.hashCode();
	}
}

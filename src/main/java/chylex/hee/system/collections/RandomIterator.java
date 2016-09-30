package chylex.hee.system.collections;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.lang3.ArrayUtils;

public class RandomIterator<T> implements Iterator<T>{
	private static final <T> List<T> shuffleMe(List<T> list, Random rand){
		Collections.shuffle(list, rand);
		return list;
	}
	
	private final List<T> collection;
	private final int[] indexes;
	private int position;
	
	RandomIterator(List<T> collection, Random rand){
		this.collection = collection;
		this.indexes = ArrayUtils.toPrimitive(shuffleMe(IntStream.range(0, collection.size()).boxed().collect(Collectors.toList()), rand).toArray(new Integer[collection.size()]));
		// sorry
	}
	
	@Override
	public boolean hasNext(){
		return position < indexes.length;
	}

	@Override
	public T next(){
		return collection.get(position++);
	}
}

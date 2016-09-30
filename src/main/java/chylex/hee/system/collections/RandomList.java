package chylex.hee.system.collections;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class RandomList<T> implements Iterable<T>{
	private final List<T> collection;
	private final Random rand;
	
	public RandomList(List<T> collection, Random rand){
		this.collection = new ArrayList<>(collection);
		this.rand = rand;
	}
	
	@Override
	public Iterator<T> iterator(){
		return new RandomIterator<>(collection, rand);
	}
}

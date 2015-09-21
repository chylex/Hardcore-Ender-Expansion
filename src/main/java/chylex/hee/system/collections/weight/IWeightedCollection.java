package chylex.hee.system.collections.weight;
import java.util.Optional;
import java.util.Random;

public interface IWeightedCollection<T>{
	boolean isEmpty();
	int size();
	
	void setDirty();
	int getTotalWeight();
	
	T getRandomItem(Random rand);
	T removeRandomItem(Random rand);
	
	default Optional<T> tryGetRandomItem(Random rand){
		return Optional.ofNullable(getRandomItem(rand));
	}
	
	default Optional<T> tryRemoveRandomItem(Random rand){
		return Optional.ofNullable(removeRandomItem(rand));
	}
}

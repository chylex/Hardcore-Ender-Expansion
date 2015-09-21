package chylex.hee.system.collections.weight;

@Deprecated
public class ObjectWeightPair<T> implements IWeightProvider{
	@Deprecated
	public static <T> ObjectWeightPair<T> of(T obj, int weight){
		return new ObjectWeightPair<>(obj,weight);
	}
	
	private T obj;
	private int weight;
	
	private ObjectWeightPair(T obj, int weight){
		this.obj = obj;
		this.weight = weight;
	}
	
	public T getObject(){
		return obj;
	}

	@Override
	public int getWeight(){
		return weight;
	}
}

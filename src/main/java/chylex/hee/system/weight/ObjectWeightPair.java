package chylex.hee.system.weight;

public class ObjectWeightPair<T> implements IWeightProvider{
	public static <T> ObjectWeightPair<T> make(T obj, int weight){
		return new ObjectWeightPair<T>(obj,weight);
	}
	
	private T obj;
	private int weight;
	
	public ObjectWeightPair(T obj, int weight){
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

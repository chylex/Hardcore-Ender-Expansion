package chylex.hee.system.weight;

public class ObjectWeightPair<T> implements IWeightProvider{
	private T obj;
	private short weight;
	
	public ObjectWeightPair(T obj, int weight){
		this.obj = obj;
		this.weight = (short)weight;
	}
	
	public T getObject(){
		return obj;
	}

	@Override
	public short getWeight(){
		return weight;
	}
}

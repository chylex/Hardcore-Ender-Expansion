package chylex.hee.system.weight;
import java.util.ArrayList;
import java.util.Random;

public class WeightedList<T extends IWeightProvider> extends ArrayList<T>{
	private static final long serialVersionUID = -382485527777212023L;
	
	private int totalWeight = 0;
	
	public WeightedList(T...weightedItems){
		for(T item:weightedItems)add(item);
	}

	@Override
	public boolean add(T obj){
		boolean b = super.add(obj);
		recalculateWeight();
		return b;
	}
	
	public boolean addAll(T[] obj){
		
		return true;
	}
	
	@Override
	public T remove(int index){
		T is = super.remove(index);
		recalculateWeight();
		return is;
	}
	
	@Override
	public boolean remove(Object o){
		boolean b = super.remove(o);
		recalculateWeight();
		return b;
	}
	
	protected void recalculateWeight(){
		totalWeight = 0;
		for(T obj:this)totalWeight += obj.getWeight();
	}
	
	public T getRandomItem(Random rand){
		if (totalWeight == 0)return null;
		int i = rand.nextInt(totalWeight);
		
		for(T obj:this){
			i -= obj.getWeight();
			if (i < 0)return obj;
		}
		
		return null;
	}
}

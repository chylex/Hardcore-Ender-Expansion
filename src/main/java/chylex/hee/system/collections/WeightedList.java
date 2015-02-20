package chylex.hee.system.collections;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import chylex.hee.system.collections.weight.IWeightProvider;
import chylex.hee.system.collections.weight.ObjectWeightPair;
import chylex.hee.system.commands.HeeDebugCommand.HeeTest;

public class WeightedList<T extends IWeightProvider> extends ArrayList<T>{
	private static final long serialVersionUID = -382485527777212023L;
	
	protected int totalWeight;
	
	public WeightedList(T...weightedItems){
		for(T item:weightedItems)add(item);
	}

	public WeightedList(WeightedList<T> weightedItemCollection){
		for(T item:weightedItemCollection)add(item);
	}

	@Override
	public boolean add(T obj){
		boolean b = super.add(obj);
		recalculateWeight();
		return b;
	}
	
	public WeightedList<T> addAll(T[] objArray){
		for(T obj:objArray)super.add(obj);
		recalculateWeight();
		return this;
	}
	
	@Override
	public boolean addAll(Collection<? extends T> collection){
		boolean b = super.addAll(collection);
		recalculateWeight();
		return b;
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
	
	public void recalculateWeight(){
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
	
	public T removeRandomItem(Random rand){
		T item = getRandomItem(rand);
		this.remove(item);
		return item;
	}
	
	public static final HeeTest $debugTest = new HeeTest(){
		@Override
		public void run(String...args){
			WeightedList<ObjectWeightPair<String>> list = new WeightedList<>();
			list.add(ObjectWeightPair.of("A",50));
			list.add(ObjectWeightPair.of("B",25));
			list.add(ObjectWeightPair.of("C",10));
			list.add(ObjectWeightPair.of("D",5));
			list.add(ObjectWeightPair.of("E",5));
			list.add(ObjectWeightPair.of("F",1));
			
			TObjectIntHashMap<String> freq = new TObjectIntHashMap<>();
			for(int a = 0; a < 5000; a++)freq.adjustOrPutValue(list.getRandomItem(world.rand).getObject(),1,1);
			
			for(String key:freq.keySet())System.out.println(key+" ... "+freq.get(key));
		}
	};
}
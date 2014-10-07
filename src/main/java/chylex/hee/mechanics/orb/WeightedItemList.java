package chylex.hee.mechanics.orb;
import chylex.hee.system.weight.WeightedList;

public class WeightedItemList extends WeightedList<WeightedItem>{
	private static final long serialVersionUID = -5274470224046379374L;
	
	@Override
	public boolean add(WeightedItem obj){
		for(int a = 0, size = size(); a < size; a++){
			WeightedItem item = get(a);
			
			if (item.getItem() == obj.getItem()){
				remove(a);
				
				if (item.getWeight() > obj.getWeight())obj.setWeight(item.getWeight());
				obj.combineDamageValues(item);
				
				break;
			}
		}
		
		boolean b = super.add(obj);
		recalculateWeight();
		return b;
	}
}
